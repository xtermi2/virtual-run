package akeefer.web.components;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.panel.Panel;

public abstract class ModalDialog extends ModalWindow {

    public ModalDialog(String id) {
        super(id);
        setContent(new DialogPanel(getContentId()));
        setResizable(false);
        //setInitialHeight(50);
        //setMinimalHeight(50);
        setInitialWidth(170);
        //setMinimalWidth(100);
        setMaskType(MaskType.SEMI_TRANSPARENT);
        setUseInitialHeight(false);
        setWindowClosedCallback(new WindowClosedCallback() {
            @Override
            public void onClose(AjaxRequestTarget target) {
                ModalDialog.this.onClose(target);
            }
        });
    }


    protected abstract void onCancel(AjaxRequestTarget target);

    protected abstract void onOK(AjaxRequestTarget target);

    protected abstract void onClose(AjaxRequestTarget target);

    class DialogPanel extends Panel {
        public DialogPanel(String id) {
            super(id);
            add(new AjaxLink("ok") {
                @Override
                public void onClick(AjaxRequestTarget target) {
                    ModalDialog.this.onOK(target);
                }
            });
            add(new AjaxLink("cancel") {
                @Override
                public void onClick(AjaxRequestTarget target) {
                    ModalDialog.this.onCancel(target);
                }
            });
        }
    }

    @Override
    public void show(AjaxRequestTarget pTarget) {
        super.show(pTarget);

        // TODO (ak) entfernt den close-Button oben rechts, funktioniert aber nicht
//        pTarget.appendJavaScript(""//
//                + "var thisWindow = Wicket.Window.get();"//
//                + "if (thisWindow) {"//
//                + "  $('.w_close').attr('style', 'display:none;');"//
//                + "}"//
//        );
    }
}
