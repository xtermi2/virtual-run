package akeefer.web.pages;

import akeefer.model.Aktivitaet;
import akeefer.service.PersonService;
import akeefer.web.VRSession;
import akeefer.web.components.AktLoadableDetachableModel;
import akeefer.web.components.GenericSortableDataProvider;
import com.visural.wicket.component.dialog.Dialog;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxFallbackDefaultDataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class AktUebersichtPage extends AbstractAuthenticatedBasePage {

    private static final Logger logger = LoggerFactory.getLogger(AktUebersichtPage.class);

    @SpringBean
    private PersonService personService;

    public AktUebersichtPage(PageParameters parameters) {
        super(parameters, false, true, false, false);
        setDefaultModel(new Model<Aktivitaet>(null));
        final IModel<List<Aktivitaet>> aktivitaetenModel = new AktLoadableDetachableModel(VRSession.get().getUser().getId());
        List<IColumn<Aktivitaet, String>> columns = new ArrayList<IColumn<Aktivitaet, String>>();
        columns.add(new PropertyColumn<Aktivitaet, String>(new Model<String>("Distanz (km)"), "distanzInKilometer", "distanzInKilometer"));
        columns.add(new PropertyColumn<Aktivitaet, String>(new Model<String>("Typ"), "typ", "typ"));
        columns.add(new PropertyColumn<Aktivitaet, String>(new Model<String>("Bezeichnung"), "bezeichnung", "bezeichnung"));
        columns.add(new PropertyColumn<Aktivitaet, String>(new Model<String>("Datum"), "aktivitaetsDatum", "aktivitaetsDatum"));
        columns.add(new PropertyColumn<Aktivitaet, String>(new Model<String>("Aufzeichnungsart"), "aufzeichnungsart", "aufzeichnungsart"));
        final GenericSortableDataProvider<Aktivitaet> dataProvider = new GenericSortableDataProvider<Aktivitaet>(aktivitaetenModel);
        dataProvider.setSort("aktivitaetsDatum", SortOrder.DESCENDING);
        final AjaxFallbackDefaultDataTable<Aktivitaet, String> table = new AjaxFallbackDefaultDataTable<Aktivitaet, String>("table", columns, dataProvider, 10);
        table.setDefaultModel(aktivitaetenModel);
        columns.add(new AbstractColumn<Aktivitaet, String>(new Model<String>("Aktionen")) {
            public void populateItem(Item<ICellPopulator<Aktivitaet>> cellItem, String componentId,
                                     IModel<Aktivitaet> model) {
                cellItem.add(new ActionPanel(componentId, model) {
                    @Override
                    void onDelete(AjaxRequestTarget target, IModel<Aktivitaet> akt) {
                        personService.deleteAktivitaet(VRSession.get().getUser(), akt.getObject());
                        target.add(table);

                    }
                });
            }
        });
        add(table);
    }

    abstract class ActionPanel extends Panel {
        public ActionPanel(String id, IModel<Aktivitaet> model) {
            super(id, model);
            final Dialog dialog = new Dialog("dialog");
            add(dialog);

            add(new Link<Aktivitaet>("edit", model) {
                @Override
                public void onClick() {
                    Aktivitaet selected = getModelObject();
                    logger.info(String.format("edit Akt createdDate='%s' bezeichnung='%s' id=%s",
                            selected.getEingabeDatum(), selected.getBezeichnung(), selected.getId()));
                    setResponsePage(new AktEditPage(getPageParameters(), getModel()));
                }
            });
            add(new WebMarkupContainer("delete").add(dialog.getClickToOpenBehaviour()));
            final AjaxLink<Aktivitaet> deleteButton = new AjaxLink<Aktivitaet>("yesButton", model) {
                @Override
                public void onClick(AjaxRequestTarget target) {
                    onDelete(target, getModel());
                    dialog.close(target);
                }
            };
            dialog.add(deleteButton);
            dialog.add(new WebMarkupContainer("noButton").add(dialog.getClickToCloseBehaviour()));
        }

        abstract void onDelete(AjaxRequestTarget target, IModel<Aktivitaet> akt);
    }

}
