package akeefer.web.components.layout;

import org.apache.wicket.model.IModel;

@SuppressWarnings("serial")
public class Panel<T> extends org.apache.wicket.markup.html.panel.Panel {

    public Panel(String id) {
        super(id);
    }

    public Panel(String id, IModel<? extends T> model) {
        super(id, model);
    }

    @SuppressWarnings("unchecked")
    public IModel<T> getModel() {
        return (IModel<T>) getDefaultModel();
    }

    public Panel<T> setModel(IModel<T> model) {
        this.setDefaultModel(model);
        return this;
    }

    @SuppressWarnings("unchecked")
    public T getModelObject() {
        return (T) getDefaultModelObject();
    }

    public void setModelObject(T object) {
        setDefaultModelObject(object);
    }
}
