package com.example.nxtbus

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView

class SplashActivity : ComponentActivity() {

    private var player: ExoPlayer? = null
    private lateinit var playerView: PlayerView

    private val fallbackHandler = Handler(Looper.getMainLooper())
    private val fallbackRunnable = Runnable {
        if (!isFinishing) startMain()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Fullscreen
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.activity_splash)

        playerView = findViewById(R.id.playerView)
        playerView.setOnClickListener { startMain() }

        // Safety timeout in case of codec issues
        fallbackHandler.postDelayed(fallbackRunnable, 8000)
    }

    override fun onStart() {
        super.onStart()
        initPlayer()
    }

    override fun onStop() {
        super.onStop()
        releasePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        fallbackHandler.removeCallbacks(fallbackRunnable)
    }

    private fun initPlayer() {
        if (player != null) return
        val exo = ExoPlayer.Builder(this).build()
        player = exo
        playerView.player = exo

        val videoUri = Uri.parse("android.resource://$packageName/${R.raw.splash_screen}")
        val mediaItem = MediaItem.fromUri(videoUri)

        exo.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == Player.STATE_ENDED) {
                    startMain()
                }
            }
            override fun onPlayerError(error: PlaybackException) {
                startMain()
            }
        })

        exo.setMediaItem(mediaItem)
        exo.volume = 0f // mute
        exo.repeatMode = Player.REPEAT_MODE_OFF
        exo.playWhenReady = true
        exo.prepare()
    }

    private fun releasePlayer() {
        playerView.player = null
        player?.release()
        player = null
    }

    private fun startMain() {
        if (isFinishing) return
        releasePlayer()
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
