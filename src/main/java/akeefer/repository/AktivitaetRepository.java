package akeefer.repository;

import akeefer.model.Aktivitaet;
import com.google.appengine.api.datastore.Key;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AktivitaetRepository extends JpaRepository<Aktivitaet, Key>, AktivitaetRepositoryCustom {

}
