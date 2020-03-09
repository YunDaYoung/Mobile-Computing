package com.example.reminder2020

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Message
import android.util.Log
import android.widget.Toast
import androidx.room.Room
import kotlinx.android.synthetic.main.activity_time.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast
import java.util.*

class TimeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_time)

        time_create.setOnClickListener {

//            val calendar = GregorianCalendar(
//
//                date_Picker.year,
//                date_Picker.month,
//                date_Picker.dayOfMonth,
//                time_Picker.currentHour,
//                time_Picker.currentMinute
//            )

            val calendar =

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                GregorianCalendar(

                    date_Picker.year,
                    date_Picker.month,
                    date_Picker.dayOfMonth,
                    time_Picker.hour,
                    time_Picker.minute
                )


            } else {

                GregorianCalendar(
                    date_Picker.year,
                    date_Picker.month,
                    date_Picker.dayOfMonth,
                    time_Picker.currentHour,
                    time_Picker.currentMinute)
            }

            Log.d("LAB7", "picked year is " + date_Picker.year)
            Log.d("LAB7", "picked month is " + date_Picker.month)

            if ((et_message.text.toString() != "") && (calendar.timeInMillis > System.currentTimeMillis())) {
                val reminder = Reminder(
                    uid = null,
                    time = calendar.timeInMillis,
                    location = null,
                    message = et_message.text.toString()
                )

                doAsync {
                    val db = Room.databaseBuilder(
                        applicationContext,
                        AppDatabase::class.java,
                        "reminders"
                    ).build()
                    db.reminderDao().insert(reminder)
                    db.close()

                    setAlarm(reminder.time!!, reminder.message)

                    finish()
                }
            } else {
                toast("Reminder cannot be scheduled for the past time and should contain some text")
            }
        }
    }


    private fun setAlarm(time: Long, message: String) {

        val intent = Intent(this, ReminderReceiver::class.java)
        intent.putExtra("message", message)

        val pendingIntent = PendingIntent.getBroadcast(this, 1, intent, PendingIntent.FLAG_ONE_SHOT)

        val manager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        manager.setExact(AlarmManager.RTC, time, pendingIntent)

        runOnUiThread {
            toast("Reminder is created")
        }

    }
}