package com.yunzia.hyperstar.ui.module.systemui.controlcenter.media.app

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.graphics.drawable.Drawable
import android.util.Log
import com.yunzia.hyperstar.ui.module.systemui.controlcenter.media.app.AppInfo

class AppListDB(context: Context?) :
    SQLiteOpenHelper(context, "appList.db", null, 1) {
    override fun onCreate(db: SQLiteDatabase) {
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
    }

    fun resetTable() {
        val db = this.writableDatabase
        db.execSQL("DROP TABLE IF EXISTS appdata")
        db.execSQL("CREATE TABLE appdata ( package_name TEXT NOT NULL UNIQUE,app_name TEXT NOT NULL);")
        db.close()
    }


    fun add(values: ContentValues?) {
        val db = this.writableDatabase
        db.insert("appdata", null, values)
        db.close()
    }

    @SuppressLint("Range", "Recycle")
    fun searchAPPlist(appName: String, appIconlist: MutableMap<String, Drawable>): ArrayList<AppInfo> {

        val appList = ArrayList<AppInfo>()
        var csearch: Cursor? = null
        val db_search = this.writableDatabase

        val query = "SELECT * FROM appdata WHERE app_name LIKE ?"
        // 使用LIKE语句时，确保将appName包围在%中
        val pattern = "%$appName%"
        csearch = db_search.rawQuery(query, arrayOf(pattern))
        while (csearch.moveToNext()) {
            val label = csearch.getString(csearch.getColumnIndex("app_name"))
            val packageName = csearch.getString(csearch.getColumnIndex("package_name"))
//            Log.d("ggc", "searchAPPlist: $label $packageName")
            val appData = AppInfo()
            appData.label = label
            appData.package_name = packageName
            appData.icon = appIconlist[appData.package_name]
            if (appIconlist[appData.package_name] == null){
                Log.d("ggc","appIconlist[appData.package_name] == null")
            }
            if (appData.icon == null){
                Log.d("ggc","appData.icon  == null")

            }
            //appData.setIcon(getAppIconByPackageName(mContext, packageName));
            appList.add(appData)
        }
        db_search.close()
        csearch.close()
//        Log.d("ggc", "searchAPPlist: " + appList.size)

        return appList
    }
}