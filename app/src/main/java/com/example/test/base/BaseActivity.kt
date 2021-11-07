package com.example.test.base

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.test.R

open class BaseActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "BaseActivity"
    }

    lateinit var rootLinearLayout: LinearLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(getTag(), "onCreate: ")
        setContentView(R.layout.activity_base)
        rootLinearLayout = findViewById(R.id.ll_root)
    }

    private fun getOneRowLayoutParam(): LinearLayout.LayoutParams {
        return LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
    }


    fun addBtn(btnName: String, onClick: () -> Unit): Button {
        val button = Button(this)
        button.text = btnName
        button.setOnClickListener {
            Log.d(TAG, "$btnName click")
            onClick.invoke()
        }
        rootLinearLayout.addView(button, getOneRowLayoutParam())
        return button
    }

    fun addBtn(btnName: String, cls: Class<*>): Button {
        return addBtn(btnName) {
            val intent = Intent(this, cls)
            startActivity(intent)
        }
    }

    fun addTextView(text: String): TextView {
        val textview = TextView(this)
        textview.text = text
        rootLinearLayout.addView(textview, getOneRowLayoutParam())
        return textview
    }

    fun makeDialog(title: String?, message: String?, positiveText: String?, negativeText: String?, onPositive: () -> Unit, onNegative: () -> Unit): AlertDialog {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.apply {
            setTitle(title)
            setMessage(message)
            setPositiveButton(positiveText, DialogInterface.OnClickListener { _, _ ->
                onPositive.invoke()
            })
            setNegativeButton(negativeText, DialogInterface.OnClickListener { _, _ ->
                onNegative.invoke()
            })
        }
        return builder.create()
    }

    open fun getTag(): String {
        return TAG
    }

    override fun onStart() {
        super.onStart()
        Log.d(getTag(), "onStart: ")
    }

    override fun onResume() {
        super.onResume()
        Log.d(getTag(), "onResume: ")
    }

    override fun onPause() {
        super.onPause()
        Log.d(getTag(), "onPause: ")
    }

    override fun onStop() {
        super.onStop()
        Log.d(getTag(), "onStop: ")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(getTag(), "onDestroy: ")
    }
}