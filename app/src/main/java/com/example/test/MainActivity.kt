package com.example.test

import android.content.IntentFilter
import android.util.Log
import com.example.test.base.BaseActivity
import java.io.File

private const val TAG = "MainActivity"

class MainActivity : BaseActivity() {
    override fun initUI() {
        super.initUI()

        addBtn("test view pager", ViewPagerTestActivity::class.java)

        val pathEditText = addEditText("path for apk")
        addBtn("test activity intent filter") {
            var path = pathEditText.text.toString()
            if (path.isEmpty()) {
                path = "/data/app/com.taobao.taobao-MyQ48Fw0zqHXXtYxWvsGnQ==/base.apk"
            }
            parseApkByReflect(File(path))
        }
    }

    private fun parseApkByReflect(apkFile: File?) {
        try {
            // android 9
            val clazz = Class.forName("android.content.pm.PackageParser")
            val packageParser = clazz.newInstance()
            val methodParsePackage = clazz.getMethod("parsePackage", File::class.java, Int::class.javaPrimitiveType)
            val packageObj = methodParsePackage.invoke(packageParser, apkFile, 0) ?: return
            val activities = packageObj.javaClass.getField("activities")[packageObj] as List<*>
            for (data in activities) {
                data ?: continue

                val componentInfo = data.javaClass.getField("info").get(data)
                val name = componentInfo.javaClass.getField("name").get(componentInfo)
                Log.d(TAG, "parseApkByReflect: activity=$name")

                val filters = data.javaClass.getField("intents")[data] as List<IntentFilter>
                for (filter in filters) {
                    val actions = getList(filter.actionsIterator())
                    val categories = getList(filter.categoriesIterator())
                    Log.d(TAG, "filter: actions=$actions, categories=$categories")
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getList(iterator: Iterator<String>?): List<String>? {
        iterator ?: return null
        val result = mutableListOf<String>()
        while (iterator.hasNext()) {
            result.add(iterator.next())
        }
        return result
    }
}