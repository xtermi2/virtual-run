package akeefer.model.mongo;

import akeefer.model.AktivitaetsAufzeichnung;
import akeefer.model.AktivitaetsTyp;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.joda.time.DateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.UUID;

@Document(collection = "activities")
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@CompoundIndexes({
        @CompoundIndex(name = "owner_distanzInKilometer", def = "{'owner' : 1, 'distanzInKilometer': 1}")
})
public class Aktivitaet implements Serializable {

    public static final BigDecimal TAUSEND = BigDecimal.valueOf(1_000L);
    public static final String FIELD_DISTANZ_IN_KILOMETER = "distanzInKilometer";
    public static final String FIELD_TYP = "typ";
    public static final String FIELD_AKTIVITAETS_DATUM = "aktivitaetsDatum";
    public static final String FIELD_AUFZEICHNUNGSART = "aufzeichnungsart";
    public static final String FIELD_BEZEICHNUNG = "bezeichnung";

    @Id
    @EqualsAndHashCode.Include
    private String id;

    @NotNull
    @NonNull
    @DecimalMin(value = "0.001")
    @Max(value = 1000)
    @Field(FIELD_DISTANZ_IN_KILOMETER)
    private BigDecimal distanzInKilometer;

    @NotNull
    @NonNull
    @Field(FIELD_TYP)
    private AktivitaetsTyp typ;

    @Past
    @Field(FIELD_AKTIVITAETS_DATUM)
    private Date aktivitaetsDatum;

    // wird nur ein mal initial gesetzt
    private Date eingabeDatum;

    // wird immer aktualisiert, wenn die Aktivitaet bearbeitet wurde.
    private Date updatedDatum;

    @NotNull
    @NonNull
    @Field(FIELD_AUFZEICHNUNGSART)
    private AktivitaetsAufzeichnung aufzeichnungsart;

    @Field(FIELD_BEZEICHNUNG)
    private String bezeichnung;

    @Indexed
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

    public void setUser(User user) {
        if (null == user) {
            this.owner = null;
        } else {
            this.owner = user.getUsername();
        }
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setIdFromUUID(UUID id) {
        this.id = id.toString();
    }
}
