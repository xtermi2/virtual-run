package akeefer.util;

/**
 * Markiert eine Methode fuers Profiling
 *
 * @author Andreas Keefer
 */

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Documented
@Target({ElementType.METHOD})
public @interface Profiling {
}
