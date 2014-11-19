package akeefer.model;

import com.google.appengine.api.datastore.Key;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.*;
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
    private Integer meter;
    private AktivitaetsTyp typ;
    private Date aktivitaetsDatum;
    private Date eingabeDatum;
    private AktivitaetsAufzeichnung aufzeichnungsart;
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;
    private String bezeichnung;

    public Key getId() {
        return id;
    }

    public void setId(Key id) {
        this.id = id;
    }

    public Integer getMeter() {
        return meter;
    }

    public void setMeter(Integer meter) {
        this.meter = meter;
    }

    @Transient
    public BigDecimal getKilometer() {
        if(null == meter){
            return null;
        }
        return new BigDecimal(meter).divide(TAUSEND, 2, RoundingMode.HALF_UP);
    }

    public void setKilometer(BigDecimal km) {
        if (null != km) {
            this.meter = km.multiply(TAUSEND).intValue();
        } else {
            this.meter = null;
        }
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
    }

    public String getBezeichnung() {
        return bezeichnung;
    }

    public void setBezeichnung(String bezeichnung) {
        this.bezeichnung = bezeichnung;
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
}
