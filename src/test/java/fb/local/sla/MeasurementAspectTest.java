package fb.local.sla;

import edu.emory.mathcs.backport.java.util.concurrent.TimeUnit;
import etm.core.configuration.EtmManager;
import etm.core.monitor.EtmMonitor;
import etm.core.renderer.SimpleTextRenderer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

import static org.junit.Assert.assertEquals;

/** unit test some aspect methods */
public class MeasurementAspectTest extends MeasurementTest {

    /** Minimum accuracy (or delta) for double operations */
	private static final double MIN_ACC = 0.00001d;

	/**
	 * just to make sure we do not get to inaccurate when converting times.
	 */
	@Test
	public void testLongToDouble() {
		convert(100, TimeUnit.MINUTES);

		convert(10, TimeUnit.HOURS);
		convert(100, TimeUnit.HOURS);

		convert(10, TimeUnit.DAYS);
		convert(100, TimeUnit.DAYS);
		convert(1000, TimeUnit.DAYS);
		convert(5413, TimeUnit.DAYS);
		convert(999999999999999999L, TimeUnit.DAYS);
		//convert(Long.MAX_VALUE, TimeUnit.DAYS);

	}

	private void convert(long n, TimeUnit unit) {
		double d = TimeUnit.MILLISECONDS.convert(n, unit);
		//System.out.println("d = "+d);
		long l = TimeUnit.MILLISECONDS.convert(n, unit);
		assertEquals(d, (double)l, MIN_ACC);
	}

	@Test
	public void testCheckSLA() {

		// time values are in ms

		measurementAspect.checkSLA(500, 1000, "test", 200.1);
		assertEquals(0, measurementAspect.getWarnings());
		assertEquals(0, measurementAspect.getErrors());

		measurementAspect.checkSLA(500, 1000, "test", 500.1);
		assertEquals(1, measurementAspect.getWarnings());
		assertEquals(0, measurementAspect.getErrors());
		measurementAspect.reset();

		measurementAspect.checkSLA(500, 1000, "test", 1000.1);
		assertEquals(0, measurementAspect.getWarnings());
		assertEquals(1, measurementAspect.getErrors());
		measurementAspect.reset();

		// does'nt really make sense - warn > error
		measurementAspect.checkSLA(5000, 1000, "testStrange", 200.1);
		assertEquals(0, measurementAspect.getWarnings());
		assertEquals(0, measurementAspect.getErrors());
		measurementAspect.reset();

		measurementAspect.checkSLA(5000, 1000, "testStrange", 2000.1);
		assertEquals(0, measurementAspect.getWarnings());
		assertEquals(1, measurementAspect.getErrors());
		measurementAspect.reset();

		measurementAspect.checkSLA(5000, 1000, "testStrange", 20000.1);
		assertEquals(0, measurementAspect.getWarnings());
		assertEquals(1, measurementAspect.getErrors());
		measurementAspect.reset();
	}
}
