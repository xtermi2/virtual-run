package akeefer.util;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import static akeefer.util.MethodProfilingLogger.endMethodProfiling;
import static akeefer.util.MethodProfilingLogger.startMethodProfiling;

/**
 * Aspect, welcher alle Methoden profiled, welche mit der @Profiling Annotation versehen sind.
 *
 * @author Andreas Keefer
 */
@Aspect
@Component("profilingAspect")
public class ProfilingAspect {

    @Pointcut("@annotation(akeefer.util.Profiling)")
    public void methodAnnotatedWithProfiling() {
    }

    @Around("methodAnnotatedWithProfiling()")
    public Object profilingExec(ProceedingJoinPoint pjp) throws Throwable {
        final long startTime = startMethodProfiling();
        try {
            return pjp.proceed();
        } finally {
            StringBuilder profiling = new StringBuilder("profiling ")
                    .append(pjp.getTarget().getClass().getSimpleName())
                    .append("#")
                    .append(pjp.getSignature().getName());
            endMethodProfiling(profiling.toString(), startTime);
        }
    }
}
