package akeefer.repository.mongo.dto;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import javax.validation.constraints.Min;

@Value
@Builder(toBuilder = true)
public class AktivitaetSearchRequest {

    // SORTING

    @NonNull
    AktivitaetSortProperties sortProperty;

    boolean sortAsc;

    // PAGINATION

    @Min(0)
    int pageableFirstElement;

    @Min(1)
    int pageSize;

    // FILTERS

    @NonNull
    String owner;
}
