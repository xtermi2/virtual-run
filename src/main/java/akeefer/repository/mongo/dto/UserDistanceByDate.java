package akeefer.repository.mongo.dto;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.Value;

import java.math.BigDecimal;

/**
 * The summed distance a user has tracked, grouped by date
 */
@Value
@AllArgsConstructor
public class UserDistanceByDate {
    /**
     * the username
     */
    @NonNull
    String owner;
    @NonNull
    String dateKey;
    @NonNull
    BigDecimal totalDistanzInKilometer;
}
