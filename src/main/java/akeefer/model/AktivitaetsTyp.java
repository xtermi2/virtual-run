package akeefer.model;

public enum AktivitaetsTyp {
    laufen("gelaufen"),
    radfahren("rad gefahren"),
    schwimmen("geschwommen"),
    spezierengehen("spazieren gegangen"),
    wandern("gewandert"),
    sonstiges("?");

    private String vergangenheitsform;

    AktivitaetsTyp(String vergangenheitsform) {
        this.vergangenheitsform = vergangenheitsform;
    }

    public String toVergangenheit() {
        // TODO (ak) lokalisieren
        return vergangenheitsform;
    }
}
