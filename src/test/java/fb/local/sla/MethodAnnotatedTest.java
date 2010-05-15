package fb.local.sla;

import static org.junit.Assert.assertEquals;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import etm.core.configuration.EtmManager;
import etm.core.monitor.EtmMonitor;
import etm.core.renderer.SimpleTextRenderer;

/**
 * Only tests SLA annotated methods.
 */
public class MethodAnnotatedTest extends MeasurementTest {

	@Resource
	protected MethodAnnotatedBean methodAnnotatedBean;

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
		methodAnnotatedBean.ok();
		assertEquals(0, measurementAspect.getErrors());
		assertEquals(0, measurementAspect.getWarnings());
	}

	@Test
	public void testWarn() throws InterruptedException {
		methodAnnotatedBean.warn();
		assertEquals(0, measurementAspect.getErrors());
		assertEquals(1, measurementAspect.getWarnings());

		methodAnnotatedBean.warn();
		assertEquals(0, measurementAspect.getErrors());
		assertEquals(2, measurementAspect.getWarnings());
	}

	@Test
	public void testError() throws InterruptedException {
		methodAnnotatedBean.error("foo");
		assertEquals(0, measurementAspect.getWarnings());
		assertEquals(1, measurementAspect.getErrors());

		methodAnnotatedBean.error("foo");
		assertEquals(0, measurementAspect.getWarnings());
		assertEquals(2, measurementAspect.getErrors());

		// check if AOP and reflection code is working
		methodAnnotatedBean.error("foo", new Integer(2));
		assertEquals(0, measurementAspect.getWarnings());
		assertEquals(3, measurementAspect.getErrors());

		methodAnnotatedBean.error("foo", 0);
		assertEquals(0, measurementAspect.getWarnings());
		assertEquals(4, measurementAspect.getErrors());
	}

}