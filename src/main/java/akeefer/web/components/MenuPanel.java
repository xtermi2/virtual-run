package akeefer.web.components;

import akeefer.web.pages.*;
import org.apache.wicket.authroles.authentication.pages.SignInPage;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;

public class MenuPanel extends Panel {

    public MenuPanel(final String id,
                     final boolean mapView,
                     final boolean aktView,
                     final boolean userView,
                     final boolean statisticView) {
        super(id);

        final Link mapButton = new Link("mapButton") {
            @Override
            public void onClick() {
                setResponsePage(MapPage.class);
            }
        };
        mapButton.setEnabled(!mapView);
        add(mapButton);

        final Button personenPassendZoomen = new Button("personenPassendZoomen");
        personenPassendZoomen.setVisible(mapView);
        add(personenPassendZoomen);

        final Button gesamtansicht = new Button("gesamtansicht");
        gesamtansicht.setVisible(mapView);
        add(gesamtansicht);

        final Link aktUebersicht = new Link("aktUebersicht") {
            @Override
            public void onClick() {
                setResponsePage(AktUebersichtPage.class);
            }
        };
        aktUebersicht.setEnabled(!aktView);
        add(aktUebersicht);

        final Link neueAktivitaet = new Link("neueAktivitaet") {
            @Override
            public void onClick() {
                setResponsePage(AktEditPage.class);
            }
        };
        neueAktivitaet.setVisible(aktView);
        add(neueAktivitaet);

        final Link userSettings = new Link("userSettings") {
            @Override
            public void onClick() {
                setResponsePage(UserDetailsPage.class);
            }
        };
        userSettings.setEnabled(!userView);
        add(userSettings);

        final Link statistics = new Link("statistics") {
            @Override
            public void onClick() {
                setResponsePage(StatisticPage.class);
            }
        };
        statistics.setEnabled(!statisticView);
        add(statistics);

        // der Logout Button ist immer sichtbar
        add(new Link("logoutButton") {
            @Override
            public void onClick() {
                getSession().invalidate();
                setResponsePage(SignInPage.class);
            }
        });
    }
}
