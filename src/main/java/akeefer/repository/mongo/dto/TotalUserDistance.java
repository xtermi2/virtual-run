package akeefer.repository.mongo.dto;

import akeefer.model.mongo.Aktivitaet;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.Value;

import java.math.BigDecimal;

/**
 * The total distance a user has tracked so far
 */
@Value
@AllArgsConstructor
public class TotalUserDistance {
    /**
     * the username
     */
    @NonNull
    String owner;
    @NonNull
    BigDecimal totalDistanzInKilometer;

    public int getDistanzInMeter() {
        return totalDistanzInKilometer.multiply(Aktivitaet.TAUSEND).intValue();
    }
}
