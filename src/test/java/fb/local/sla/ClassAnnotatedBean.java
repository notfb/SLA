package fb.local.sla;

import java.util.concurrent.TimeUnit;

/**
 * Simple Bean to test class annotations
 */
@SLA(error=200, warn=100, unit= TimeUnit.MILLISECONDS)
public class ClassAnnotatedBean {

    public void ok() {
		// do nothing
	}

	public void warn() throws InterruptedException {
		Thread.sleep(140);
	}

    public void error() throws InterruptedException {
		Thread.sleep(250);
	}
}
