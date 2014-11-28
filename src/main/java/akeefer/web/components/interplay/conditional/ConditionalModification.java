package akeefer.web.components.interplay.conditional;

import akeefer.Nonnull;
import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.model.IModel;

import static akeefer.web.components.layout.LayoutUtils.*;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.apache.commons.lang3.Validate.isTrue;

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
    public static ConditionalModification showIf(@Nonnull IConditional<?> conditional) {
        return new VisibleIfFulfilled(conditional);
    }

    @Nonnull
    public static ConditionalModification hideIf(@Nonnull IConditional<?> conditional) {
        return new InvisibleIfFulfilled(conditional);
    }

    @Nonnull
    public static ConditionalModification enableIf(@Nonnull IConditional<?> conditional) {
        return new EnabledIfFulfilled(conditional);
    }

    @Nonnull
    public static ConditionalModification disableIf(@Nonnull IConditional<?> conditional) {
        return new DisabledIfFulfilled(conditional);
    }

    @Nonnull
    public static ConditionalModification requiredIf(@Nonnull IConditional<?> conditional) {
        return new RequiredIfFulfilled(conditional);
    }

    @Nonnull
    public static ConditionalModification requiredUnless(@Nonnull IConditional<?> conditional) {
        return new RequiredUnlessFulfilled(conditional);
    }

    @Nonnull
    public static ConditionalModification alterModelObjectIf(@Nonnull IConditional<?> conditional, Object value) {
        return new AlterModelObjectIf(conditional, value);
    }

    @Nonnull
    public static ConditionalModification alterModelObjectUnless(@Nonnull IConditional<?> conditional, Object value) {
        return new AlterModelObjectUnless(conditional, value);
    }

    @Nonnull
    public static ConditionalModification alterModelObjectDependingOn(@Nonnull IConditional<?> conditional, Object valueIf, Object valueUnless) {
        return new AlterModelObjectIf(conditional, valueIf, valueUnless);
    }

    @Nonnull
    public static ConditionalModification alterModelDependingOn(@Nonnull IConditional<?> conditional, IModel<?> modelIf, IModel<?> modelUnless) {
        return new AlterModelIf(conditional, modelIf, modelUnless);
    }

    @Nonnull
    public static ConditionalModification styleClassIf(@Nonnull IConditional<?> conditional, @Nonnull String styleClass) {
        return new StyleClassIfFulfilled(conditional, styleClass);
    }

    @Nonnull
    public static ConditionalModification styleClassUnless(@Nonnull IConditional<?> conditional, @Nonnull String styleClass) {
        return new StyleClassUnlessFulfilled(conditional, styleClass);
    }

    @Nonnull
    public static ConditionalModification alterStyleClassDependingOn(@Nonnull IConditional<?> conditional, @Nonnull String classIf,
                                                                     @Nonnull String classUnless) {
        return new AlterStyleClassIf(conditional, classIf, classUnless);
    }

    @Nonnull
    public static ConditionalModification alterTitleDependingOn(@Nonnull IConditional<?> conditional, IModel<String> titleIf,
                                                                IModel<String> titleUnless) {
        return new AlterTitleIf(conditional, titleIf, titleUnless);
    }

    private static class AlterModelObjectIf extends ConditionalModification {

        private final Object valueIf;
        private final Object valueUnless;
        private final boolean unless;

        public AlterModelObjectIf(@Nonnull IConditional<?> conditional, Object valueIf) {
            super(conditional);
            this.valueIf = valueIf;
            this.valueUnless = null;
            this.unless = false;
        }

        public AlterModelObjectIf(@Nonnull IConditional<?> conditional, Object valueIf, Object valueUnless) {
            super(conditional);
            this.valueIf = valueIf;
            this.valueUnless = valueUnless;
            this.unless = true;
        }

        @Override
        protected void applyStateOnCondition(boolean condition, Component component) {
            if (condition) {
                component.setDefaultModelObject(valueIf);
            } else if (unless) {
                component.setDefaultModelObject(valueUnless);
            }
        }
    }

    private static final class AlterModelObjectUnless extends AlterModelObjectIf {

        public AlterModelObjectUnless(@Nonnull IConditional<?> conditional, Object valueUnless) {
            super(conditional, valueUnless);
        }

        @Override
        protected void applyStateOnCondition(boolean condition, Component component) {
            super.applyStateOnCondition(!condition, component);
        }
    }

    private static class AlterModelIf extends ConditionalModification {

        private final IModel<?> modelIf;
        private final IModel<?> modelUnless;

        public AlterModelIf(@Nonnull IConditional<?> conditional, IModel<?> modelIf, IModel<?> modelUnless) {
            super(conditional);
            this.modelIf = modelIf;
            this.modelUnless = modelUnless;
        }

        @Override
        protected void applyStateOnCondition(boolean condition, Component component) {
            if (condition) {
                component.setDefaultModel(modelIf);
            } else {
                component.setDefaultModel(modelUnless);
            }
        }
    }

    private static final class VisibleIfFulfilled extends ConditionalModification {

        public VisibleIfFulfilled(@Nonnull IConditional<?> conditional) {
            super(conditional);
        }

        @Override
        protected void applyStateOnCondition(boolean condition, Component component) {
            component.setVisibilityAllowed(condition);
            component.setVisible(condition);
        }
    }

    private static final class InvisibleIfFulfilled extends ConditionalModification {

        public InvisibleIfFulfilled(@Nonnull IConditional<?> conditional) {
            super(conditional);
        }

        @Override
        protected void applyStateOnCondition(boolean condition, Component component) {
            // component.modelChanging();
            component.setVisibilityAllowed(!condition);
            component.setVisible(!condition);
        }
    }

    private static final class EnabledIfFulfilled extends ConditionalModification {

        public EnabledIfFulfilled(@Nonnull IConditional<?> conditional) {
            super(conditional);
        }

        @Override
        protected void applyStateOnCondition(boolean condition, Component component) {
            component.setEnabled(condition);
        }
    }

    private static final class DisabledIfFulfilled extends ConditionalModification {

        public DisabledIfFulfilled(@Nonnull IConditional<?> conditional) {
            super(conditional);
        }

        @Override
        protected void applyStateOnCondition(boolean condition, Component component) {
            component.setEnabled(!condition);
        }
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

    private static final class RequiredUnlessFulfilled extends ConditionalModification {

        public RequiredUnlessFulfilled(@Nonnull IConditional<?> conditional) {
            super(conditional);
        }

        @Override
        protected void applyStateOnCondition(boolean condition, Component component) {
            ((FormComponent<?>) component).setRequired(!condition);
        }
    }

    private static class StyleClassIfFulfilled extends ConditionalModification {

        @Nonnull
        protected final String styleClass;

        public StyleClassIfFulfilled(@Nonnull IConditional<?> conditional, @Nonnull String styleClass) {
            super(conditional);
            this.styleClass = styleClass;
            isTrue(!isEmpty(styleClass));
        }

        @Override
        public void onConfigure(Component component) {
        }

        @Override
        protected void applyStateOnCondition(boolean condition, Component component) {
        }

        @Override
        public void onComponentTag(Component component, ComponentTag tag) {
            super.onComponentTag(component, tag);
            applyStateOnCondition(getConditional().isFulfilled(), tag);
        }

        protected void applyStateOnCondition(boolean condition, @Nonnull final ComponentTag tag) {
            modifyClass(tag, styleClass, condition);
        }
    }

    private static final class StyleClassUnlessFulfilled extends StyleClassIfFulfilled {

        public StyleClassUnlessFulfilled(@Nonnull IConditional<?> conditional, @Nonnull String styleClass) {
            super(conditional, styleClass);
        }

        @Override
        protected void applyStateOnCondition(boolean condition, @Nonnull final ComponentTag tag) {
            modifyClass(tag, styleClass, !condition);
        }
    }

    private static class AlterTitleIf extends ConditionalModification {

        private final IModel<String> titleIf;
        private final IModel<String> titleUnless;

        public AlterTitleIf(@Nonnull IConditional<?> conditional, IModel<String> titleIf, IModel<String> titleUnless) {
            super(conditional);
            this.titleIf = titleIf;
            this.titleUnless = titleUnless;
        }

        @Override
        protected void applyStateOnCondition(boolean condition, Component component) {
        }

        @Override
        public void onComponentTag(Component component, ComponentTag tag) {
            super.onComponentTag(component, tag);
            applyStateOnCondition(getConditional().isFulfilled(), tag);
        }

        protected void applyStateOnCondition(boolean condition, @Nonnull ComponentTag tag) {
            if (condition) {
                switchTitle(tag, (titleIf != null ? titleIf.getObject() : null));
            } else {
                switchTitle(tag, (titleUnless != null ? titleUnless.getObject() : null));
            }
        }

        @Override
        public void detach(Component component) {
            super.detach(component);
            titleIf.detach();
            titleUnless.detach();
        }
    }

    private static class AlterStyleClassIf extends ConditionalModification {

        @Nonnull
        private final String classIf;
        @Nonnull
        private final String classUnless;

        public AlterStyleClassIf(@Nonnull IConditional<?> conditional, @Nonnull String classIf, @Nonnull String classUnless) {
            super(conditional);
            this.classIf = classIf;
            this.classUnless = classUnless;
            isTrue(!isEmpty(classIf));
            isTrue(!isEmpty(classUnless));
        }

        @Override
        protected void applyStateOnCondition(boolean condition, Component component) {
        }

        @Override
        public void onComponentTag(Component component, ComponentTag tag) {
            super.onComponentTag(component, tag);
            applyStateOnCondition(getConditional().isFulfilled(), tag);
        }

        protected void applyStateOnCondition(boolean condition, @Nonnull final ComponentTag tag) {
            if (condition) {
                switchClass(tag, classUnless, classIf);
            } else {
                switchClass(tag, classIf, classUnless);
            }
        }
    }
}
