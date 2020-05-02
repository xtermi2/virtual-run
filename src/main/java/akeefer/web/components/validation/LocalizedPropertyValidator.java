package akeefer.web.components.validation;

import org.apache.wicket.bean.validation.PropertyValidator;

public class LocalizedPropertyValidator<T> extends PropertyValidator<T> {

    public LocalizedPropertyValidator(Class<?>... groups) {
        super(groups);
    }
}
