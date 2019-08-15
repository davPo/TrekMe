package com.peterlaurence.trekme.viewmodel.mapcreate

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.billingclient.api.SkuDetails
import com.peterlaurence.trekme.billing.Billing
import kotlinx.coroutines.launch

class IgnLicenseViewModel : ViewModel() {
    private val ignLicenseStatus = MutableLiveData<Boolean>()
    private val ignLicenseDetails = MutableLiveData<IgnLicenseDetails>()

    fun getIgnLicensePurchaseStatus(billing: Billing) {
        viewModelScope.launch {
            billing.getIgnLicensePurchaseStatus().also {
                ignLicenseStatus.postValue(it)
            }
        }
    }

    fun getIgnLicenseInfo(billing: Billing) {
        viewModelScope.launch {
            val licenseDetails = billing.getIgnLicenseDetails()
            ignLicenseDetails.postValue(licenseDetails)
        }
    }

    fun buyLicense(billing: Billing) {
        val ignLicenseDetails = ignLicenseDetails.value
        if (ignLicenseDetails != null) {
            billing.launchBilling(ignLicenseDetails.skuDetails) {
                /* It's assumed that if this is called, it's a success */
                ignLicenseStatus.postValue(true)
            }
        }
    }

    fun getIgnLicenseStatus(): LiveData<Boolean> {
        return ignLicenseStatus
    }

    fun getIgnLicenseDetails(): LiveData<IgnLicenseDetails> {
        return ignLicenseDetails
    }
}

data class IgnLicenseDetails(val skuDetails: SkuDetails) {
    val price: String
        get() = skuDetails.price
}

class NotSupportedException : Exception()
class ProductNotFoundException : Exception()