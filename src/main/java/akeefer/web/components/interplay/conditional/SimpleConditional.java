package akeefer.web.components.interplay.conditional;

import akeefer.Nonnull;
import org.apache.wicket.model.IChainingModel;
import org.apache.wicket.model.IModel;

import java.util.List;

/**
 * Factory-Method-Holder für verschiedene einfache Realisierungen von {@link IConditional}.
 */
@SuppressWarnings("serial")
public abstract class SimpleConditional {
    @Nonnull
    public static <T> IConditional<T> isOneOf(IModel<T> iModel, T... values) {
        return new IsOneOfConditional<T>(iModel, values);
    }

    @Nonnull
    public static <T> IConditional<T> not(@Nonnull IConditional<T> conditional) {
        return new NotConditional<T>(conditional);
    }

    @Nonnull
    public static IConditional<Void> and(IConditional<?>... conditionals) {
        return new AbstractChainingConditional(conditionals) {
            @Override
            protected boolean isFulfilled(List<? extends IConditional<?>> conditionals) {
                for (IConditional<?> conditional : conditionals) {
                    if (!conditional.isFulfilled()) {
                        return false;
                    }
                }
                return true;
            }
        };
    }

    @Nonnull
    public static IConditional<Void> or(IConditional<?>... conditionals) {
        return new AbstractChainingConditional(conditionals) {
            @Override
            protected boolean isFulfilled(List<? extends IConditional<?>> conditionals) {
                for (IConditional<?> conditional : conditionals) {
                    if (conditional.isFulfilled()) {
                        return true;
                    }
                }
                return false;
            }
        };
    }

    public static class NotConditional<T> implements IConditional<T>, IChainingModel<T> {

        @Nonnull
        private IConditional<T> conditional;

        public NotConditional(@Nonnull IConditional<T> conditional) {
            this.conditional = conditional;
        }

        @Override
        public T getObject() {
            return conditional.getObject();
        }

        @Override
        public void setObject(T object) {
            conditional.setObject(object);
        }

        @Override
        public void detach() {
            conditional.detach();
        }

        @Override
        public boolean isFulfilled() {
            return !conditional.isFulfilled();
        }

        @SuppressWarnings("unchecked")
        @Override
        public void setChainedModel(IModel<?> model) {
            this.conditional = (IConditional<T>) model;
        }

        @Override
        public IConditional<T> getChainedModel() {
            return conditional;
        }
    }
}
