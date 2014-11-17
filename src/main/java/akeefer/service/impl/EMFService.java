package akeefer.service.impl;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 * Created by akeefer on 17.11.14.
 */
public class EMFService {

    private static final EntityManagerFactory emfInstance = Persistence
            .createEntityManagerFactory("transactions-optional");

    private EMFService() {
    }

    public static EntityManagerFactory get() {
        return emfInstance;
    }
}
