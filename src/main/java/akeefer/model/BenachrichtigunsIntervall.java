package akeefer.model;

public enum BenachrichtigunsIntervall {

    deaktiviert(0, null),
    taeglich(1, "gestern"),
    woechnetlich(7, "letzte Woche");

    private int tage;
    private String zeitinterval;

    BenachrichtigunsIntervall(int tage, String zeitinterval) {
        this.tage = tage;
        this.zeitinterval = zeitinterval;
    }

    public int getTage() {
        return tage;
    }

    public String getZeitinterval() {
        // TODO (ak) lokalisieren
        return zeitinterval;
    }
}
