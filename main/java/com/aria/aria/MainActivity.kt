package com.aria.aria

import android.os.Bundle
import android.os.Build
import android.app.PictureInPictureParams
import androidx.activity.ComponentActivity


import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.aria.aria.ui.theme.AriaTheme
import com.aria.aria.ui.theme.Black
import com.aria.aria.ui.theme.MatteBlack
import com.aria.aria.ui.AriaApp

class MainActivity : ComponentActivity() {

    private var canEnterPip: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AriaTheme {
                AriaApp()
            }
        }
    }

    override fun onUserLeaveHint() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && canEnterPip) {
            // Enter PiP when leaving UI; actual playing-state check is best-effort.
            try {
                if (isInPictureInPictureMode) return

                val pipParams = PictureInPictureParams.Builder()
                    .setAspectRatio(android.util.Rational(16, 9))
                    .build()

                enterPictureInPictureMode(pipParams)
            } catch (_: Exception) {
                // Ignore if PiP cannot be entered.
            }
        }
        super.onUserLeaveHint()
    }

    override fun onPictureInPictureModeChanged(
        isInPictureInPictureMode: Boolean,
        newConfig: android.content.res.Configuration
    ) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig)
    }
}


@Composable
fun AriaHomeScreen(){
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Black),
        contentAlignment = Alignment.Center
    ){
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ){
            Image(
                painter = painterResource(id= R.drawable.aria_logo),
                contentDescription =  "Aria Logo",
                modifier = Modifier.size(300.dp),
                contentScale = ContentScale.Fit,
            )
        }
    }
}