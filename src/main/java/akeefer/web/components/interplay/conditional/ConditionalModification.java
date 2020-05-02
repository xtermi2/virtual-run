package akeefer.web.components.interplay.conditional;

import akeefer.Nonnull;
import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.html.form.FormComponent;

/**
 * <strong>Example</strong>
 * <p/>
 * <pre>
 * IConditional&lt;Boolean&gt; conditional = new SimpleConditional(Boolean.TRUE);
 * TextField&lt;String&gt; text = new TextField&lt;String&gt;(&quot;text&quot;);
 * text.add(ConditionalModification.showIf(conditional));
 * </pre>
 */
@SuppressWarnings("serial")
public abstract class ConditionalModification extends Behavior implements IComponentModifier {

    @Nonnull
    private final IConditional<?> conditional;

    public ConditionalModification(@Nonnull IConditional<?> conditional) {
        this.conditional = conditional;
    }

    @Nonnull
    protected IConditional<?> getConditional() {
        return conditional;
    }

    @Override
    public void bind(Component component) {
        component.setOutputMarkupId(true);
        component.setOutputMarkupPlaceholderTag(true);
    }

    @Override
    public void modify(Component component) {
        applyStateOnCondition(conditional.isFulfilled(), component);
    }

    @Override
    public boolean isEnabled(Component component) {
        return true;
    }

    @Override
    public void onConfigure(Component component) {
        modify(component);
    }

    @Override
    public void detach(Component component) {
        super.detach(component);
        conditional.detach();
    }

    protected abstract void applyStateOnCondition(boolean condition, Component component);

    @Nonnull
    public static ConditionalModification requiredIf(@Nonnull IConditional<?> conditional) {
        return new RequiredIfFulfilled(conditional);
    }
    private static final class RequiredIfFulfilled extends ConditionalModification {

        public RequiredIfFulfilled(@Nonnull IConditional<?> conditional) {
            super(conditional);
        }

        @Override
        protected void applyStateOnCondition(boolean condition, Component component) {
            ((FormComponent<?>) component).setRequired(condition);
        }
    }

}
