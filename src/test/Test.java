package test;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

/** Annotate tests to be collected and run by test runner.
 *
 * Any method annotated with @Test will be collected when the parent
 * class is sent to a BkTest runner method. Test methods should be
 * *instance*, not static. A new object of the parent class will be
 * instantiated for every test.
 * */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Test { }