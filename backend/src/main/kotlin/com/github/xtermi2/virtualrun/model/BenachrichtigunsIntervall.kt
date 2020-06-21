package com.github.xtermi2.virtualrun.model

enum class BenachrichtigunsIntervall(val tage: Int,
                                     val zeitinterval: String?) {
    deaktiviert(0, null),
    taeglich(1, "gestern"),
    woechnetlich(7, "letzte Woche");
}