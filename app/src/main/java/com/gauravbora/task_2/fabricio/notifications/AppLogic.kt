package com.gauravbora.task_2.fabricio.notifications

import android.graphics.Typeface
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.StyleSpan
import com.altamirano.fabricio.core.commons.OnBoardItem
import com.gauravbora.task_2.R
import com.gauravbora.task_2.fabricio.notifications.models.SpanedKey
import java.text.SimpleDateFormat
import java.util.*

object AppLogic {
    const val TAG = "NotLog"
    val FORMAT_DATE_MONTH = SimpleDateFormat("MM-dd HH:mm:ss", Locale.ENGLISH)
    val FORMAT_DATE_HOURS = SimpleDateFormat("HH:mm:ss", Locale.ENGLISH)

    fun getOnBoardItems() = arrayListOf(
        OnBoardItem(R.drawable.ic_step_1, R.string.step_one_title, R.string.step_one_subtitle),
        OnBoardItem(R.drawable.ic_step_2, R.string.step_two_title, R.string.step_two_subtitle),
        OnBoardItem(R.drawable.ic_step_3, R.string.step_three_title, R.string.step_three_subtitle),
        OnBoardItem(R.drawable.ic_step_4, R.string.step_four_title, R.string.step_four_subtitle)
    )

    fun String.getFormatted(): Spanned {
        val boldOption = SpannableStringBuilder(
            this.replace(",", "\n").replace("{", "\n{\n").replace("}", "\n}\n")
        )

        val names = arrayOf(
            "android.title",
            "android.text",
            "android.subText",
            "android.template",
            "android.showChronometer",
            "android.icon",
            "android.progress",
            "android.progressMax",
            "android.appInfo",
            "android.showWhen",
            "android.largeIcon",
            "android.bigText",
            "android.infoText",
            "android.progressIndeterminate",
            "android.remoteInputHistory",
            "android.reduced.images",
            "android.reduced"
        )

        for (item in names) {
            SpanedKey(this).process(item)?.let {
                boldOption.setSpan(StyleSpan(Typeface.BOLD), it.start, it.end, 0)
            }
        }

        return boldOption
    }

    fun Long.getAsDate(): String {
        try {
            Date(this).let { date ->
                val current = Date()
                return if (current.month == date.month && current.day == date.day) {
                    FORMAT_DATE_HOURS.format(date)
                } else {
                    FORMAT_DATE_MONTH.format(date)
                }
            }

        } catch (ex: java.lang.Exception) {

        }
        return FORMAT_DATE_MONTH.format(this)
    }

}