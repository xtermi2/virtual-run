package akeefer.web.pages;

import akeefer.model.Aktivitaet;
import akeefer.web.VRSession;
import akeefer.web.components.GenericSortableDataProvider;
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
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class AktUebersichtPage extends AbstractAuthenticatedBasePage {

    private static final Logger logger = LoggerFactory.getLogger(AktUebersichtPage.class);

    private Aktivitaet selected;

    public AktUebersichtPage(PageParameters parameters) {
        super(parameters, false, true);

        List<IColumn<Aktivitaet, String>> columns = new ArrayList<IColumn<Aktivitaet, String>>();
        columns.add(new PropertyColumn<Aktivitaet, String>(new Model<String>("Distanz (km)"), "distanzInKilometer", "distanzInKilometer"));
        columns.add(new PropertyColumn<Aktivitaet, String>(new Model<String>("Typ"), "typ", "typ"));
        columns.add(new PropertyColumn<Aktivitaet, String>(new Model<String>("Bezeichnung"), "bezeichnung", "bezeichnung"));
        columns.add(new PropertyColumn<Aktivitaet, String>(new Model<String>("Datum"), "aktivitaetsDatum", "aktivitaetsDatum"));
        columns.add(new PropertyColumn<Aktivitaet, String>(new Model<String>("Aufzeichnungsart"), "aufzeichnungsart", "aufzeichnungsart"));
        columns.add(new AbstractColumn<Aktivitaet, String>(new Model<String>("Aktionen")) {
            public void populateItem(Item<ICellPopulator<Aktivitaet>> cellItem, String componentId,
                                     IModel<Aktivitaet> model) {
                cellItem.add(new ActionPanel(componentId, model));
            }
        });
        GenericSortableDataProvider<Aktivitaet> dataProvider = new GenericSortableDataProvider<Aktivitaet>(VRSession.get().getUser().getAktivitaeten());
        dataProvider.setSort("aktivitaetsDatum", SortOrder.DESCENDING);
        add(new AjaxFallbackDefaultDataTable<Aktivitaet, String>("table", columns, dataProvider, 5));
    }

    public Aktivitaet getSelected() {
        return selected;
    }

    public void setSelected(Aktivitaet selected) {
        this.selected = selected;
    }

    class ActionPanel extends Panel {
        public ActionPanel(String id, IModel<Aktivitaet> model) {
            super(id, model);
            add(new Link("edit") {
                @Override
                public void onClick() {
                    selected = (Aktivitaet) getParent().getDefaultModelObject();
                    logger.info(String.format("selected Akt createdDate='%s' bezeichnung='%s' id=%s",
                            selected.getEingabeDatum(), selected.getBezeichnung(), selected.getId()));
                    setResponsePage(new AktEditPage(getPageParameters(), (IModel<Aktivitaet>) getParent().getDefaultModel()));
                }
            });
        }
    }


}
