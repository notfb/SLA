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
@Ignore
public class ClassAnnotationTest extends MeasurementAspectTest {

	@Resource
	private ClassAnnotatedBean classAnnotatedBean;

	@Test
	public void test() throws InterruptedException {
		assertEquals(0, slaMonitorBean.getErrors());
		assertEquals(0, slaMonitorBean.getWarnings());

		classAnnotatedBean.childMethod();
		assertEquals(0, slaMonitorBean.getErrors());
		assertEquals(1, slaMonitorBean.getWarnings());
	}

}
