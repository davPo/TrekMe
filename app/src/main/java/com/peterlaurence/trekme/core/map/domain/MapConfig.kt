package com.peterlaurence.trekme.core.map.domain

data class MapConfig(
    var name: String,
    var thumbnail: String?,
    val levels: List<Level>,
    val origin: MapOrigin,
    val size: Size,
    val imageExtension: String,
    var calibration: Calibration?,
    var sizeInBytes: Long?,
)

data class Level(val level: Int, val tileSize: Size)

data class Size(val width: Int, val height: Int)

sealed interface MapOrigin
data class Wmts(val licensed: Boolean): MapOrigin
object Vips : MapOrigin

