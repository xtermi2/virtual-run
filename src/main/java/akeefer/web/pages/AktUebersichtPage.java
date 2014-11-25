package akeefer.web.pages;

import akeefer.model.Aktivitaet;
import akeefer.service.PersonService;
import akeefer.web.VRSession;
import akeefer.web.components.GenericSortableDataProvider;
import akeefer.web.components.ModalDialog;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxFallbackDefaultDataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class AktUebersichtPage extends AbstractAuthenticatedBasePage {

    private static final Logger logger = LoggerFactory.getLogger(AktUebersichtPage.class);
    private final IModel<Aktivitaet> delAkt = new Model<>(null);

    @SpringBean
    private PersonService personService;

    public AktUebersichtPage(PageParameters parameters) {
        super(parameters, false, true);

        List<IColumn<Aktivitaet, String>> columns = new ArrayList<IColumn<Aktivitaet, String>>();
        columns.add(new PropertyColumn<Aktivitaet, String>(new Model<String>("Distanz (km)"), "distanzInKilometer", "distanzInKilometer"));
        columns.add(new PropertyColumn<Aktivitaet, String>(new Model<String>("Typ"), "typ", "typ"));
        columns.add(new PropertyColumn<Aktivitaet, String>(new Model<String>("Bezeichnung"), "bezeichnung", "bezeichnung"));
        columns.add(new PropertyColumn<Aktivitaet, String>(new Model<String>("Datum"), "aktivitaetsDatum", "aktivitaetsDatum"));
        columns.add(new PropertyColumn<Aktivitaet, String>(new Model<String>("Aufzeichnungsart"), "aufzeichnungsart", "aufzeichnungsart"));
        final GenericSortableDataProvider<Aktivitaet> dataProvider = new GenericSortableDataProvider<Aktivitaet>(new PropertyModel<List<Aktivitaet>>(VRSession.get().getUser(), "aktivitaeten"));
        dataProvider.setSort("aktivitaetsDatum", SortOrder.DESCENDING);
        final AjaxFallbackDefaultDataTable<Aktivitaet, String> table = new AjaxFallbackDefaultDataTable<Aktivitaet, String>("table", columns, dataProvider, 3);

        final ModalDialog delDialog = new ModalDialog("delDialog") {
            @Override
            protected void onCancel(AjaxRequestTarget target) {
                delAkt.setObject(null);
            }

            @Override
            protected void onOK(AjaxRequestTarget target) {
                personService.deleteAktivitaet(VRSession.get().getUser().getObject(), delAkt.getObject());
                if (null != target) {
                    logger.info("adding table to target");
                    target.add(table);
                } else {
                    logger.info("target ist null :(");
                }
                delAkt.setObject(null);
                close(target);
            }
        };
        delDialog.setTitle("Wirklich Löschen?");
        add(delDialog);

        columns.add(new AbstractColumn<Aktivitaet, String>(new Model<String>("Aktionen")) {
            public void populateItem(Item<ICellPopulator<Aktivitaet>> cellItem, String componentId,
                                     IModel<Aktivitaet> model) {
                cellItem.add(new ActionPanel(componentId, model) {
                    @Override
                    void onDelete(AjaxRequestTarget target, IModel<Aktivitaet> akt) {
                        delAkt.setObject(akt.getObject());
                        delDialog.show(target);
                    }
                });
            }
        });

        add(table);
    }

    abstract class ActionPanel extends Panel {
        public ActionPanel(String id, IModel<Aktivitaet> model) {
            super(id, model);
            add(new Link("edit") {
                @Override
                public void onClick() {
                    Aktivitaet selected = (Aktivitaet) getParent().getDefaultModelObject();
                    logger.info(String.format("selected Akt createdDate='%s' bezeichnung='%s' id=%s",
                            selected.getEingabeDatum(), selected.getBezeichnung(), selected.getId()));
                    setResponsePage(new AktEditPage(getPageParameters(), (IModel<Aktivitaet>) getParent().getDefaultModel()));
                }
            });
            add(new AjaxFallbackLink("delete") {
                @Override
                public void onClick(AjaxRequestTarget target) {
                    onDelete(target, (IModel<Aktivitaet>) getParent().getDefaultModel());
                }
            });
        }

        abstract void onDelete(AjaxRequestTarget target, IModel<Aktivitaet> akt);
    }


}
