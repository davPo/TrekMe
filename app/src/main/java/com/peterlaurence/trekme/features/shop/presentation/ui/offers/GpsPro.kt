package com.peterlaurence.trekme.features.shop.presentation.ui.offers

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.peterlaurence.trekme.R
import com.peterlaurence.trekme.billing.common.PurchaseState
import com.peterlaurence.trekme.features.shop.presentation.ui.Header
import com.peterlaurence.trekme.features.shop.presentation.ui.PriceButton
import com.peterlaurence.trekme.features.shop.presentation.viewmodel.GpsProPurchaseViewModel

@Composable
fun GpsProPurchaseHeaderStateful(
    viewModel: GpsProPurchaseViewModel = viewModel()
) {
    val purchaseState by viewModel.purchaseFlow.collectAsState()
    val subDetails by viewModel.subscriptionDetailsFlow.collectAsState(initial = null)

    GpsProPurchaseHeader(purchaseState, subDetails?.trialDurationInDays)
}

@Composable
private fun GpsProPurchaseHeader(purchaseState: PurchaseState, trialDuration: Int?) {
    val subTitle = when (purchaseState) {
        PurchaseState.CHECK_PENDING -> stringResource(id = R.string.module_check_pending)
        PurchaseState.PURCHASED -> stringResource(id = R.string.module_owned)
        PurchaseState.NOT_PURCHASED -> {
            if (trialDuration != null) {
                stringResource(id = R.string.free_trial).format(trialDuration)
            } else {
                stringResource(id = R.string.module_error)
            }
        }
        PurchaseState.PURCHASE_PENDING -> stringResource(id = R.string.module_check_pending)
        PurchaseState.UNKNOWN -> stringResource(id = R.string.module_error)
    }
    Header(title = stringResource(id = R.string.gps_pro_name), subTitle = subTitle)
}

@Composable
fun ColumnScope.GpsProPurchaseUI() {
    Image(
        modifier = Modifier
            .padding(16.dp)
            .clip(RoundedCornerShape(10.dp))
            .align(Alignment.CenterHorizontally),
        painter = painterResource(id = R.drawable.gps_ext),
        contentScale = ContentScale.Inside,
        contentDescription = null
    )
    val alpha = 0.87f
    Text(
        stringResource(id = R.string.gps_pro_pres_p1_title),
        Modifier.alpha(alpha),
        fontWeight = FontWeight.Medium,
    )
    Spacer(modifier = Modifier.padding(8.dp))
    Text(
        stringResource(
            id = R.string.gps_pro_pres_content
        ),
        Modifier.alpha(alpha),
        textAlign = TextAlign.Justify,
        fontSize = 14.sp
    )
    Spacer(modifier = Modifier.padding(8.dp))
    Text(
        stringResource(id = R.string.gps_pro_pres_p2_title),
        Modifier.alpha(alpha),
        fontWeight = FontWeight.Medium,
    )
    Spacer(modifier = Modifier.padding(8.dp))
    for (device in supportedDevices) {
        Text(
            "• $device",
            Modifier.alpha(alpha),
            fontSize = 14.sp
        )
    }
    Spacer(modifier = Modifier.padding(8.dp))
    Text(
        stringResource(
            id = R.string.gps_pro_pres_ending
        ),
        Modifier.alpha(alpha),
        textAlign = TextAlign.Justify,
        fontSize = 14.sp
    )
}

private val supportedDevices = listOf(
    "GARMIN GLO2",
    "Dual XGPS150A & SkyPro XGPS160",
    "Bad Elf Flex, GNSS Surveyor, GPS Pro+",
    "Juniper Systems Geode"
)

@Composable
fun GpsProPurchaseFooterStateful(viewModel: GpsProPurchaseViewModel = viewModel()) {
    val purchaseState by viewModel.purchaseFlow.collectAsState()
    val subDetails by viewModel.subscriptionDetailsFlow.collectAsState(initial = null)

    GpsProPurchaseFooter(purchaseState, price = subDetails?.price, viewModel::buy)
}

@Composable
private fun GpsProPurchaseFooter(
    purchaseState: PurchaseState,
    price: String?,
    buyCb: () -> Unit
) {
    if (purchaseState == PurchaseState.NOT_PURCHASED && price != null) {
        PriceButton(
            onClick = buyCb,
            modifier = Modifier.padding(bottom = 16.dp),
            duration = stringResource(id = R.string.one_year),
            price = price
        )
    }
}
