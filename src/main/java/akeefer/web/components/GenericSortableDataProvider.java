package akeefer.web.components;

import org.apache.commons.collections.comparators.NullComparator;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.*;

public class GenericSortableDataProvider<T extends Serializable> extends SortableDataProvider<T, String> {

    private static final Logger logger = LoggerFactory.getLogger(GenericSortableDataProvider.class);
    private static final Comparator COMPARATOR = new NullComparator(true);

    private final IModel<List<T>> list;

    public GenericSortableDataProvider(IModel<List<T>> list) {
        Validate.notNull(list);
        this.list = list;
        if (null == list.getObject()) {
            list.setObject(new ArrayList<T>());
        }
    }

    @Override
    public Iterator<? extends T> iterator(long first, long count) {
        // wir nehmen hier eine neue Liste, da der JPA Provider (datanucleus) eventuell eine ListImpl hier bereitstellt, die nicht sortierbar ist
        List<T> tmpList = new ArrayList<T>(list.getObject());
        if (null != getSort() && StringUtils.isNotBlank(getSort().getProperty())) {
            if (logger.isInfoEnabled()) {
                logger.info(String.format("sorting ascending=%s; value='%s'; ListSize=%s;",
                        getSort().isAscending(), getSort().getProperty(), tmpList.size()));
            }
            Collections.sort(tmpList, new Comparator<T>() {
                @Override
                public int compare(T o1, T o2) {
                    PropertyModel o1Property = new PropertyModel<Comparable>(o1, getSort().getProperty());
                    PropertyModel o2Property = new PropertyModel<Comparable>(o2, getSort().getProperty());
                    return (getSort().isAscending() ? 1 : -1) * COMPARATOR.compare(o1Property.getObject(), o2Property.getObject());
                }
            });
        }

        return tmpList.subList((int) first, (int) Math.min(first + count, size())).iterator();
    }

    @Override
    public long size() {
        return list.getObject().size();
    }

    @Override
    public IModel<T> model(T object) {
        return Model.of(object);
    }
}
