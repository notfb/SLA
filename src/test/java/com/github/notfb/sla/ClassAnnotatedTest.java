package com.github.notfb.sla;

import org.junit.Before;
import org.junit.Test;

import javax.annotation.Resource;

import static org.junit.Assert.assertEquals;

/**
 * Test class level annotation.
 */
public class ClassAnnotatedTest extends MeasurementTest {

	@Resource(name = "classAnnotatedBean")
	protected ClassAnnotatedBean monitoredBean;

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
		monitoredBean.warn();
		assertEquals(0, measurementAspect.getErrors());
		assertEquals(1, measurementAspect.getWarnings());

		monitoredBean.warn();
		assertEquals(0, measurementAspect.getErrors());
		assertEquals(2, measurementAspect.getWarnings());
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

}