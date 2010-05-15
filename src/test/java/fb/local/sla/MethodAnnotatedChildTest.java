package fb.local.sla;

import static org.junit.Assert.assertEquals;

import javax.annotation.Resource;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/testSlaContext.xml" })
@Ignore("TODO: impl")
public class MethodAnnotatedChildTest extends MethodAnnotatedTest {

	@Resource
	private MethodAnnotatedChildBean methodAnnotatedChildBean;

    @Test
	public void test() throws InterruptedException {
		assertEquals(0, measurementAspect.getErrors());
		assertEquals(0, measurementAspect.getWarnings());

		methodAnnotatedChildBean.childMethodWarn();
		assertEquals(0, measurementAspect.getErrors());
		assertEquals(1, measurementAspect.getWarnings());
	}

}
