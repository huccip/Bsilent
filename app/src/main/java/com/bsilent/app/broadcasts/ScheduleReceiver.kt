package com.bsilent.app.broadcasts

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.bsilent.app.other.Constants
import com.bsilent.app.services.ScheduleService

class ScheduleReceiver : BroadcastReceiver() {

    override fun onReceive(c: Context, intent: Intent) {
        if (intent.action == Constants.START_SCHEDULE_SERVICE_ACTION) {
            //todo remove this log
            Log.e("test receiver","call")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                c.startForegroundService(
                    Intent(
                        c,
                        ScheduleService::class.java
                    ).also {
                        it.action = Constants.START_SCHEDULE_SERVICE_ACTION
                        it.putExtra(
                            "schedule_id",
                            intent.getLongExtra("schedule_id",-1)
                        )
                    }
                )
            }else{
                c.startService(
                    Intent(
                        c,
                        ScheduleService::class.java
                    ).also {
                        it.action = Constants.START_SCHEDULE_SERVICE_ACTION
                        it.putExtra(
                            "schedule_id",
                            intent.getLongExtra("schedule_id",-1)
                        )
                    }
                )
            }
        }
    }

}