package com.example.test

import android.os.Bundle
import com.example.test.base.BaseActivity

class B : BaseActivity() {
    override fun getTag(): String {
        return "b"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addBtn("back") {
            onBackPressed()
        }
        addBtn("finish") {
            finish()
        }
    }
}