package akeefer.web.components.validation;

import org.apache.wicket.bean.validation.Property;
import org.apache.wicket.bean.validation.PropertyValidator;
import org.apache.wicket.model.IModel;

public class LocalizedPropertyValidator<T> extends PropertyValidator<T> {

    public LocalizedPropertyValidator(Class<?>... groups) {
        super(groups);
    }

    public LocalizedPropertyValidator(IModel<Class<?>[]> groups) {
        super(groups);
    }

    public LocalizedPropertyValidator(Property property, Class<?>... groups) {
        super(property, groups);
    }

    public LocalizedPropertyValidator(Property property, IModel<Class<?>[]> groups) {
        super(property, groups);
    }
}
