package akeefer.web.components.interplay.conditional;

import org.apache.wicket.model.IModel;

/**
 * Interface zur Beschreibung von Conditionals (Wahr/Unwahr Aussagen).<br/>
 * Conditionals evaluieren ihre Aussage auf Basis von Modells.
 * In Verbindung mit {@link ConditionalModification}s können Conditionals verwendet werden, um die
 * Zustände von GUI-Bausteinen und deren Models zu beeinflussen.<br/>
 * Für viele Aussagen gibt es im {@link SimpleConditional} Factory-Methoden.<br/>
 * <strong>Beispiel</strong><code><pre>
 * Person person = new Person();
 * person.setAlter(24);
 * <p/>
 * // gewrappter Zugriff über ein PropertyModel.
 * IConditional&lt;Integer> minderjaehrig
 *    = new {@link AbstractConditional}&lt;String>(new PropertyModel&lt;Integer>(person, "alter")) {
 *      // hier anonym, kann aber auch als konkrete, eigenständige Klasse implementiert werden
 *      public boolean isFulfilled(Integer modelObject) {
 *          if (modelObject == null) {
 *              return false;
 *          }
 *          return modelObject &lt; 18;
 *      }
 * };
 * HinweisFuerVolljaehrigePanel hinweis = new HinweisFuerVolljaehrigePanel("hinweis");
 * add(hinweis);
 * hinweis.add({@link ConditionalModification}.hideIf(minderjaehrig));
 * </pre></code>
 *
 * @param <T> Modelobject-Typ.
 */
public interface IConditional<T> extends IModel<T> {

    /**
     * Prüft, ob die Bedingung erfüllt ist.
     *
     * @return <code>true</code>, wenn die Bedingung erfüllt ist, sonst <code>false</code>.
     */
    boolean isFulfilled();
}