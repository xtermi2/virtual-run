package akeefer.web.components.layout;

import akeefer.Nonnull;
import org.apache.commons.lang3.Validate;
import org.apache.wicket.markup.ComponentTag;

import static org.apache.commons.lang3.StringUtils.*;

public final class LayoutUtils {

    public static final String CLASS_ATTRIBUTE = "class";
    public static final String STYLE_ATTRIBUTE = "style";
    public static final String TITLE_ATTRIBUTE = "title";
    public static final String MAXLENGTH_ATTRIBUTE = "maxlength";

    public static final String CLASS_SEPARATOR = " ";

    public static final String FIRSTCHILD_CLASS = "first-child";
    public static final String LASTCHILD_CLASS = "last-child";
    public static final String STATECURRENT_CLASS = "state-current";
    public static final String STATEPREVIOUS_CLASS = "state-previous";
    public static final String STATEACTIVE_CLASS = "state-active";
    public static final String STATENEXT_CLASS = "state-next";
    public static final String STATEBEHIND_CLASS = "state-behind";

    public static final String TABS_CLASS = "tabs";
    public static final String PROCESS_FIRSTLAYER_CLASS = "nav-firstlayer";
    public static final String PROCESS_SECONDLAYER_CLASS = "nav-secondlayer";

    public static final String ERRORSTATE_CLASS = "state-error";
    public static final String WARNSTATE_CLASS = "state-warning";
    public static final String INFOSTATE_CLASS = "state-info";
    public static final String REQUIREDSTATE_CLASS = "required";
    public static final String LOCKEDSTATE_CLASS = "state-locked";
    public static final String DISABLEDSTATE_CLASS = "state-disabled";
    public static final String IMPORTANTSTATE_CLASS = "action-user";

    public static final String TABLE_ALIGN_RIGHT_CLASS = "align-right";
    public static final String TABLE_ALIGN_LEFT_CLASS = "align-left";
    public static final String TABLE_ALIGN_CENTER_CLASS = "align-center";

    public static final String INPUT_NUMBER_CLASS = "input-number";

    public static final String BUTTON_SUBMITTYPE_CLASS = "type-submit";
    public static final String BUTTON_RESETTYPE_CLASS = "type-reset";

    private LayoutUtils() {
    }

    /**
     * Include or exclude a class in the class attribute.
     *
     * @param tag        The tag.
     * @param styleClass The style class. Not blank.
     * @param include    Should the class be included, i.e. added, or excluded, i.e. removed?
     */
    public static void modifyClass(@Nonnull final ComponentTag tag, @Nonnull final String styleClass, final boolean include) {
        modifyAttributeValue(tag, CLASS_ATTRIBUTE, styleClass, CLASS_SEPARATOR, include);
    }

    /**
     * switch a class in the class attribute.
     *
     * @param tag      The tag.
     * @param oldClass The old style class. Not blank.
     * @param newClass The new style class. Not blank.
     */
    public static void switchClass(@Nonnull final ComponentTag tag, @Nonnull final String oldClass, @Nonnull final String newClass) {
        switchAttributeValue(tag, CLASS_ATTRIBUTE, oldClass, newClass);
    }

    /**
     * switch a style in the style attribute.
     *
     * @param tag      The tag.
     * @param oldStyle The old style class. Not blank.
     * @param newStyle The new style class. Not blank.
     */
    public static void switchStyle(@Nonnull final ComponentTag tag, @Nonnull final String oldStyle, @Nonnull final String newStyle) {
        switchAttributeValue(tag, STYLE_ATTRIBUTE, oldStyle, newStyle);
    }

    /**
     * Include or exclude a style in the style attribute.
     *
     * @param tag        The tag.
     * @param styleValue The style value. Not blank.
     * @param include    Should the value be included, i.e. added, or excluded, i.e. removed?
     */
    public static void modifyStyle(@Nonnull final ComponentTag tag, @Nonnull final String styleValue, final boolean include) {
        modifyAttributeValue(tag, STYLE_ATTRIBUTE, styleValue, CLASS_SEPARATOR, include);
    }

    /**
     * change the title attribute
     *
     * @param tag      The tag.
     * @param newTitle new title
     */
    public static void switchTitle(@Nonnull final ComponentTag tag, final String newTitle) {
        tag.put(TITLE_ATTRIBUTE, (newTitle != null ? newTitle : ""));
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
    private static void modifyAttributeValue(@Nonnull final ComponentTag tag, @Nonnull final String attribute, @Nonnull final String value,
                                             @Nonnull final String separator, final boolean include) {
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

    /**
     * change a value in the attribute.
     *
     * @param tag       The tag.
     * @param attribute The attribute of which to modify the value. Not blank.
     * @param oldValue  The value to remove.
     * @param newValue  The value to insert.
     */
    private static void switchAttributeValue(@Nonnull final ComponentTag tag, @Nonnull final String attribute, @Nonnull final String oldValue,
                                             @Nonnull final String newValue) {
        Validate.isTrue(!isEmpty(attribute));
        Validate.isTrue(!isEmpty(oldValue));
        Validate.isTrue(!isEmpty(newValue));
        String classValue = tag.getAttribute(attribute);
        if (isBlank(classValue)) {
            classValue = newValue;
        } else {
            classValue = classValue.replace(oldValue + CLASS_SEPARATOR, EMPTY).trim();
            classValue = classValue.replace(oldValue, EMPTY).trim();
            classValue = classValue + CLASS_SEPARATOR + newValue;
        }
        tag.put(attribute, classValue);
    }
}
