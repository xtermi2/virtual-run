package akeefer.test.util;

import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;

public class ProxyUtil {

    private ProxyUtil(){}

    public static <T> T getTargetObject(Object proxy, Class<T> targetClass) throws Exception {
        while (AopUtils.isJdkDynamicProxy(proxy)) {
            proxy = getTargetObject(((Advised) proxy).getTargetSource().getTarget(), targetClass);
        }
        return (T) proxy; // expected to be cglib proxy then, which is simply a specialized class
    }
}
