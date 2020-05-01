package akeefer.repository.mongo.dto;

import akeefer.model.AktivitaetsTyp;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.Value;

import java.math.BigDecimal;

/**
 * The summed distance a user has tracked, grouped by typ
 */
@Value
@AllArgsConstructor
public class UserDistanceByType {
    /**
     * the username
     */
    @NonNull
    String owner;
    @NonNull
    AktivitaetsTyp typ;
    @NonNull
    BigDecimal totalDistanzInKilometer;
}
