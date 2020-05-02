package akeefer.web.components.interplay.conditional;

import org.apache.wicket.model.IModel;

import java.util.Arrays;
import java.util.Collection;

/**
 * Conditional that checks, if a model is contained in a collection.
 *
 * @param <T> The type of the model.
 */
@SuppressWarnings("serial")
class IsOneOfConditional<T> extends AbstractConditional<T> {
    private final Object collectionObject;

    IsOneOfConditional(IModel<T> tiModel, T... values) {
        super(tiModel);
        this.collectionObject = Arrays.asList(values);
    }

    @Override
    public boolean isFulfilled(T modelObject) {
        return getCollection() != null && getCollection().contains(modelObject);
    }

    @SuppressWarnings("unchecked")
    private Collection<T> getCollection() {
        if (collectionObject == null || collectionObject instanceof Collection) {
            return (Collection<T>) collectionObject;
        }
        return ((IModel<? extends Collection<T>>) collectionObject).getObject();
    }
}
