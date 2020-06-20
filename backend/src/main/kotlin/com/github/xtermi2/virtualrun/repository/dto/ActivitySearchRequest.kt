package com.github.xtermi2.virtualrun.repository.dto

import javax.validation.constraints.Min
import javax.validation.constraints.NotNull

data class ActivitySearchRequest(
        // SORTING
        val sortProperty: AktivitaetSortProperties = AktivitaetSortProperties.AKTIVITAETS_DATUM,

        val sortAsc: Boolean = false,

        // PAGINATION
        @Min(0)
        val pageableFirstElement: Int = 0,

        @Min(1)
        val pageSize: Int = 10,

        // FILTERS
        @NotNull
        val owner: String
)