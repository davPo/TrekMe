package com.peterlaurence.trekme.model.providers.stream

import com.peterlaurence.trekme.core.map.TileStreamProvider
import com.peterlaurence.trekme.core.providers.bitmap.TileStreamProviderHttpAuth
import com.peterlaurence.trekme.core.providers.urltilebuilder.UrlTileBuilder
import java.io.InputStream

/**
 * A [TileStreamProvider] specific for France IGN.
 * Luckily, IGN's [WMTS service](https://geoservices.ign.fr/documentation/geoservices/wmts.html) has
 * a grid coordinates that is exactly the same as the one [MapView] uses.
 * Consequently, to make a valid HTTP request, we just have to format the URL with raw zoom-level,
 * row and col numbers.
 * Additional information have to be provided though, like IGN credentials.
 *
 * @author peterLaurence on 20/06/19
 */
class TileStreamProviderIgn(urlTileBuilder: UrlTileBuilder) : TileStreamProvider {
    private val base: TileStreamProvider

    init {
        base = TileStreamProviderHttpAuth(urlTileBuilder, "TrekMe")
    }

    override fun getTileStream(row: Int, col: Int, zoomLvl: Int): InputStream? {
        /* Filter-out inaccessible tiles at lower levels */
        when (zoomLvl) {
            3 -> if (row > 7 || col > 7) return null
        }
        /* Safeguard */
        if (zoomLvl > 17) return null

        return base.getTileStream(row, col, zoomLvl)
    }
}