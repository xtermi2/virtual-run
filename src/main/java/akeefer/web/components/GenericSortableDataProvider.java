package akeefer.web.components;

import org.apache.commons.collections.comparators.NullComparator;
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import java.io.Serializable;
import java.util.*;

public class GenericSortableDataProvider<T extends Serializable> extends SortableDataProvider<T, String> {

    private static final Comparator COMPARATOR = new NullComparator(true);

    private final List<T> list;

    public GenericSortableDataProvider(List<T> list) {
        if (null != list) {
            this.list = new ArrayList<T>(list);
        } else {
            this.list = Collections.emptyList();
        }
    }

    @Override
    public Iterator<? extends T> iterator(long first, long count) {
        if (null != getSort() && StringUtils.isNotBlank(getSort().getProperty())) {
            Collections.sort(list, new Comparator<T>() {
                @Override
                public int compare(T o1, T o2) {
                    PropertyModel o1Property = new PropertyModel<Comparable>(o1, getSort().getProperty());
                    PropertyModel o2Property = new PropertyModel<Comparable>(o2, getSort().getProperty());
                    return (getSort().isAscending() ? 1 : -1) * COMPARATOR.compare(o1Property.getObject(), o2Property.getObject());
                }
            });
        }

        return list.subList((int) first, (int) Math.min(first + count, size())).iterator();
    }

    @Override
    public long size() {
        return list.size();
    }

    @Override
    public IModel<T> model(T object) {
        return Model.of(object);
    }
}
