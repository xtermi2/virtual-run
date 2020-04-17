package akeefer.model.mongo;

import akeefer.model.AktivitaetsAufzeichnung;
import akeefer.model.AktivitaetsTyp;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.joda.time.DateTime;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;
import javax.persistence.Transient;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

@Document(collection = "activities")
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class Aktivitaet {

    public static final BigDecimal TAUSEND = BigDecimal.valueOf(1000L);

    @Id
    @EqualsAndHashCode.Include
    private String id;

    @NotNull
    @NonNull
    @DecimalMin(value = "0.001")
    @Max(value = 1000)
    private BigDecimal distanzInKilometer;

    @NotNull
    @NonNull
    private AktivitaetsTyp typ;

    @Past
    private Date aktivitaetsDatum;

    // wird nur ein mal initial gesetzt
    private Date eingabeDatum;

    // wird immer aktualisiert, wenn die Aktivitaet bearbeitet wurde.
    private Date updatedDatum;

    @NotNull
    @NonNull
    private AktivitaetsAufzeichnung aufzeichnungsart;

    private String bezeichnung;

    private String owner;

    @Transient
    @JsonIgnore
    public Integer getDistanzInMeter() {
        if (null != distanzInKilometer) {
            return distanzInKilometer.multiply(TAUSEND).intValue();
        }
        return null;
    }

    public Aktivitaet setDistanzInMeter(Integer meter) {
        if (null == meter) {
            distanzInKilometer = null;
        } else {
            this.distanzInKilometer = new BigDecimal(meter).divide(TAUSEND, 3, RoundingMode.HALF_UP);
        }
        return this;
    }

    @Transient
    @JsonIgnore
    public DateTime getEingabeDatumAsDateTime() {
        return null == eingabeDatum ? null : new DateTime(eingabeDatum);
    }

    public Aktivitaet setEingabeDatumAsDateTime(DateTime eingabeDatum) {
        this.eingabeDatum = eingabeDatum.toDate();
        return this;
    }

    public void setUser(akeefer.model.mongo.User user) {
        if (null == user) {
            this.owner = null;
        } else {
            this.owner = user.getUsername();
        }
    }

    public Aktivitaet cloneWithoutUser() {
        return this.toBuilder()
                .build();
    }
}