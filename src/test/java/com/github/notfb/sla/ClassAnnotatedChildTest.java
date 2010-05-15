package com.github.notfb.sla;

import org.junit.Before;
import org.junit.Test;

import javax.annotation.Resource;

import static org.junit.Assert.assertEquals;

/**
 * Test non-annotated subclass of an annotated class.
 */
public class ClassAnnotatedChildTest extends MeasurementTest {

	@Resource(name = "classAnnotatedChildBean")
	protected ClassAnnotatedChildBean monitoredBean;

    @Before
    public void checkNoWarnError() {
        assertEquals(0, measurementAspect.getErrors());
		assertEquals(0, measurementAspect.getWarnings());
    }

	@Test
	public void testOk() {
		monitoredBean.ok();
		assertEquals(0, measurementAspect.getErrors());
		assertEquals(0, measurementAspect.getWarnings());
	}

	@Test
	public void testWarn() throws InterruptedException {
        // overridden ... no annotation
		monitoredBean.warn();
		assertEquals(0, measurementAspect.getErrors());
		assertEquals(0, measurementAspect.getWarnings());
	}

	@Test
	public void testError() throws InterruptedException {
		monitoredBean.error();
		assertEquals(0, measurementAspect.getWarnings());
		assertEquals(1, measurementAspect.getErrors());

		monitoredBean.error();
		assertEquals(0, measurementAspect.getWarnings());
		assertEquals(2, measurementAspect.getErrors());
	}

    @Test
	public void testChildWarn() throws InterruptedException {
        monitoredBean.childWarn();
		assertEquals(0, measurementAspect.getErrors());
		assertEquals(0, measurementAspect.getWarnings());
	}

    @Test
	public void testChildOk() throws InterruptedException {
        monitoredBean.childOk();
		assertEquals(0, measurementAspect.getErrors());
		assertEquals(0, measurementAspect.getWarnings());
	}

    @Test
	public void testChildError() throws InterruptedException {
        monitoredBean.childOk();
		assertEquals(0, measurementAspect.getErrors());
		assertEquals(0, measurementAspect.getWarnings());
	}

}