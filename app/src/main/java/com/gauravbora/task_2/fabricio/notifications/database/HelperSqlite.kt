package com.gauravbora.task_2.fabricio.notifications.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.gauravbora.task_2.fabricio.notifications.models.TrackEntry

internal class HelperSqlite(context: Context?) : SQLiteOpenHelper(context, "notifications_dada.db", null, 1) {
    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(TrackEntry.SQL_CREATE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL(TrackEntry.SQL_DELETE_TABLE)
        onCreate(db)
    }
}