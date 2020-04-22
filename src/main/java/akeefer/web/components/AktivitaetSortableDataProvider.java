package akeefer.web.components;

import akeefer.model.mongo.Aktivitaet;
import akeefer.repository.mongo.dto.AktivitaetSearchRequest;
import akeefer.repository.mongo.dto.AktivitaetSortProperties;
import akeefer.service.PersonService;
import lombok.NonNull;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.util.Iterator;
import java.util.List;

public class AktivitaetSortableDataProvider extends SortableDataProvider<Aktivitaet, AktivitaetSortProperties> {

    private final String username;
    private final PersonService personService;
    private Long size;

    public AktivitaetSortableDataProvider(@NonNull String username,
                                          @NonNull PersonService personService) {
        this.username = username;
        this.personService = personService;
    }

    @Override
    public Iterator<? extends Aktivitaet> iterator(long first, long count) {
        List<Aktivitaet> res = personService.searchActivities(AktivitaetSearchRequest.builder()
                .sortProperty(getSort().getProperty())
                .sortAsc(getSort().isAscending())
                .pageableFirstElement((int) first)
                .pageSize((int) count)
                .owner(username)
                .build());
        return res.iterator();
    }

    @Override
    public long size() {
        if (null == size) {
            size = personService.countActivities(username);
        }
        return size;
    }

    @Override
    public IModel<Aktivitaet> model(Aktivitaet object) {
        return Model.of(object);
    }

    @Override
    public void detach() {
        size = null;
    }
}
