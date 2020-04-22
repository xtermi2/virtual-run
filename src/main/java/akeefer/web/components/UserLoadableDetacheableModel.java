package akeefer.web.components;

import akeefer.model.mongo.User;
import akeefer.service.PersonService;
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
    private final String userId;

    public UserLoadableDetacheableModel(String userId) {
        Validate.notNull(userId, "userId must not be null");
        Injector.get().inject(this);
        this.userId = userId;
    }

    @Override
    protected User load() {
        return personService.findUserById(userId);
    }
}
