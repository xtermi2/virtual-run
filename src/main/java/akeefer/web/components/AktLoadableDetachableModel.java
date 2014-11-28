package akeefer.web.components;

import akeefer.model.Aktivitaet;
import akeefer.service.PersonService;
import com.google.appengine.api.datastore.Key;
import org.apache.commons.lang3.Validate;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * loads all Aktivitaeten from Database
 */
public class AktLoadableDetachableModel extends LoadableDetachableModel<List<Aktivitaet>> {

    private static final Logger logger = LoggerFactory.getLogger(AktLoadableDetachableModel.class);

    @SpringBean
    private PersonService personService;

    private Key userId;

    public AktLoadableDetachableModel(Key userId) {
        Validate.notNull(userId, "userId must not be null");
        Injector.get().inject(this);
        this.userId = userId;
    }

    @Override
    protected List<Aktivitaet> load() {
        return personService.loadAktivitaeten(userId);
    }

    @Override
    protected void onDetach() {
        logger.info("onDetach");
    }

    @Override
    protected void onAttach() {
        logger.info("onAttach");
    }
}
