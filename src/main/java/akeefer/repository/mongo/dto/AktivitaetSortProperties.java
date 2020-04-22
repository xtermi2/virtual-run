package akeefer.repository.mongo.dto;

import akeefer.model.mongo.Aktivitaet;

public enum AktivitaetSortProperties {

    DISTANZ_IN_KILOMETER(Aktivitaet.FIELD_DISTANZ_IN_KILOMETER),
    TYP(Aktivitaet.FIELD_TYP),
    AKTIVITAETS_DATUM(Aktivitaet.FIELD_AKTIVITAETS_DATUM),
    AUFZEICHNUNGSART(Aktivitaet.FIELD_AUFZEICHNUNGSART),
    BEZEICHNUNG(Aktivitaet.FIELD_BEZEICHNUNG);

    private String fieldName;

    AktivitaetSortProperties(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldName() {
        return fieldName;
    }
}
