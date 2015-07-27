package akeefer.web.components.interplay.conditional;

import akeefer.Nonnull;
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.model.IChainingModel;
import org.apache.wicket.model.IModel;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * Factory-Method-Holder für verschiedene einfache Realisierungen von {@link IConditional}.
 */
@SuppressWarnings("serial")
public abstract class SimpleConditional {

    @Nonnull
    public static IConditional<Boolean> isTrue(IModel<Boolean> iModel) {
        return new AbstractConditional<Boolean>(iModel) {
            @Override
            public boolean isFulfilled(Boolean bool) {
                return Boolean.TRUE.equals(bool);
            }
        };
    }

    @Nonnull
    public static <T> IConditional<T> isSame(IModel<T> iModel, final T object) {
        return new AbstractConditional<T>(iModel) {
            @Override
            public boolean isFulfilled(T modelObject) {
                return modelObject == object || (modelObject != null && modelObject.equals(object)); // NOSONAR
                // hier werden absichtlich beide Gleichheiten geprüft
            }
        };
    }

    @Nonnull
    public static <T> IConditional<T> isNull(IModel<T> iModel) {
        return new AbstractConditional<T>(iModel) {
            @Override
            public boolean isFulfilled(T modelObject) {
                return modelObject == null;
            }
        };
    }

    @Nonnull
    public static <T> IConditional<T> isNotNull(IModel<T> iModel) {
        return new AbstractConditional<T>(iModel) {
            @Override
            public boolean isFulfilled(T modelObject) {
                return modelObject != null;
            }
        };
    }

    @Nonnull
    public static <T> IConditional<T> isSame(IModel<T> iModel, final IModel<T> otherModel) {
        return new AbstractConditional<T>(iModel) {
            @Override
            public boolean isFulfilled(T modelObject) {
                Object otherModelObject = otherModel != null ? otherModel.getObject() : null;
                return modelObject == otherModelObject || (null != modelObject && modelObject.equals(otherModelObject));
            }
        };
    }

    @Nonnull
    public static IConditional<String> isSameIgnoreCase(IModel<String> iModel, final IModel<String> otherModel) {
        return new AbstractConditional<String>(iModel) {
            @Override
            public boolean isFulfilled(String modelObject) {
                String otherModelObject = otherModel != null ? otherModel.getObject() : null;
                if (otherModelObject == null) {
                    return modelObject == null;
                }
                return otherModelObject.equalsIgnoreCase(modelObject);
            }
        };
    }

    @Nonnull
    public static <T extends Comparable<T>> IConditional<T> isSameInComparison(IModel<T> iModel, final IModel<T> otherModel) {
        return new AbstractConditional<T>(iModel) {
            @Override
            public boolean isFulfilled(T modelObject) {
                T otherModelObject = otherModel != null ? otherModel.getObject() : null;
                return modelObject == otherModelObject || modelObject != null && otherModelObject != null
                        && modelObject.compareTo(otherModelObject) == 0;
            }
        };
    }

    @Nonnull
    public static <T extends Comparable<T>> IConditional<T> isLess(IModel<T> iModel, final IModel<T> otherModel) {
        return new AbstractConditional<T>(iModel) {
            @Override
            public boolean isFulfilled(T modelObject) {
                T otherModelObject = otherModel != null ? otherModel.getObject() : null;
                return modelObject != null && otherModelObject != null && modelObject.compareTo(otherModelObject) < 0;
            }
        };
    }

    @Nonnull
    public static <T extends Comparable<T>> IConditional<T> isGreater(IModel<T> iModel, final IModel<T> otherModel) {
        return new AbstractConditional<T>(iModel) {
            @Override
            public boolean isFulfilled(T modelObject) {
                T otherModelObject = otherModel != null ? otherModel.getObject() : null;

                return modelObject != null && otherModelObject != null && modelObject.compareTo(otherModelObject) > 0;
            }
        };
    }

    @Nonnull
    public static IConditional<String> isBlank(IModel<String> iModel) {
        return new AbstractConditional<String>(iModel) {
            @Override
            public boolean isFulfilled(String modelObject) {
                return StringUtils.isBlank(modelObject);
            }
        };
    }

    @Nonnull
    public static IConditional<String> isNotBlank(IModel<String> iModel) {
        return new AbstractConditional<String>(iModel) {
            @Override
            public boolean isFulfilled(String modelObject) {
                return !StringUtils.isBlank(modelObject);
            }
        };
    }

    @Nonnull
    public static <T extends Collection<?>> IConditional<T> isEmpty(IModel<T> iModel) {
        return new AbstractConditional<T>(iModel) {
            @Override
            public boolean isFulfilled(T modelObject) {
                return modelObject == null || modelObject.isEmpty();
            }
        };
    }

    @Nonnull
    public static <T extends Collection<?>> IConditional<T> isNotEmpty(IModel<T> iModel) {
        return new AbstractConditional<T>(iModel) {
            @Override
            public boolean isFulfilled(T modelObject) {
                return modelObject != null && !modelObject.isEmpty();
            }
        };
    }

    @Nonnull
    public static <T extends Collection<?>> IConditional<T> hasExactItems(IModel<T> iModel, IModel<Integer> count) {
        return new AbstractListConditional<T>(iModel, count) {
            @Override
            public boolean isFulfilled(T modelObject) {
                int count = getCount();
                if (modelObject == null) {
                    return count == 0;
                }
                return modelObject.size() == count;
            }
        };
    }

    @Nonnull
    public static <T extends Collection<?>> IConditional<T> hasNotExactItems(IModel<T> iModel, IModel<Integer> count) {
        return new AbstractListConditional<T>(iModel, count) {
            @Override
            public boolean isFulfilled(T modelObject) {
                int count = getCount();
                if (modelObject == null) {
                    return count != 0;
                }
                return modelObject.size() != count;
            }
        };
    }

    @Nonnull
    public static <T extends Collection<?>> IConditional<T> hasExactOrLesserItems(IModel<T> iModel, IModel<Integer> count) {
        return new AbstractListConditional<T>(iModel, count) {
            @Override
            public boolean isFulfilled(T modelObject) {
                int count = getCount();
                if (modelObject == null) {
                    return count >= 0;
                }
                return modelObject.size() <= count;
            }
        };
    }

    @Nonnull
    public static <T extends Collection<?>> IConditional<T> hasExactOrMoreItems(IModel<T> iModel, IModel<Integer> count) {
        return new AbstractListConditional<T>(iModel, count) {
            @Override
            public boolean isFulfilled(T modelObject) {
                int count = getCount();
                if (modelObject == null) {
                    return count <= 0;
                }
                return modelObject.size() >= count;
            }
        };
    }

    @Nonnull
    public static <T extends Collection<?>> IConditional<T> hasLesserItems(IModel<T> iModel, IModel<Integer> count) {
        return new AbstractListConditional<T>(iModel, count) {
            @Override
            public boolean isFulfilled(T modelObject) {
                int count = getCount();
                if (modelObject == null) {
                    return count > 0;
                }
                return modelObject.size() < count;
            }
        };
    }

    @Nonnull
    public static <T extends Collection<?>> IConditional<T> hasMoreItems(IModel<T> iModel, IModel<Integer> count) {
        return new AbstractListConditional<T>(iModel, count) {
            @Override
            public boolean isFulfilled(T modelObject) {
                int count = getCount();
                if (modelObject == null) {
                    return count < 0;
                }
                return modelObject.size() > count;
            }
        };
    }

    @Nonnull
    public static <T> IConditional<T> isOneOf(IModel<T> iModel, Collection<T> collection) {
        return new IsOneOfConditional<T>(iModel, collection);
    }

    @Nonnull
    public static <T> IConditional<T> isOneOf(IModel<T> iModel, IModel<? extends Collection<? extends T>> collection) {
        return new IsOneOfConditional<T>(iModel, collection);
    }

    @Nonnull
    public static <T> IConditional<T> isOneOf(IModel<T> iModel, T... values) {
        return new IsOneOfConditional<T>(iModel, values);
    }

    @Nonnull
    public static <T> IConditional<T> isInstanceOf(IModel<T> iModel, @Nonnull final Class<?> clazz) {
        return new AbstractConditional<T>(iModel) {
            @Override
            public boolean isFulfilled(T modelObject) {
                return modelObject != null && clazz.isAssignableFrom(modelObject.getClass());
            }
        };
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
    public static IConditional<Void> xor(IConditional<?>... conditionals) {
        return new AbstractChainingConditional(conditionals) {
            @Override
            protected boolean isFulfilled(List<? extends IConditional<?>> conditionals) {
                int fullfilled = 0;
                for (IConditional<?> conditional : conditionals) {
                    if (conditional.isFulfilled()) {
                        fullfilled++;
                    }
                }
                return fullfilled == 1;
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

    @SuppressWarnings("unchecked")
    @Nonnull
    public static <O> IConditional<O> alwaysTrue() {
        return (IConditional<O>) TrueConditional.INSTANCE;
    }

    @SuppressWarnings("unchecked")
    @Nonnull
    public static <O> IConditional<O> alwaysFalse() {
        return (IConditional<O>) FalseConditional.INSTANCE;
    }

    private static final class TrueConditional<O extends Serializable> implements IConditional<O> {
        @SuppressWarnings("rawtypes")
        @Nonnull
        private static final TrueConditional INSTANCE = new TrueConditional();

        @Override
        public O getObject() {
            return null;
        }

        @Override
        public void setObject(O object) {
        }

        @Override
        public void detach() {
        }

        @Override
        public boolean isFulfilled() {
            return true;
        }
    }

    private static final class FalseConditional<O extends Serializable> implements IConditional<O> {
        @SuppressWarnings("rawtypes")
        @Nonnull
        private static final FalseConditional INSTANCE = new FalseConditional();

        @Override
        public O getObject() {
            return null;
        }

        @Override
        public void setObject(O object) {
        }

        @Override
        public void detach() {
        }

        @Override
        public boolean isFulfilled() {
            return false;
        }
    }

    public abstract static class AbstractListConditional<T extends Collection<?>> implements IConditional<T>, IChainingModel<T> {

        private IModel<T> target;
        private IModel<Integer> count;

        public AbstractListConditional(IModel<T> iModel, IModel<Integer> count) {
            this.target = iModel;
            this.count = count;
        }

        @Override
        public T getObject() {
            if (target != null) {
                return ((IModel<T>) target).getObject();
            }
            return null;
        }

        @Override
        public void setObject(T object) {
            target.setObject(object);
        }

        @Override
        public void detach() {
            if (target != null) {
                target.detach();
            }
            if (count != null) {
                count.detach();
            }
        }

        @Override
        public IModel<?> getChainedModel() {
            return target;
        }

        @SuppressWarnings("unchecked")
        @Override
        public void setChainedModel(IModel<?> model) {
            target = (IModel<T>) model;
        }

        @Override
        public String toString() {
            return getClass() + "(" + target + ")";
        }

        public int getCount() {
            if (count != null) {
                Integer result = count.getObject();
                return result != null ? result.intValue() : 0;
            }
            return 0;
        }

        @Override
        public boolean isFulfilled() {
            return isFulfilled(getObject());
        }

        public abstract boolean isFulfilled(T modelObject);

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
