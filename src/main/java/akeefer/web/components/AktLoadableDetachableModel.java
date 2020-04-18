package akeefer.web.components;

import akeefer.model.mongo.Aktivitaet;
import akeefer.service.PersonService;
import org.apache.commons.lang3.Validate;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.List;

/**
 * loads all Aktivitaeten from Database
 */
public class AktLoadableDetachableModel extends LoadableDetachableModel<List<Aktivitaet>> {

    @SpringBean
    private PersonService personService;

    private String userId;

    public AktLoadableDetachableModel(String userId) {
        Validate.notNull(userId, "userId must not be null");
        Injector.get().inject(this);
        this.userId = userId;
    }

    @Override
    protected List<Aktivitaet> load() {
        return personService.loadAktivitaeten(userId);
    }
}
