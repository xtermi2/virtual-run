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

    @NonNull
    boolean sortAsc;

    // PAGINATION

    @NonNull
    @Min(0)
    int pageableFirstElement;

    @NonNull
    @Min(1)
    int pageSize;

    // FILTERS

    @NonNull
    String owner;
}
