package com.gauravbora.task_2.fabricio.notifications

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.gauravbora.task_2.R
import com.gauravbora.task_2.fabricio.notifications.adapters.SwipeItem
import com.gauravbora.task_2.fabricio.notifications.database.CallbackSql
import com.gauravbora.task_2.fabricio.notifications.database.DataBase
import com.gauravbora.task_2.fabricio.notifications.database.TracesAdapter
import com.gauravbora.task_2.fabricio.notifications.models.TrackNotification
import kotlinx.android.synthetic.main.activity_notifications_log.*

class NotificationsLogActivity : AppCompatActivity(), CallbackSql<List<TrackNotification>> {
    var origin: String? = null
    var dataBase: DataBase? = null
    val source = ArrayList<TrackNotification>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notifications_log)

//        showDialog()



        setSupportActionBar(this.toolbar)

        intent.extras?.getString("key_origin")?.let {
            this.origin = it
            this.toolbar.subtitle = it
            this.supportActionBar?.setDisplayHomeAsUpEnabled(true)
            this.supportActionBar?.setDisplayShowHomeEnabled(true)
            this.toolbar.setNavigationIcon(R.drawable.ic_go_back)
            this.toolbar.setNavigationOnClickListener {
                this.onBackPressed()
            }
        }

        this.dataBase = DataBase.instance(this)

        recyclerView.adapter = TracesAdapter(source, origin).apply {
            this.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
                override fun onChanged() {
                    applyEmptyView()
                }
            })
        }


        val swipeItem = object : SwipeItem(this) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                if (direction == ItemTouchHelper.LEFT) {
                    val adapter = recyclerView.adapter as TracesAdapter

                    source.removeAt(viewHolder.adapterPosition).let { trace ->
                        if (origin.isNullOrEmpty()) {
                            dataBase?.deleteFromOrigin(trace.origin)
                        } else {
                            dataBase?.delete(trace._id)
                        }
                    }
                    applyEmptyView()
                    adapter.notifyItemRemoved(viewHolder.adapterPosition)
                }
            }

        }
        ItemTouchHelper(swipeItem).attachToRecyclerView(recyclerView)

        dataBase?.let {
            if (this.origin.isNullOrEmpty()) {
                it.registerObserver(this)

            } else {
                loadData(it.getTracesByGroup(this.origin!!))
            }
        }
    }

//    private fun showDialog() {
//        val tl = databaseList().size
//        val lastNotificationInfo = dataBase?.getAllTracesGrouped()
//        val notificationname = lastNotificationInfo?.last()?.title
//        val time = lastNotificationInfo?.last()?.time
//        val source=lastNotificationInfo?.last()?.raw
//
//        val builder = AlertDialog.Builder(this)
//        builder.setMessage("Total Responses \n $tl")
//            .setPositiveButton("See Here",
//                DialogInterface.OnClickListener { dialog, id ->
//                    // START THE GAME!
////                    Toast.makeText(this, "Start Clicked", Toast.LENGTH_SHORT).show()
//                })
//            .setNegativeButton(" ",
//                DialogInterface.OnClickListener { dialog, id ->
//                    // User cancelled the dialog
////                    Toast.makeText(this, "Cancel Clicked", Toast.LENGTH_SHORT).show()
//                })
//        // Create the AlertDialog object and return it
//        builder.create()
//
////showing the dialog 1
//        builder.show()
//
//    }


private fun applyEmptyView() {
    if (source.isEmpty()) {
        emptyLayout?.visibility = View.VISIBLE
    } else {
        emptyLayout?.visibility = View.GONE
    }
}

override fun onResult(result: List<TrackNotification>) {
    loadData(result)
}

private fun loadData(list: List<TrackNotification>) {

    source.clear()
    source.addAll(list)
    recyclerView.adapter?.notifyDataSetChanged()
}

override fun onDestroy() {
    super.onDestroy()
    dataBase?.removeObserver(this)
}

}