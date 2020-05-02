package akeefer.web.components.layout;

import akeefer.Nonnull;
import org.apache.commons.lang3.Validate;
import org.apache.wicket.markup.ComponentTag;

import static org.apache.commons.lang3.StringUtils.*;

public final class LayoutUtils {

    public static final String CLASS_ATTRIBUTE = "class";

    public static final String CLASS_SEPARATOR = " ";

    private LayoutUtils() {
    }

    /**
     * Include or exclude a class in the class attribute.
     *
     * @param tag        The tag.
     * @param styleClass The style class. Not blank.
     * @param include    Should the class be included, i.e. added, or excluded, i.e. removed?
     */
    public static void modifyClass(@Nonnull ComponentTag tag, @Nonnull String styleClass, boolean include) {
        modifyAttributeValue(tag, CLASS_ATTRIBUTE, styleClass, CLASS_SEPARATOR, include);
    }

    /**
     * Include or exclude a value in the attribute.
     *
     * @param tag       The tag. Not <code>null</code>.
     * @param attribute The attribute of which to modify the value. Not <code>null</code> or blank.
     * @param value     The value. Not <code>null</code> or blank.
     * @param separator Separator for values. Not <code>null</code>.
     * @param include   Should the class be included, i.e. added, or excluded, i.e. removed?
     */
    private static void modifyAttributeValue(@Nonnull ComponentTag tag, @Nonnull String attribute, @Nonnull String value,
                                             @Nonnull String separator, boolean include) {
        Validate.isTrue(!isEmpty(attribute));
        Validate.isTrue(!isEmpty(value));
        String classValue = tag.getAttribute(attribute);
        if (include) {
            if (isBlank(classValue)) {
                classValue = value;
            } else {
                classValue = classValue + separator + value;
            }
        } else {
            if (isBlank(classValue)) {
                classValue = EMPTY;
            } else {
                classValue = classValue.replace(value + separator, EMPTY).trim();
                classValue = classValue.replace(value, EMPTY).trim();
            }
        }
        tag.put(attribute, classValue);
    }
}
