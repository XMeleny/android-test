package com.example.test

import android.os.Bundle
import com.example.test.base.BaseActivity

class A : BaseActivity() {
    override fun getTag(): String {
        return "a"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addBtn("jump to b", B::class.java)
    }
}