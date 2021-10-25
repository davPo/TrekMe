package com.peterlaurence.trekme.core.map.mappers

import com.peterlaurence.trekme.core.map.domain.Marker
import com.peterlaurence.trekme.core.map.domain.Route
import com.peterlaurence.trekme.core.map.entity.RouteGson
import com.peterlaurence.trekme.core.map.entity.RouteInfoKtx
import com.peterlaurence.trekme.core.map.entity.RouteKtx

fun Route.toRouteKtx(): RouteKtx {
    val r = this
    return RouteKtx(
        markers = r.routeMarkers.map { it.toMarkerKtx() }
    )
}

fun Route.toRouteInfoKtx(): RouteInfoKtx {
    val r = this
    return RouteInfoKtx(
        name = r.name,
        color = r.color,
        visible = r.visible,
        elevationTrusted = r.elevationTrusted,
    )
}

fun RouteGson.Route.toDomain(): Route {
    val domainList = ArrayList<Marker>(routeMarkers.size)
    return Route(
        id,
        name,
        visible,
        markers = routeMarkers.mapTo(domainList) { it.toDomain() },
        color = color,
        elevationTrusted = elevationTrusted
    )
}