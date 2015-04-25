package akeefer.web.models;

import org.apache.wicket.Component;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.lang.Classes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.apache.commons.lang3.Validate.notNull;

/**
 * Property Model which resolves Enum String from resource file (SimpleClassName.name)
 * <p/>
 * User: Andreas Keefer, Senacor Technologies AG
 * Date: 25.04.15
 * Time: 17:45
 *
 * @author Andreas Keefer (andreas.keefer@senacor.com), Senacor Technologies AG
 */
public class EnumPropertyModel<T extends Enum> extends PropertyModel {

    private static final Logger logger = LoggerFactory.getLogger(EnumPropertyModel.class);

    private Component component;

    public EnumPropertyModel(Object modelObject, String expression, Component resourceProvider) {
        super(modelObject, expression);
        notNull(resourceProvider, "resourceProvider must not be null");
        this.component = resourceProvider;
    }

    @Override
    public String getObject() {
        Object object = super.getObject();
        if (null != object) {
            T enumValue = (T) object;
            String key = resourceKey(enumValue);
            return component.getString(key, null, enumValue.name());
        }
        return null;
    }

    protected String resourceKey(T object) {
        return Classes.simpleName(object.getDeclaringClass()) + '.' + object.name();
    }
}