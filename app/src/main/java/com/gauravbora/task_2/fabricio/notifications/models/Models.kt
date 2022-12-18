package com.gauravbora.task_2.fabricio.notifications.models

import android.provider.BaseColumns

data class TrackNotification(
    var _id: Int,
    val title: String = "",
    val origin: String = "",
    val text: String,
    val raw: String = "",
    val time: Long = System.currentTimeMillis(),
    var expended: Boolean = false
)

object TrackEntry : BaseColumns {
    const val TABLE_NAME = "TrackRequest"
    const val ORIGIN = "origin"
    const val TIME = "time"
    const val TITLE = "title"
    const val TEXT = "text"
    const val RAW = "raw"

    const val SQL_CREATE_TABLE =
        "CREATE TABLE $TABLE_NAME (" +
                "${BaseColumns._ID} INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$ORIGIN TEXT," +
                "$TIME LONG," +
                "$TITLE TEXT, " +
                "$TEXT TEXT, " +
                "$RAW TEXT)"

    val SQL_DELETE_TABLE = "DROP TABLE IF EXISTS $TABLE_NAME"
}

class SpanedKey(val source: String) {
    var start = 0
    var end = 0
    fun process(key: String): SpanedKey? {
        if (source.contains(key)) {
            start = source.indexOf(key) + 1
            end = start + key.length + 1
            return this
        }
        return null
    }
}