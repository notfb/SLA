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
	public void test() throws InterruptedException {
		assertEquals(0, measurementAspect.getErrors());
		assertEquals(0, measurementAspect.getWarnings());

		monitoredBean.childMethodWarn();
		assertEquals(0, measurementAspect.getErrors());
		assertEquals(1, measurementAspect.getWarnings());
	}

}
