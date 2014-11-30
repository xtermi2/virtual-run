package akeefer.web.components;

import org.apache.commons.lang3.Validate;
import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.model.IComponentAssignedModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;

public class PlaceholderBehavior extends Behavior {
    private IModel<String> placeholder;

    public static PlaceholderBehavior ofResourceKey(String key) {
        return new PlaceholderBehavior(new ResourceModel(key));
    }

    public static PlaceholderBehavior ofModel(IModel<String> model) {
        return new PlaceholderBehavior(model);
    }

    public static PlaceholderBehavior ofText(String text) {
        return new PlaceholderBehavior(text);
    }

    public PlaceholderBehavior(IModel<String> placeholder) {
        Validate.notNull(placeholder);
        this.placeholder = placeholder;
    }

    public PlaceholderBehavior(String placeholder) {
        this.placeholder = Model.of(placeholder);
    }

    @Override
    public void onComponentTag(Component component, ComponentTag tag) {
        tag.put("placeholder", this.placeholder.getObject());
    }

    @Override
    public void bind(final Component component) {
        super.bind(component);
        if (placeholder instanceof IComponentAssignedModel) {
            // z.B. bei einem ResourceModel muss das model an eine Component gebunden werden um die Resource zu laden
            placeholder = ((IComponentAssignedModel) placeholder).wrapOnAssignment(component);
        }
    }
}