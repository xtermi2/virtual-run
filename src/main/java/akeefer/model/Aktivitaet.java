package akeefer.model;

import com.google.appengine.api.datastore.Key;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.*;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

@Entity
public class Aktivitaet implements Serializable {

    private static final long serialVersionUID = 0;
    public static final BigDecimal TAUSEND = BigDecimal.valueOf(1000L);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // hier muss man einen Key verwenden, da ein Eingebetteter Typ (User#aktivitaeten) nicht mit einem Long als PK funktioniert
    private Key id;

    @NotNull
    @DecimalMin(value = "0.001")
    @Max(value = 1000)
    private BigDecimal distanzInKilometer;

    @NotNull
    private AktivitaetsTyp typ;

    @Past
    private Date aktivitaetsDatum;

    // wird nur ein mal initial gesetzt
    private Date eingabeDatum;

    // wird immer aktualisiert, wenn die Aktivitaet bearbeitet wurde.
    private Date updatedDatum;

    @NotNull
    private AktivitaetsAufzeichnung aufzeichnungsart;

    @ManyToOne(fetch = FetchType.EAGER)
    private User user;

    private String bezeichnung;

    private String owner;

    public Key getId() {
        return id;
    }

    public void setId(Key id) {
        this.id = id;
    }

    @Transient
    public Integer getDistanzInMeter() {
        if (null != distanzInKilometer) {
            return distanzInKilometer.multiply(TAUSEND).intValue();
        }
        return null;
    }

    public void setDistanzInMeter(Integer meter) {
        if (null == meter) {
            distanzInKilometer = null;
        } else {
            this.distanzInKilometer = new BigDecimal(meter).divide(TAUSEND, 3, RoundingMode.HALF_UP);
        }
    }

    public BigDecimal getDistanzInKilometer() {
        return this.distanzInKilometer;
    }

    public void setDistanzInKilometer(BigDecimal kilometer) {
        this.distanzInKilometer = kilometer;
    }


    public AktivitaetsTyp getTyp() {
        return typ;
    }

    public void setTyp(AktivitaetsTyp typ) {
        this.typ = typ;
    }

    public Date getAktivitaetsDatum() {
        return aktivitaetsDatum;
    }

    public void setAktivitaetsDatum(Date aktivitaetsDatum) {
        this.aktivitaetsDatum = aktivitaetsDatum;
    }

    public Date getEingabeDatum() {
        return eingabeDatum;
    }

    public void setEingabeDatum(Date eingabeDatum) {
        this.eingabeDatum = eingabeDatum;
    }

    public Date getUpdatedDatum() {
        return updatedDatum;
    }

    public void setUpdatedDatum(Date updatedDatum) {
        this.updatedDatum = updatedDatum;
    }

    public AktivitaetsAufzeichnung getAufzeichnungsart() {
        return aufzeichnungsart;
    }

    public void setAufzeichnungsart(AktivitaetsAufzeichnung aufzeichnungsart) {
        this.aufzeichnungsart = aufzeichnungsart;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
        if (null == user) {
            this.owner = null;
        } else {
            this.owner = user.getUsername();
        }
    }

    public String getBezeichnung() {
        return bezeichnung;
    }

    public void setBezeichnung(String bezeichnung) {
        this.bezeichnung = bezeichnung;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj.getClass() != getClass()) {
            return false;
        }
        Aktivitaet rhs = (Aktivitaet) obj;
        return new EqualsBuilder()
                .append(this.id, rhs.id)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(id)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("distanzInKilometer", distanzInKilometer)
                .append("typ", typ)
                .append("aktivitaetsDatum", aktivitaetsDatum)
                .append("eingabeDatum", eingabeDatum)
                .append("updatedDatum", updatedDatum)
                .append("bezeichnung", bezeichnung)
                .append("owner", owner)
                .toString();
    }
}
