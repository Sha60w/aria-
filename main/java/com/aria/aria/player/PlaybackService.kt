package com.aria.aria.player

import android.content.Intent
import android.os.IBinder
import android.app.Service
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi

class PlaybackService : Service() {

    @OptIn(UnstableApi::class)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val player = PlayerHolder.getPlayer(applicationContext)

        when (intent?.action) {
            ACTION_TOGGLE_PLAY -> {
                if (player.isPlaying.value) player.pause() else player.resume()
            }

            ACTION_PLAY -> player.resume()
            ACTION_PAUSE -> player.pause()
        }

        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null
}

const val ACTION_TOGGLE_PLAY = "com.aria.aria.action.TOGGLE_PLAY"
const val ACTION_PLAY = "com.aria.aria.action.PLAY"
const val ACTION_PAUSE = "com.aria.aria.action.PAUSE"

