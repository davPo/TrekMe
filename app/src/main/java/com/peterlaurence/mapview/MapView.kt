package com.peterlaurence.mapview

import android.content.Context
import android.util.AttributeSet
import com.peterlaurence.mapview.core.TileStreamProvider
import com.peterlaurence.mapview.core.throttle
import com.peterlaurence.mapview.layout.ZoomPanLayout
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

class MapView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
        ZoomPanLayout(context, attrs, defStyleAttr) {

    /**
     * There are two conventions when using [MapView].
     * 1. The provided [levelCount] will define the zoomLevels index that the provided
     * [tileStreamProvider] will be given for its [TileStreamProvider#zoomLevels]. The zoomLevels
     * will be [0 ; [levelCount]-1].
     *
     * 2. A map is made of levels with level p+1 being twice bigger than level p.
     * The last level will be at scale 1. So all levels have scales between 0 and 1.
     *
     * So it is assumed that the scale of level 1 is twice the scale at level 0, and so on until
     * last level [levelCount] - 1 (which has scale 1).
     */
    fun configureLevels(levelCount: Int, tileStreamProvider: TileStreamProvider) {

    }
}

fun main(args: Array<String>) = runBlocking {
    var last: Long = 0
    val scaleChannel = throttle<Int> {
        val now = System.nanoTime() / 1000000
        println("process $it ${now - last}")
        last = now
    }

    (0..100).forEach {
        scaleChannel.send(it)
        delay(3)
    }
}