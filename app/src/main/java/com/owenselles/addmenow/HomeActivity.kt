/*
 * Copyright (c) 2019 Owen Selles
 * All rights reserved.
 */

package com.owenselles.addmenow

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_home.*
import java.util.*
import kotlin.time.ExperimentalTime
import kotlin.time.milliseconds


class HomeActivity : AppCompatActivity() {

    val db = FirebaseFirestore.getInstance()

    val TAG = "AddMeNow"

    var time = "Post"

    var cTimer: CountDownTimer? = null

    var isWaiting = false

    @ExperimentalTime
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        MobileAds.initialize(this)
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)

        val fragmentAdapter = MyPagerAdapter(supportFragmentManager)
        viewpager_main.adapter = fragmentAdapter

        tabs_main.setupWithViewPager(viewpager_main)

        RateButton.setOnClickListener {
            launchMarket()
        }

        SnapButton.setOnClickListener {
            shareApp()
        }

        PostButton.setOnClickListener {
            //todo open post popup
            val values = arrayOf(
                "\uD83D\uDD25 Streaks",
                "\uD83C\uDF89 Friends",
                "\uD83C\uDF51 Friends +",
                "\uD83D\uDC40 Views"
            )
            var looking = ""

            val builder =
                AlertDialog.Builder(this, R.style.ThemeOverlay_MaterialComponents_Dialog_Alert)
            builder.setTitle("Tell people why to add you")
            builder.setSingleChoiceItems(
                values, -1
            ) { _, item ->
                when (item) {
                    0 ->
                        looking = "streak"

                    1 ->
                        looking = "friends"

                    2 ->
                        looking = "friends+"

                    3 ->
                        looking = ""

                }
            }
            builder.setPositiveButton(time) { _, _ ->
                if (!isWaiting) {
                    getDetails(looking)
                    startTimer()
                    isWaiting = true
                } else {
                    Toast.makeText(this, "please wait", Toast.LENGTH_LONG).show()
                }
            }
            builder.show()
        }
    }

    @ExperimentalTime
    fun startTimer() {
        cTimer = object : CountDownTimer(120000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val minutes = millisUntilFinished.milliseconds.inMinutes.toString()
                time = "Please wait ${minutes.subSequence(0,1)} minutes before posting again!"
            }

            override fun onFinish() {
                time = "Post"
                isWaiting = false
                sendNotification()

            }
        }
        (cTimer as CountDownTimer).start()
    }

//    fun cancelTimer() {
//        cTimer?.cancel()
//    }

    fun sendNotification() {
        createNotificationChannel()

        val intent = Intent(this, HomeActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, 0)

        val builder = NotificationCompat.Builder(this, "Reminder")
            .setSmallIcon(R.drawable.common_full_open_on_phone)
            .setContentTitle("AddMeNow")
            .setContentText("You can post again!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(this)) {
            // notificationId is a unique int for each notification that you must define
            notify(1, builder.build())
        }
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Reminders"
            val descriptionText = "Used to send reminders when you can post."
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("Reminder", name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.settings, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == R.id.your_item_id) {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
            true
        } else super.onOptionsItemSelected(item)
    }

    private fun shareApp() {
        val sharingIntent = Intent(android.content.Intent.ACTION_SEND)
        sharingIntent.type = "text/plain"
        val shareBody =
            "Hey, i use this cool application to get more friends on Snapchat: https://play.google.com/store/apps/details?id=$packageName"
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "AddMeNow")
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody)
        startActivity(Intent.createChooser(sharingIntent, "Share this app via"))
    }

    private fun launchMarket() {
        val uri = Uri.parse("market://details?id=$packageName")
        val myAppLinkToMarket = Intent(Intent.ACTION_VIEW, uri)
        try {
            startActivity(myAppLinkToMarket)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, " unable to find market app", Toast.LENGTH_LONG).show()
        }
    }

    private fun getDetails(looking: String) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val docRef =
            db.collection("users").document(currentUser!!.uid)
        docRef.get()
            .addOnSuccessListener { document ->
                val birthdate = document.get("birthdate")
                val gender = document.get("gender")
                val snap = document.get("snap")
                addPost(birthdate as String, gender as String, snap as String, looking)
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
            }
    }

    private fun addPost(birthdate: String, gender: String, snap: String, looking: String) {
        val parts = birthdate.split(".")
        val year = parts[2].toInt()
        val month = parts[1].toInt()
        val day = parts[0].toInt()
        val age = getAge(year, month, day)


        val post = hashMapOf(
            "snapName" to snap,
            "gender" to gender,
            "age" to age,
            "looking" to looking,
            "timestamp" to FieldValue.serverTimestamp()
        )
        val currentUser = FirebaseAuth.getInstance().currentUser
        val userSettings = db.collection("posts").document(currentUser!!.uid)
        userSettings
            .set(post)
            .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully updated!") }
            .addOnFailureListener { e -> Log.w(TAG, "Error updating document", e) }

    }

    private fun getAge(year: Int, month: Int, day: Int): String {
        val dob = Calendar.getInstance()
        val today = Calendar.getInstance()

        dob.set(year, month, day)

        var age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR)

        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
            age--
        }

        val ageInt = age

        return ageInt.toString()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        this.finishAffinity()
    }
}