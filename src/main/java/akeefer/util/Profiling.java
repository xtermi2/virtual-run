package akeefer.util;


import java.lang.annotation.*;

/**
 * Markiert eine Methode fuers Profiling
 *
 * @author Andreas Keefer
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Target({ElementType.METHOD})
public @interface Profiling {
}
