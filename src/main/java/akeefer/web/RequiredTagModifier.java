package akeefer.web;

import org.apache.wicket.bean.validation.ITagModifier;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.form.FormComponent;

import javax.validation.constraints.NotNull;

/**
 * Erzwugt das Required tag bei NotNull BeanValidation Constraint
 */
public class RequiredTagModifier implements ITagModifier<NotNull> {
    @Override
    public void modify(FormComponent<?> component, ComponentTag tag, NotNull annotation) {
        if ("input".equalsIgnoreCase(tag.getName()) || "select".equalsIgnoreCase(tag.getName())) {
            tag.put("required", "required");
        }
    }
}
