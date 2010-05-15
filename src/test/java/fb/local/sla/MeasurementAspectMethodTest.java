package fb.local.sla;

import static org.junit.Assert.assertEquals;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import edu.emory.mathcs.backport.java.util.concurrent.TimeUnit;

import etm.core.configuration.EtmManager;
import etm.core.monitor.EtmMonitor;
import etm.core.renderer.SimpleTextRenderer;

/**
 * Only tests SLA annotated methods, a class annotation is tested in
 * MeasurementAspectClassTest.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "testSLAContext.xml" })
public class MeasurementAspectMethodTest {

	/** Minimum accuracy (or delta) for double operations */
	private static final double MIN_ACC = 0.00001d;

	@Resource
	protected MeasurementAspect slaMonitorBean;

	@Resource(name = "monitoredBean")
	protected AnnotatedBean monitoredBean;

	protected EtmMonitor etmMonitor = EtmManager.getEtmMonitor();

	@Before
	public void setUp() throws Exception {
		//etmMonitor.start();
		slaMonitorBean.reset();
		assertEquals(0, slaMonitorBean.getErrors());
		assertEquals(0, slaMonitorBean.getWarnings());
	}

	@After
	public void tearDown() throws Exception {
		etmMonitor.render(new SimpleTextRenderer());
		//etmMonitor.stop();
		etmMonitor.reset();
	}

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

		slaMonitorBean.checkSLA(500, 1000, "test", 200.1);
		assertEquals(0, slaMonitorBean.getWarnings());
		assertEquals(0, slaMonitorBean.getErrors());

		slaMonitorBean.checkSLA(500, 1000, "test", 500.1);
		assertEquals(1, slaMonitorBean.getWarnings());
		assertEquals(0, slaMonitorBean.getErrors());
		slaMonitorBean.reset();

		slaMonitorBean.checkSLA(500, 1000, "test", 1000.1);
		assertEquals(0, slaMonitorBean.getWarnings());
		assertEquals(1, slaMonitorBean.getErrors());
		slaMonitorBean.reset();

		// does'nt really make sense - warn > error
		slaMonitorBean.checkSLA(5000, 1000, "testStrange", 200.1);
		assertEquals(0, slaMonitorBean.getWarnings());
		assertEquals(0, slaMonitorBean.getErrors());
		slaMonitorBean.reset();

		slaMonitorBean.checkSLA(5000, 1000, "testStrange", 2000.1);
		assertEquals(0, slaMonitorBean.getWarnings());
		assertEquals(1, slaMonitorBean.getErrors());
		slaMonitorBean.reset();

		slaMonitorBean.checkSLA(5000, 1000, "testStrange", 20000.1);
		assertEquals(0, slaMonitorBean.getWarnings());
		assertEquals(1, slaMonitorBean.getErrors());
		slaMonitorBean.reset();
	}

	@Test
	public void testOk() {
		monitoredBean.ok();
		assertEquals(0, slaMonitorBean.getErrors());
		assertEquals(0, slaMonitorBean.getWarnings());
	}

	@Test
	public void testWarn() throws InterruptedException {
		monitoredBean.warn();
		assertEquals(0, slaMonitorBean.getErrors());
		assertEquals(1, slaMonitorBean.getWarnings());

		monitoredBean.warn();
		assertEquals(0, slaMonitorBean.getErrors());
		assertEquals(2, slaMonitorBean.getWarnings());
	}

	@Test
	public void testError() throws InterruptedException {
		monitoredBean.error("foo");
		assertEquals(0, slaMonitorBean.getWarnings());
		assertEquals(1, slaMonitorBean.getErrors());

		monitoredBean.error("foo");
		assertEquals(0, slaMonitorBean.getWarnings());
		assertEquals(2, slaMonitorBean.getErrors());

		// check if AOP and reflection code is working
		monitoredBean.error("foo", new Integer(2));
		assertEquals(0, slaMonitorBean.getWarnings());
		assertEquals(3, slaMonitorBean.getErrors());

		monitoredBean.error("foo", 0);
		assertEquals(0, slaMonitorBean.getWarnings());
		assertEquals(4, slaMonitorBean.getErrors());
	}

}