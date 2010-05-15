package fb.local.sla;

import static org.junit.Assert.assertEquals;

import javax.annotation.Resource;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Unit test for class annotated subclassed bean.
 * Extends MethodAnnotatedTest since all test methods should pass as well for the subclass.
 */
public class MethodAnnotatedChildTest extends MethodAnnotatedTest {

	@Resource(name = "methodAnnotatedChildBean")
	protected MethodAnnotatedChildBean monitoredBean;

    @Test
	public void testChildMethodWarn() throws InterruptedException {
		assertEquals(0, measurementAspect.getErrors());
		assertEquals(0, measurementAspect.getWarnings());

		monitoredBean.childMethodWarn();
		assertEquals(0, measurementAspect.getErrors());
		assertEquals(1, measurementAspect.getWarnings());
	}

    @Test
	public void testChildMethodError() throws InterruptedException {
		assertEquals(0, measurementAspect.getErrors());
		assertEquals(0, measurementAspect.getWarnings());

		monitoredBean.childMethodError();
		assertEquals(1, measurementAspect.getErrors());
		assertEquals(0, measurementAspect.getWarnings());
	}

    @Test
	public void testChildMethodOk() throws InterruptedException {
		assertEquals(0, measurementAspect.getErrors());
		assertEquals(0, measurementAspect.getWarnings());

		monitoredBean.childMethodOk();
		assertEquals(0, measurementAspect.getErrors());
		assertEquals(0, measurementAspect.getWarnings());
	}

    @Test
	public void testChildMethodNoSla() throws InterruptedException {
		assertEquals(0, measurementAspect.getErrors());
		assertEquals(0, measurementAspect.getWarnings());

		monitoredBean.childMethodNoSla();
		assertEquals(0, measurementAspect.getErrors());
		assertEquals(0, measurementAspect.getWarnings());
	}

    @Test
	public void testOverriddenWarn() throws InterruptedException {
		assertEquals(0, measurementAspect.getErrors());
		assertEquals(0, measurementAspect.getWarnings());

        // make sure we called the correct method
		assertEquals(23, monitoredBean.warn());

        // no warning since overridden method not annotated
		assertEquals(0, measurementAspect.getErrors());
		assertEquals(0, measurementAspect.getWarnings());
	}

}
