package com.peterlaurence.trekme.model.providers.stream

import com.peterlaurence.trekme.core.map.TileStreamProvider
import com.peterlaurence.trekme.core.mapsource.MapSource
import com.peterlaurence.trekme.core.mapsource.MapSourceCredentials
import com.peterlaurence.trekme.core.providers.urltilebuilder.*

/**
 * This is the unique place of the app (excluding tests), where we create a [TileStreamProvider]
 * from a [MapSource].
 */
fun createTileStreamProvider(mapSource: MapSource, layer: String, mapSourceCredentials: MapSourceCredentials): TileStreamProvider {
    return when (mapSource) {
        MapSource.IGN -> {
            val ignCredentials = mapSourceCredentials.getIGNCredentials() ?: throw Exception("Missing IGN credentials")
            val urlTileBuilder = UrlTileBuilderIgn(ignCredentials.api ?: "", layer)
            TileStreamProviderIgn(urlTileBuilder)
        }
        MapSource.USGS -> {
            val urlTileBuilder = UrlTileBuilderUSGS()
            TileStreamProviderUSGS(urlTileBuilder)
        }
        MapSource.OPEN_STREET_MAP -> {
            val urlTileBuilder = UrlTileBuilderOSM()
            TileStreamProviderOSM(urlTileBuilder)
        }
        MapSource.IGN_SPAIN -> {
            val urlTileBuilder = UrlTileBuilderIgnSpain()
            TileStreamProviderIgnSpain(urlTileBuilder)
        }
        MapSource.SWISS_TOPO -> {
            val urlTileBuilder = UrlTileBuilderSwiss()
            TileStreamProviderSwiss(urlTileBuilder)
        }
    }
}
