package com.github.notfb.sla;

import static org.junit.Assert.assertEquals;

import javax.annotation.Resource;

import org.junit.Test;

/**
 * Only tests SLA annotated methods.
 */
public class MethodAnnotatedTest extends MeasurementTest {

	@Resource(name = "methodAnnotatedBean")
	protected MethodAnnotatedBean monitoredBean;

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