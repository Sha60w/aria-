package com.aria.aria.quicksettings

import android.content.Intent
import android.os.Build
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import androidx.annotation.RequiresApi


@RequiresApi(Build.VERSION_CODES.N)
class AriaQuickSettingsTile : TileService() {


    override fun onClick() {
        val player = com.aria.aria.player.PlayerHolder.getPlayer(applicationContext)
        val isPlaying = player.isPlaying.value

        val action = if (isPlaying) com.aria.aria.player.ACTION_PAUSE else com.aria.aria.player.ACTION_PLAY
        val intent = Intent(applicationContext, com.aria.aria.player.PlaybackService::class.java).apply {
            this.action = action
        }
        applicationContext.startService(intent)

        updateTile(!isPlaying)
    }

    override fun onStartListening() = updateTile()
    override fun onStopListening() = updateTile()
    override fun onTileAdded() = updateTile()

    private fun updateTile(forceIsPlaying: Boolean? = null) {
        val player = com.aria.aria.player.PlayerHolder.getPlayer(applicationContext)
        val isPlaying = forceIsPlaying ?: player.isPlaying.value

        val newState = if (isPlaying) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE
        val newLabel = if (isPlaying) "Playing" else "Paused"

        qsTile?.apply {
            state = newState
            subtitle = newLabel
            updateTile()
        }
    }
}


