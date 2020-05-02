package akeefer.service.dto;

import lombok.*;
import org.joda.time.LocalDate;

import java.math.BigDecimal;
import java.util.NavigableMap;

@Value
@Builder(toBuilder = true)
@AllArgsConstructor
public class UserForecast {
    @NonNull
    String username;
    @NonNull
    @Singular("aggregatedDistancePerDay")
    NavigableMap<LocalDate, BigDecimal> aggregatedDistancesPerDay;
}
