package com.gauravbora.task_2.fabricio.notifications.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.provider.BaseColumns
import com.gauravbora.task_2.fabricio.notifications.models.TrackEntry
import com.gauravbora.task_2.fabricio.notifications.models.TrackNotification
import java.lang.Exception

class DataBase private constructor(ctx:Context) {

   private var onObservers = ArrayList<CallbackSql<List<TrackNotification>>>()

    private val helper: HelperSqlite = HelperSqlite(ctx)

    companion object {
        private var instance: DataBase? = null
        fun instance(ctx: Context): DataBase {
            synchronized(this) {
                if (instance == null)
                    instance = DataBase(ctx)
                return instance!!
            }
        }
    }

    fun insert(trace: TrackNotification) {
        if(trace.origin.contains("YouTube") || trace.origin.contains("youtube") || trace.origin.contains("Youtube") ||trace.origin.contains("youTube")) {
            val values = ContentValues().apply {
                put(TrackEntry.ORIGIN, trace.origin)
                put(TrackEntry.TIME, trace.time)
                put(TrackEntry.TITLE, trace.title)
                put(TrackEntry.TEXT, trace.text)
                put(TrackEntry.RAW, trace.raw)
            }
            helper.writableDatabase.insert(TrackEntry.TABLE_NAME, null, values).let {
                trace._id = it.toInt()
            }
            helper.writableDatabase.close()

            this.evaluated()
        }
    }

    fun getAllTracesGrouped(): List<TrackNotification> {
        val list = ArrayList<TrackNotification>()

        val cursor = helper.readableDatabase.rawQuery(
            "SELECT * FROM ${TrackEntry.TABLE_NAME} Group by origin ORDER BY time DESC ",
            null
        )
        with(cursor) {
            while (moveToNext()) {
                list.add(getTraceByCursor(this))
            }
            close()
        }
        return list
    }

    private fun getTraceByCursor(cr: Cursor): TrackNotification {
        with(cr) {
            val id = getInt(this.getColumnIndexOrThrow(BaseColumns._ID))
            val origin = getString(getColumnIndexOrThrow(TrackEntry.ORIGIN))
            val time = getLong(getColumnIndexOrThrow(TrackEntry.TIME))
            val title = getString(getColumnIndexOrThrow(TrackEntry.TITLE))
            val text = getString(getColumnIndexOrThrow(TrackEntry.TEXT))
            val raw = getString(getColumnIndexOrThrow(TrackEntry.RAW))

            return TrackNotification(id, title, origin, text, raw, time)
        }
    }

    fun getTracesByGroup(origin:String): List<TrackNotification> {
        val list = ArrayList<TrackNotification>()

        val cursor = helper.readableDatabase.rawQuery(
            "SELECT * FROM ${TrackEntry.TABLE_NAME} WHERE origin = '$origin' ORDER BY time DESC ",
            null
        )
        with(cursor) {
            while (moveToNext()) {
                list.add(getTraceByCursor(this))
            }
            close()
        }
        return list
    }

    fun deleteFromOrigin(origin: String){
        try {
            helper.writableDatabase.execSQL("Delete from ${TrackEntry.TABLE_NAME} where ${TrackEntry.ORIGIN}='$origin'")
        }catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    fun delete(id: Int) {
        try {
            helper.writableDatabase.execSQL("Delete from ${TrackEntry.TABLE_NAME} where ${BaseColumns._ID}=$id")
        }catch (ex:Exception) {
            ex.printStackTrace()
        }

        this.evaluated()
    }

    fun registerObserver(observer: CallbackSql<List<TrackNotification>>){
        onObservers.add(observer)
        this.evaluated()
    }

    fun removeObserver(observer: CallbackSql<List<TrackNotification>>){
        onObservers.remove(observer)
    }


    private fun evaluated(){
        val result = getAllTracesGrouped()
        try {

            for(item in onObservers){
                item.onResult(result)
            }
        }catch (ex:Exception){
            ex.printStackTrace()
        }


    }

}