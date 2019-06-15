package com.peterlaurence.mapview.core

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Process
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.selects.select
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger

/**
 * We build our own coroutine dispatcher, as we want to set the min priority to each thread of the
 * pool. Also, the size of the pool should be less than the number of cores (which is different from
 * [Dispatchers.Default] which uses exactly the number of cores).
 */
val threadId = AtomicInteger()
val nCores = Runtime.getRuntime().availableProcessors()
val nWorkers = nCores - 1
val dispatcher = Executors.newFixedThreadPool(nWorkers) {
    Thread(it, "TileCollector-worker-${threadId.incrementAndGet()}").apply {
        isDaemon = true
        priority = Thread.MIN_PRIORITY
        Process.setThreadPriority(Process.THREAD_PRIORITY_LOWEST)
    }
}.asCoroutineDispatcher()

/**
 * @param [visibleTileLocations] channel of [TileSpec], which capacity should be [Channel.CONFLATED].
 * @param [tilesOutput] channel of [Tile], which capacity should be [Channel.UNLIMITED]
 */
fun CoroutineScope.collectTiles(visibleTileLocations: ReceiveChannel<List<TileSpec>>,
                                tilesOutput: SendChannel<Tile>,
                                tileStreamProvider: TileStreamProvider,
                                bitmapFlow: Flow<Bitmap>) {
    val tilesToDownload = Channel<TileStatus>(capacity = Channel.RENDEZVOUS)
    val tilesDownloadedFromWorker = Channel<TileBundle>(capacity = Channel.UNLIMITED)

    repeat(nWorkers) { worker(tilesToDownload, tilesDownloadedFromWorker, tileStreamProvider, bitmapFlow) }
    tileCollector(visibleTileLocations, tilesToDownload, tilesDownloadedFromWorker, tilesOutput)
}

private fun CoroutineScope.worker(tilesToDownload: ReceiveChannel<TileStatus>,
                                  tilesDownloaded: SendChannel<TileBundle>,
                                  tileStreamProvider: TileStreamProvider,
                                  bitmapFlow: Flow<Bitmap>) = launch(dispatcher) {

    val bitmapLoadingOptions = BitmapFactory.Options()
    bitmapLoadingOptions.inPreferredConfig = Bitmap.Config.RGB_565

    for (tileStatus in tilesToDownload) {
        /* If it was cancelled, do nothing and send back the TileSpec as is */
        if (tileStatus.cancelled) {
            tilesDownloaded.send(TileBundle(tileStatus, null))
            continue
        }

        val spec = tileStatus.spec

        val i = tileStreamProvider.getTileStream(spec.row, spec.col, spec.zoom)

        if (spec.subSample > 0) {
            bitmapLoadingOptions.inBitmap = null
            bitmapLoadingOptions.inScaled = true
            bitmapLoadingOptions.inSampleSize = spec.subSample
        } else {
            bitmapLoadingOptions.inScaled = false
            bitmapLoadingOptions.inBitmap = bitmapFlow.single()
            bitmapLoadingOptions.inSampleSize = 0
        }

        try {
            val bitmap = BitmapFactory.decodeStream(i, null, bitmapLoadingOptions) ?: continue
            val tile = Tile(spec.zoom, spec.row, spec.col, bitmap, spec.subSample)
            tilesDownloaded.send(TileBundle(tileStatus, tile))
        } catch (e: OutOfMemoryError) {
            // no luck
        } catch (e: Exception) {
            // maybe retry
        } finally {
            i?.close()
        }
    }
}

private fun CoroutineScope.tileCollector(tileLocations: ReceiveChannel<List<TileSpec>>,
                                         tilesToDownload: SendChannel<TileStatus>,
                                         tilesDownloadedFromWorker: ReceiveChannel<TileBundle>,
                                         tilesOutput: SendChannel<Tile>) = launch {

    val tilesBeingProcessed = mutableListOf<TileStatus>()

    while (true) {
        select<Unit> {
            tileLocations.onReceive {
                for (loc in it) {
                    if (!tilesBeingProcessed.any { status -> status.spec == loc }) {
                        /* Add it to the list of locations being processed */
                        val status = TileStatus(loc)
                        tilesBeingProcessed.add(status)

                        /* Now download the tile */
                        tilesToDownload.send(status)
                    }
                }
                for (status in tilesBeingProcessed) {
                    if (!it.contains(status.spec)) {
                        status.cancelled = true
                    }
                }
            }

            tilesDownloadedFromWorker.onReceive {
                it.tile?.let { tile ->
                    tilesOutput.send(tile)
                }

                /* Now remove the corresponding TileStatus from the list, but only after a delay, to
                 * disallow this tile to be requested again while it's about to be rendered */
                launch {
                    delay(30)
                    tilesBeingProcessed.remove(it.status)
                }
            }
        }
    }
}

data class TileStatus(val spec: TileSpec, @Volatile var cancelled: Boolean = false)
data class TileBundle(val status: TileStatus, val tile: Tile?)