package com.example.uaspraktikummobile.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class NotifReceiver: BroadcastReceiver()  {
    override fun onReceive(context: Context?, intent: Intent?) {
        val msg = intent?.getStringExtra("MESSAGE")
        if (msg != null) {
            Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
        }
    }

}