package fb.local.sla;

import java.util.concurrent.TimeUnit;

/**
 * simple bean for testing of class level annotation.
 */
public class MethodAnnotatedChildBean extends MethodAnnotatedBean {

    @SLA(error=200, warn=100, unit=TimeUnit.MILLISECONDS)
    public void childMethodOk() throws InterruptedException {
		// do nothing
	}

    @SLA(error=200, warn=100, unit=TimeUnit.MILLISECONDS)
	public void childMethodWarn() throws InterruptedException {
		Thread.sleep(140);
	}

    @SLA(error=200, warn=100, unit=TimeUnit.MILLISECONDS)    
    public void childMethodError() throws InterruptedException {
		Thread.sleep(250);
	}

    public void childMethodNoSla() throws InterruptedException {
	    // do nothing
	}
}
