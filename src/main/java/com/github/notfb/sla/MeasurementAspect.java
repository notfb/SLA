package com.github.notfb.sla;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.concurrent.ThreadSafe;

import net.jcip.examples.Computable;
import net.jcip.examples.Memoizer;

import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.InitializingBean;

import etm.core.configuration.EtmManager;
import etm.core.configuration.XmlEtmConfigurator;
import etm.core.monitor.EtmMonitor;
import etm.core.monitor.EtmPoint;

/**
 * <p>
 * Aspect to monitor runtime performance of <code>@SLA</code> annotated classes
 * and methods. Inherited annotations are ignored. If a Service Level Agreement
 * (SLA) violation occurs the error or warning count get's increased and a
 * message is logged. This aspect does <b>not</b> care how a method finishes
 * -&gt; returning normally or throwing an exception triggers the same behavior
 * regarding performance monitoring.
 * </p>
 * 
 * <p>
 * JETM is used to do the performance monitoring.
 * </p>
 * 
 * <pre>
 * TODO: where to configure JETM? - atm it is in afterPropertiesSet()
 * TODO: increase test coverage
 * TODO: aop xml config, hot deploy new spring context?
 * </pre>
 *
 * @see com.github.notfb.sla.SLA
 */
@Aspect
@ThreadSafe
public class MeasurementAspect implements InitializingBean {

	/** count of expected concurrent updating threads for the caching hashmap */
	protected static final int CC_UPDATERS = 16;

	/** simple value object for SLA timing values */
	private static class SLAValues {
		public final double warnMs;
		public final double errorMs;
		public final String name;

		public SLAValues(double warnMs, double errorMs, String name) {
			super();
			this.warnMs = warnMs;
			this.errorMs = errorMs;
			this.name = name;
		}
	}

	private String jetmConfig = "/jetm-config.xml";

	private final EtmMonitor etmMonitor = EtmManager.getEtmMonitor();

	private final AtomicInteger warnings = new AtomicInteger(0);
	private final AtomicInteger errors = new AtomicInteger(0);

	private final Memoizer<ProceedingJoinPoint, SLAValues, String> cache;

	private static final Logger logger = Logger
			.getLogger(MeasurementAspect.class);

	public MeasurementAspect() {

		final Computable<ProceedingJoinPoint, SLAValues> c;
		c = new Computable<ProceedingJoinPoint, SLAValues>() {
			public SLAValues compute(ProceedingJoinPoint pjp) {
				return getSLAUncached(pjp);
			}
		};

		final Computable<ProceedingJoinPoint, String> hash;
		hash = new Computable<ProceedingJoinPoint, String>() {

			public String compute(ProceedingJoinPoint pjp) {
				// TODO: find something better to use than pjp.toLongString
				return pjp.toLongString();

			}
		};

		cache = new Memoizer<ProceedingJoinPoint, SLAValues, String>(c, hash,
				64, 0.75f, CC_UPDATERS);
	}

	public void afterPropertiesSet() {
		XmlEtmConfigurator.configure(MeasurementAspect.class
				.getResourceAsStream(jetmConfig));
		logger.info("initialized (from " + jetmConfig + ")");
	}

    /** Get the location of the JETM config file */
	public String getJetmConfig() {
		return jetmConfig;
	}

    /** Set the location of the JETM config file */
	public void setJetmConfig(String jetmConfig) {
		this.jetmConfig = jetmConfig;
	}

	/** reset warn and error counters */
	public void reset() {
		errors.set(0);
		warnings.set(0);
	}

	public int getWarnings() {
		return warnings.get();
	}

	public int getErrors() {
		return errors.get();
	}

    // FIXME: unify advices to avoid matching twice!
	@Around("@annotation(SLA) || (execution(* *(..)) && within(@SLA *))")
    //@Around("execution(* *(..)) && (within(@SLA *) || @annotation(SLA))")
	public Object monitor(ProceedingJoinPoint pjp) throws Throwable {

		EtmPoint point = null;
		SLAValues sla = null;

		try {
			sla = getSLA(pjp);
			point = etmMonitor.createPoint(sla.name);
		} catch (RuntimeException e) {
			logger.warn("Caught exception within AOP advice measurement code",
					e);
			// yes, eat exception to avoid causing errors in the "real" system
		}

		try {
			return pjp.proceed();
		} finally {
            collectPoint(point, sla);
        }

	}

    protected void collectPoint(EtmPoint point, SLAValues sla) {
        if (point != null && sla != null) {
            point.collect();
            double timeMs = point.getTransactionTime();
            checkSLA(sla.warnMs, sla.errorMs, sla.name, timeMs);
        }
    }

	protected SLAValues getSLA(ProceedingJoinPoint pjp) {
		try {
			return cache.compute(pjp);
		} catch (InterruptedException e) {
			logger.info("interrupted while searching SLA annotation for "
					+ "join point", e);
			return getSLAUncached(pjp);
		}
	}

	protected SLAValues getSLAUncached(ProceedingJoinPoint pjp) {
		final String pjpName = pjp.toLongString();
		if (logger.isDebugEnabled()) {
			logger.debug("getSLAUncached(" + pjpName + ")");
		}

		final Method method = getTargetMethod(pjp);
		final Annotation[] annotations = method.getDeclaredAnnotations();
		SLAValues slaValues = null;

		for (Annotation a : annotations) {
			if (a instanceof SLA) {
				slaValues = getSLAValues((SLA) a, mkPointName(pjp
						.getSignature().getDeclaringType(), method));
			}
		}
        
        if (slaValues == null) {
            logger.debug("no method level annotation found - trying class ...");
            slaValues = getSLAUncachedFromClass(pjp);
        }

		if (slaValues == null) {
			logger.error("FAILED to locate @SLA annotation for join "
					+ "point method (checked method and class) "
                    + "- join point: " + pjpName);
		} else if (slaValues.warnMs > slaValues.errorMs) {
			logger.warn("@SLA with warning time > error time at join point "
					+ pjpName);
		}

		return slaValues;
	}

    protected SLAValues getSLAUncachedFromClass(ProceedingJoinPoint pjp) {

        final Class<?> clazz = pjp.getSignature().getDeclaringType();
        if (logger.isDebugEnabled()) {
            logger.debug("getSLAUncachedFromClass - using class " + clazz.getName());
        }
        final Annotation[] annotations = clazz.getDeclaredAnnotations();
        SLAValues slaValues = null;

        for (Annotation a : annotations) {
            if (a instanceof SLA) {
                String pointName = mkPointName(clazz, getTargetMethod(pjp));
                slaValues = getSLAValues((SLA) a, pointName);
            }
        }

        return slaValues;
    }

    private String mkPointName(Class<?> clazz, Method method) {
		final StringBuilder sb = new StringBuilder(clazz.getSimpleName());
		sb.append('.').append(method.getName());
		Class<?>[] types = method.getParameterTypes();
		sb.append("(");

		// skip last iteration to avoid having an "if" in the for loop
		for (int i = 0; i < types.length - 1; i++) {
			sb.append(types[i].getSimpleName()).append(", ");
		}
		if (types.length > 0) {
			sb.append(types[types.length - 1].getSimpleName());
		}
		sb.append(")");

		return sb.toString();
	}

	private SLAValues getSLAValues(SLA sla, String name) {
		// Note: converting from long to double here
		final double warn = TimeUnit.MILLISECONDS.convert(sla.warn(), sla
				.unit());
		final double error = TimeUnit.MILLISECONDS.convert(sla.error(), sla
				.unit());

		logger.debug(String.format("got warn=%f, error=%f", warn, error));
		return new SLAValues(warn, error, name);
	}

	private Method getTargetMethod(ProceedingJoinPoint pjp) {
        if (logger.isDebugEnabled()) {
		    logger.debug("kind=" + pjp.getKind() + ", static="
				    + pjp.getStaticPart());
        }
		Signature sig = pjp.getSignature();
		if (sig instanceof MethodSignature) {
			return ((MethodSignature) sig).getMethod();
		} else {
			String name = sig.getClass().getName();
			logger.error("unsupported signature type : " + name);
			throw new IllegalStateException(
					"unsupported join point signature type: " + name);
		}
	}

	// used for unit testing as well
	protected void checkSLA(double warnMs, double errorMs, String name,
			double timeMs) {

		// opt.: quick return for default case if (timeMs < warnMs) { return;} else
		if (timeMs >= errorMs) {
			logger.error(String.format("@SLA.error %.1fms >= %.1fms - in: %s",
					timeMs, errorMs, name));
			errors.incrementAndGet();
		} else if (timeMs >= warnMs) {
			logger.warn(String.format("@SLA.warning %.1fms >= %.1fms - in: %s",
					timeMs, warnMs, name));
			warnings.incrementAndGet();
		}
	}

}
