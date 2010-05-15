package fb.local.sla;

import java.util.concurrent.TimeUnit;

/**
 * Simple bean for testing of class level annotation by extending an annotated class.
 */
public class ClassAnnotatedChildBean extends ClassAnnotatedBean {

    public void childOk() {
		// do nothing
	}

    // should not trigger a warning -> child not annotated
	public void childWarn() throws InterruptedException {
		Thread.sleep(240);
	}

    // should not trigger an error -> child not annotated
    public void childError() throws InterruptedException {
		Thread.sleep(350);
	}

    @Override
    public void ok() {
		// do nothing
	}

    // should not trigger an error -> child not annotated
    @Override
    public void warn() throws InterruptedException {
		Thread.sleep(240);
	}

}