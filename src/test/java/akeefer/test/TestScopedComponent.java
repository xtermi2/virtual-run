package akeefer.test;

import org.springframework.context.annotation.Primary;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Primary
public @interface TestScopedComponent {
}
