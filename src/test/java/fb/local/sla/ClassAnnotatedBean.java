package fb.local.sla;

import java.util.concurrent.TimeUnit;

/**
 * simple bean for testing of class level annotation.
 */
@SLA(error=200, warn=100, unit=TimeUnit.MILLISECONDS)
public class ClassAnnotatedBean extends AnnotatedBean {
	
	public void childMethod() throws InterruptedException {
		Thread.sleep(140);
	}
}
