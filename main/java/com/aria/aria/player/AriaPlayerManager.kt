package com.aria.aria.player

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer

import com.aria.aria.data.model.Song

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

@UnstableApi
class AriaPlayerManager(context: Context) {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private var progressJob: Job? = null

    private val player: ExoPlayer = ExoPlayer.Builder(context.applicationContext).build()

    private val _currentPosition = mutableLongStateOf(0L)
    val currentPosition: Long get() = _currentPosition.longValue

    private val _duration = mutableLongStateOf(0L)
    val duration: Long get() = _duration.longValue

    private val _currentTitle = mutableStateOf("")
    val currentTitle: State<String> = _currentTitle

    private val _isPlaying = mutableStateOf(false)
    val isPlaying: State<Boolean> = _isPlaying

    private val _artworkUri = mutableStateOf<String?>(null)
    val artworkUri: State<String?> = _artworkUri

    private var queue: List<MediaItem> = emptyList()
    private var queueIndex: Int = -1

    private val _queueTitles = androidx.compose.runtime.mutableStateOf<List<String>>(emptyList())
    val queueTitles: State<List<String>> get() = _queueTitles

    private val _queueUris = androidx.compose.runtime.mutableStateOf<List<String>>(emptyList())
    val queueUris: State<List<String>> get() = _queueUris


    init {
        player.addListener(object : androidx.media3.common.Player.Listener {
            override fun onPlaybackStateChanged(state: Int) {
                _isPlaying.value = player.isPlaying && state == androidx.media3.common.Player.STATE_READY
            }

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                _isPlaying.value = isPlaying
            }

            override fun onMediaItemTransition(
                mediaItem: MediaItem?,
                reason: Int
            ) {
                // Title best-effort
                val title = mediaItem?.mediaMetadata?.title?.toString().orEmpty()
                if (title.isNotBlank()) _currentTitle.value = title
            }

            override fun onPositionDiscontinuity(reason: Int) {
                // keep progress loop in sync
                _currentPosition.longValue = player.currentPosition
                _duration.longValue = player.duration.coerceAtLeast(0L)
            }
        })

        startProgressUpdates()
    }

    fun playSong(title: String, uri: String, artworkUri: String? = null) {
        _currentTitle.value = title
        _artworkUri.value = artworkUri

        val item = MediaItem.Builder()
            .setUri(uri)
            .setMediaId(uri)
            .setMediaMetadata(androidx.media3.common.MediaMetadata.Builder().setTitle(title).build())
            .build()

        queue = listOf(item)
        queueIndex = 0
        syncQueueState()

        player.setMediaItems(queue, queueIndex, 0L)
        player.prepare()
        player.play()
    }


    fun setQueue(songs: List<Song>) {
        queue = songs.map { s ->
            MediaItem.Builder()
                .setUri(s.uri)
                .setMediaId(s.uri)
                .setMediaMetadata(
                    androidx.media3.common.MediaMetadata.Builder()
                        .setTitle(s.title)
                        .build()
                )
                .build()
        }
        if (queueIndex == -1 && queue.isNotEmpty()) queueIndex = 0
        syncQueueState()
    }



    fun playQueueAt(index: Int, autoplay: Boolean = true) {
        if (queue.isEmpty()) return

        queueIndex = index.coerceIn(0, queue.size - 1)
        // keep queue state in sync even if only index changes
        syncQueueState()

        player.setMediaItems(queue, queueIndex, 0L)
        player.prepare()

        if (autoplay) {
            player.play()
        } else {
            player.pause()
        }

        val title = queue[queueIndex].mediaMetadata.title?.toString().orEmpty()
        if (title.isNotBlank()) _currentTitle.value = title
    }


    fun clearQueue() {
        queueIndex = -1
        queue = emptyList()
        player.stop()
        player.clearMediaItems()
        _isPlaying.value = false
        _currentPosition.longValue = 0L
        _duration.longValue = 0L
        _currentTitle.value = ""
        _artworkUri.value = null
        syncQueueState()
    }

    private fun syncQueueState() {
        _queueTitles.value = queue.mapNotNull { it.mediaMetadata.title?.toString() }
        _queueUris.value = queue.map { it.localConfiguration?.uri?.toString() ?: it.mediaId ?: "" }
    }




    fun next() {
        if (queue.isEmpty()) return
        val nextIndex = if (queueIndex == -1) 0 else (queueIndex + 1).coerceAtMost(queue.size - 1)
        playQueueAt(nextIndex, autoplay = true)
    }

    fun previous() {
        if (queue.isEmpty()) return
        val prevIndex = if (queueIndex <= 0) 0 else queueIndex - 1
        playQueueAt(prevIndex, autoplay = true)
    }

    fun seekTo(positionMs: Long) {
        player.seekTo(positionMs)
        _currentPosition.longValue = positionMs
    }

    fun pause() {
        player.pause()
        _isPlaying.value = false
    }

    fun resume() {
        player.play()
        _isPlaying.value = true
    }

    fun stop() {
        progressJob?.cancel()
        player.stop()
        player.clearMediaItems()

        _currentPosition.longValue = 0L
        _duration.longValue = 0L
        _currentTitle.value = ""
        _artworkUri.value = null
        _isPlaying.value = false

        startProgressUpdates()
    }

    fun release() {
        progressJob?.cancel()
        scope.cancel()
        player.release()
    }

    fun progress(): Float {
        val d = _duration.longValue
        if (d <= 0L) return 0f
        return (_currentPosition.longValue.toFloat() / d.toFloat()).coerceIn(0f, 1f)
    }

    private fun startProgressUpdates() {
        progressJob?.cancel()
        progressJob = scope.launch {
            while (isActive) {
                _currentPosition.longValue = player.currentPosition
                _duration.longValue = player.duration.coerceAtLeast(0L)
                _isPlaying.value = player.isPlaying
                delay(250)
            }
        }
    }
}

