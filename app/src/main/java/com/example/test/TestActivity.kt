package com.example.test

import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity

class TestActivity : AppCompatActivity() {

    lateinit var videoView: VideoView
    lateinit var fakeStatusBar: View
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)
        supportActionBar?.hide()

        configImmerseBar(this) // COMMENT: zzxmer 沉浸式

        videoView = findViewById(R.id.vv_test)
        fakeStatusBar = findViewById(R.id.v_status_bar)


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            val statusBarHeight = getStatusBarHeight(this)
            fakeStatusBar.layoutParams.height = statusBarHeight
            fakeStatusBar.alpha = 0f
        }

        videoView.setOnCompletionListener {
            videoView.start()
        }

        playVideo()

    }

    fun playVideo() {
        videoView.setVideoURI(Uri.parse("android.resource://$packageName/${R.raw.test}"))
        videoView.start()
    }

    fun stopVideo() {
        videoView.stopPlayback()
    }

    override fun onRestart() {
        super.onRestart()
        playVideo()
    }

    override fun onStop() {
        super.onStop()
        stopVideo()
    }

    fun getStatusBarHeight(context: Context): Int {
        // 获得状态栏高度
        val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
        return context.resources.getDimensionPixelSize(resourceId)
    }

}