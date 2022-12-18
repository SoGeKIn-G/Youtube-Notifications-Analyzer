package com.gauravbora.task_2.fabricio.notifications.database

interface CallbackSql<T> {
    fun onResult(result:T)
}