package akeefer.repository;

import akeefer.model.Aktivitaet;
import akeefer.model.User;
import com.google.appengine.api.datastore.Key;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AktivitaetRepository extends JpaRepository<Aktivitaet, Key> {

    List<Aktivitaet> findByUser(User user);
}
