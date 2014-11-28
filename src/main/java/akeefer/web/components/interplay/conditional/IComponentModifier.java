package akeefer.web.components.interplay.conditional;

import org.apache.wicket.Component;

/**
 * Interface für Bausteine, die GUI-Komponenten modifizieren.
 */
public interface IComponentModifier {

    /**
     * Modifiziert die übergebene Komponente.
     *
     * @param component Komponente.
     */
    void modify(Component component);
}
