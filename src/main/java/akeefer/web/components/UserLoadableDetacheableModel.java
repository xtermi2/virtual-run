package akeefer.web.components;

import akeefer.model.User;
import akeefer.service.PersonService;
import com.google.appengine.api.datastore.Key;
import org.apache.commons.lang3.Validate;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * Loads User from Database
 */
public class UserLoadableDetacheableModel extends LoadableDetachableModel<User> {

    @SpringBean
    private PersonService personService;
    private Key userId;

    public UserLoadableDetacheableModel(Key userId) {
        Validate.notNull(userId, "userId must not be null");
        Injector.get().inject(this);
        this.userId = userId;
    }

    @Override
    protected User load() {
        return personService.findUserById(userId);
    }
}
