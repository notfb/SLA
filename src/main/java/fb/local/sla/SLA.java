package fb.local.sla;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.concurrent.TimeUnit;

/**
 * A annotation to introduce a SLA (Service Level Agreement) to your Code.
 * 
 * You can specify a warn(ing) time value (default time unit is milliseconds).
 * If a method takes longer to complete than the specified time, an warning message is logged.
 * The error value works the same way, it just logs an error.
 *
 * Extending a annotated class or overriding an annotated method or class is _NOT_ considered an
 * extension of the SLA! You need to add a new SLA-Annotation.
 * Why? To keep it simple. I dislike stepping through complex class hierarchies just to figure out what SLA is used.
 * (If you have a different point of view, feel free to tell me.)
 *
 * @see fb.local.sla.MeasurementAspect 
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@java.lang.annotation.Target( { ElementType.METHOD, ElementType.TYPE })
public @interface SLA {

	long warn();
	
	long error();

	TimeUnit unit() default TimeUnit.MILLISECONDS;

}
