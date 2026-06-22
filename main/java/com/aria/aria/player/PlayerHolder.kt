package com.aria.aria.player

import android.content.Context

object PlayerHolder {

    private var playerManager: AriaPlayerManager? = null

    fun getPlayer(
        context: Context
    ): AriaPlayerManager {

        if (playerManager == null) {
            playerManager = AriaPlayerManager(
                context.applicationContext
            )
        }

        return playerManager!!
    }
}