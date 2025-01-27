package com.peterlaurence.trekme.core.map

import com.peterlaurence.trekme.core.map.maploader.MapLoader
import com.peterlaurence.trekme.core.repositories.map.RouteRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.io.File
import kotlin.test.assertNotNull

/**
 * Unit tests for maps's json file parsing.
 *
 * @author P.Laurence on 26/02/17 -- Converted to Kotlin on 17/02/2019
 */
@RunWith(RobolectricTestRunner::class)
class MapParserTest {
    private val mapLoader = MapLoader(Dispatchers.Unconfined, Dispatchers.Default, Dispatchers.IO)
    private val routeRepository = RouteRepository(Dispatchers.IO, Dispatchers.Unconfined)

    @Test
    fun legacyMapRoutesParse() = runBlocking {
        val mapDirURL =
            MapImporterTest::class.java.classLoader!!.getResource("map-with-legacy-routes")
        val mapDir = File(mapDirURL.toURI())

        assertTrue(mapDir.exists())

        val dirs = listOf(mapDir)
        mapLoader.clearMaps()
        val mapList = mapLoader.updateMaps(dirs.filterNotNull())

        /* One map should be found */
        assertEquals(1, mapList.size.toLong())
        val map = mapList[0]

        routeRepository.importRoutes(map)
        assertEquals(2, map.routes.value.size.toLong())

        val route = map.routes.value[0]
        assertEquals("A test route 1", route.name)
        assertTrue(route.visible.value)
        val markers = route.routeMarkers
        assertEquals(2, markers.size.toLong())

        val marker1 = markers[0]
        assertEquals("marker1", marker1.name)
        val lat = marker1.lat
        assertNotNull(lat)
        assertEquals(0.0, lat, 0.0)

        val marker2 = markers[1]
        assertEquals("marker2", marker2.name)
        val lon = marker2.lon
        assertNotNull(lon)
        assertEquals(0.0, lon, 0.0)
    }

    @Test
    fun mapRoutesParse() = runBlocking {
        val mapDirURL = MapImporterTest::class.java.classLoader!!.getResource("map-with-routes")
        val mapDir = File(mapDirURL.toURI())

        assertTrue(mapDir.exists())

        val dirs = listOf(mapDir)
        mapLoader.clearMaps()
        val mapList = mapLoader.updateMaps(dirs)

        /* One map should be found */
        assertEquals(1, mapList.size.toLong())
        val map = mapList[0]

        routeRepository.importRoutes(map)
        assertEquals(2, map.routes.value.size.toLong())

        val route1 = map.routes.value.sortedBy { it.name }[0]
        assertEquals("track-10-09-2021_22h09-04s", route1.name)
        assertTrue(route1.visible.value)
        val markers1 = route1.routeMarkers
        assertEquals(2, markers1.size.toLong())

        val marker1 = markers1[0]
        assertEquals("marker1", marker1.name)
        val lat = marker1.lat
        assertNotNull(lat)
        assertEquals(0.0, lat, 0.0)

        val marker2 = markers1[1]
        assertEquals("marker2", marker2.name)
        val lon = marker2.lon
        assertNotNull(lon)
        assertEquals(-2418744.7142449305, lon, 0.0)

        val route2 = map.routes.value.sortedBy { it.name }[1]
        assertEquals("track-13-07-2021_10h27-44s", route2.name)
        assertTrue(route2.visible.value)
        val markers2 = route2.routeMarkers
        assertEquals(2, markers2.size.toLong())

        val marker3 = markers2[0]
        assertEquals("marker3", marker3.name)
        val lat2 = marker3.lat
        assertNotNull(lat2)
        assertEquals(-2785744.45, lat2, 0.0)

        val marker4 = markers2[1]
        assertEquals("marker4", marker4.name)
        val lon2 = marker4.lon
        assertNotNull(lon2)
        assertEquals(5298798.45375, lon2, 0.0)
    }
}
