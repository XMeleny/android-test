package com.example.test

import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity

class TestActivity : AppCompatActivity() {
    private lateinit var videoView: VideoView
    var videoComplete = false
    var videoRepeat = false
    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("zzxmer", "onCreate: ")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)
        videoView = findViewById(R.id.vv_test)
        findViewById<Button>(R.id.btn_playVideo).setOnClickListener {
            playVideo()
        }

        findViewById<Button>(R.id.btn_setColor).setOnClickListener {
            window.statusBarColor = Color.BLUE
            window.navigationBarColor = Color.BLUE
        }
    }

    var hasVideo = false

    fun playVideo() {
        hasVideo = true
        videoView.setVideoURI(Uri.parse("android.resource://$packageName/${R.raw.test}"))
        videoView.start()
        videoRepeat = findViewById<CheckBox>(R.id.cb_videoRepeat).isChecked
        videoView.setOnCompletionListener {
            videoComplete = true
            if (videoRepeat) {
                videoView.start()
            }
        }
    }

    override fun onResume() {
        Log.d("zzxmer", "onResume: ")
        super.onResume()
        if (hasVideo) {
            videoView.start()
        }
    }

    override fun onPause() {
        Log.d("zzxmer", "onPause: ")
        super.onPause()
        if (hasVideo) {
            videoView.pause()
        }
    }

    override fun onDestroy() {
        Log.d("zzxmer", "onDestroy: ")
        super.onDestroy()
        if (hasVideo) {
            videoView.stopPlayback()
        }
    }
}