package fb.local.sla;

import static org.junit.Assert.assertEquals;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import etm.core.renderer.SimpleTextRenderer;

/**
 * Only tests SLA annotated methods.
 */
public class ClassAnnotatedTest extends MeasurementTest {

	@Resource(name = "methodAnnotatedBean")
	protected MethodAnnotatedBean monitoredBean;

    @Before
	public void setUp() throws Exception {
		measurementAspect.reset();
		assertEquals(0, measurementAspect.getErrors());
		assertEquals(0, measurementAspect.getWarnings());
	}

	@After
	public void tearDown() throws Exception {
		etmMonitor.render(new SimpleTextRenderer());
		etmMonitor.reset();
	}

	@Test
	public void testOk() {
		monitoredBean.ok();
		assertEquals(0, measurementAspect.getErrors());
		assertEquals(0, measurementAspect.getWarnings());
	}

	@Test
	public void testWarn() throws InterruptedException {
		monitoredBean.warn();
		assertEquals(0, measurementAspect.getErrors());
		assertEquals(1, measurementAspect.getWarnings());

		monitoredBean.warn();
		assertEquals(0, measurementAspect.getErrors());
		assertEquals(2, measurementAspect.getWarnings());
	}

	@Test
	public void testError() throws InterruptedException {
		monitoredBean.error("foo");
		assertEquals(0, measurementAspect.getWarnings());
		assertEquals(1, measurementAspect.getErrors());

		monitoredBean.error("foo");
		assertEquals(0, measurementAspect.getWarnings());
		assertEquals(2, measurementAspect.getErrors());

		// check if AOP and reflection code is working
		monitoredBean.error("foo", new Integer(2));
		assertEquals(0, measurementAspect.getWarnings());
		assertEquals(3, measurementAspect.getErrors());

		monitoredBean.error("foo", 0);
		assertEquals(0, measurementAspect.getWarnings());
		assertEquals(4, measurementAspect.getErrors());
	}

}