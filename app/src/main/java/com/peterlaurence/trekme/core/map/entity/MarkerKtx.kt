package com.peterlaurence.trekme.core.map.entity

import kotlinx.serialization.Serializable

@Serializable
data class MarkerKtx(
    var lat: Double,
    var lon: Double,
    var name: String? = null,
    var elevation: Double? = null,
    var comment: String? = null
)