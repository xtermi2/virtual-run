package akeefer.web.components;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.form.FormComponent;

/**
 * sets on every FormComponent the HTML 'required' Attribut when the FormComponent is Required
 */
public class RequiredBehavior extends Behavior {

    @Override
    public void onComponentTag(Component component, ComponentTag tag) {
        if (component instanceof FormComponent) {
            FormComponent formComponent = (FormComponent) component;
            if (formComponent.isRequired()) {
                tag.put("required", "required");
            }
        }
    }
}
