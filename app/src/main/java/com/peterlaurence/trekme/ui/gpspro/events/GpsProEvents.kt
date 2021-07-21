package com.peterlaurence.trekme.ui.gpspro.events

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class GpsProEvents {
    private val _showGpsProFragmentSignal = MutableSharedFlow<Unit>(extraBufferCapacity = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    val showGpsProFragmentSignal = _showGpsProFragmentSignal.asSharedFlow()

    fun requestShowGpsProFragment() = _showGpsProFragmentSignal.tryEmit(Unit)

    /**********************************************************************************************/

    private val _showBtDeviceSettingsFragmentSignal = MutableSharedFlow<Unit>(extraBufferCapacity = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    val showBtDeviceSettingsFragmentSignal = _showBtDeviceSettingsFragmentSignal.asSharedFlow()

    fun requestShowBtDeviceSettingsFragment() = _showBtDeviceSettingsFragmentSignal.tryEmit(Unit)

    /**********************************************************************************************/

    private val _nmeaSentencesFlow = MutableSharedFlow<String>(extraBufferCapacity = 50, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    val nmeaSentencesFlow = _nmeaSentencesFlow.asSharedFlow()

    fun postNmeaSentence(sentence: String) = _nmeaSentencesFlow.tryEmit(sentence)
}