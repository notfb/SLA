package fb.local.sla;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.concurrent.TimeUnit;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@java.lang.annotation.Target( { ElementType.METHOD, ElementType.TYPE })
public @interface SLA {

	long warn();
	
	long error();

	TimeUnit unit() default TimeUnit.MILLISECONDS;

}
