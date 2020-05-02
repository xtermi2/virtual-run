package akeefer.web.components.interplay.conditional;

import org.apache.wicket.model.IChainingModel;
import org.apache.wicket.model.IDetachable;
import org.apache.wicket.model.IModel;

/**
 * Abstract Impl of Conditionals.
 *
 * @param <T> Modelobject-Typ.
 */
@SuppressWarnings("serial")
public abstract class AbstractConditional<T> implements IConditional<T>, IChainingModel<T> {

    private Object target;

    public AbstractConditional(IModel<T> model) {
        this.target = model;
    }

    public boolean isFulfilled() {
        return isFulfilled(getObject());
    }

    /**
     * checks if the condition is fulfilled
     *
     * @param modelObject Modelobject, which will be checked.
     * @return <code>true</code>, if the condition is fulfilled
     */
    public abstract boolean isFulfilled(T modelObject);

    @SuppressWarnings("unchecked")
    @Override
    public T getObject() {
        if (target instanceof IModel) {
            return ((IModel<T>) target).getObject();
        } else {
            return (T) target;
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void setObject(T object) {
        if (target instanceof IModel) {
            ((IModel<T>) target).setObject(object);
        } else {
            target = object;
        }
    }

    @Override
    public void detach() {
        if (target instanceof IDetachable) {
            ((IDetachable) target).detach();
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public IModel<?> getChainedModel() {
        if (target instanceof IModel) {
            return (IModel<T>) target;
        }
        return null;
    }

    @Override
    public void setChainedModel(IModel<?> model) {
        target = model;
    }

    @Override
    public String toString() {
        return getClass() + "(" + target + ")";
    }
}
