package com.github.xtermi2.virtualrun.repository.dto

import javax.validation.constraints.Min

data class ActivitySearchRequest(
        // SORTING
        val sortProperty: AktivitaetSortProperties,

        val sortAsc: Boolean,

        // PAGINATION
        @Min(0)
        val pageableFirstElement: Int,

        @Min(1)
        val pageSize: Int,

        // FILTERS
        val owner: String
)