package com.peterlaurence.trekme.features.mapcreate.presentation.ui.wmts.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.peterlaurence.trekme.core.geocoding.GeoPlace
import com.peterlaurence.trekme.features.mapcreate.presentation.viewmodel.GeoplaceList

@Composable
fun GeoPlaceListUI(uiState: GeoplaceList, onGeoPlaceSelection: (GeoPlace) -> Unit) {
    LazyColumn {
        items(uiState.geoPlaceList) { place ->
            Column(Modifier.clickable { onGeoPlaceSelection(place) }) {
                Text(
                    text = place.name,
                    Modifier.padding(start = 24.dp, top = 8.dp),
                    fontSize = 17.sp
                )
                Text(text = place.locality, Modifier.padding(start = 24.dp, top = 4.dp))
                Divider(Modifier.padding(top = 8.dp))
            }
        }
    }
}