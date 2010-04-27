package fb.local.sla;

import java.util.concurrent.TimeUnit;

/**
 * simple bean for testing ...
 */
public class AnnotatedBean {

	@SLA(error=200, warn=100, unit=TimeUnit.MILLISECONDS)
	public void ok() {
		/* do nothing */
	}
	
	@SLA(error=1000, warn=50)
	public int warn() throws InterruptedException {
		Thread.sleep(200);
		return 42;
	}
	
	@SLA(error=50, warn=10)
	public Object error(String s) throws InterruptedException {
		Thread.sleep(200);
		return s;
	}
	
	// overloaded method to check for AOP and reflection problems
	@SLA(error=55, warn=10)
	public Object error(String s, Object o) throws InterruptedException {
		Thread.sleep(200);
		return s + o.toString();
	}
	
	@SLA(error=60, warn=10)
	public Object error(String s, int i) throws InterruptedException {
		Thread.sleep(200);
		return s + i;
	}
}
