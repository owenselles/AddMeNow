/*
 * Copyright (c) 2019 Owen Selles
 * All rights reserved.
 */

package com.owenselles.addmenow

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_home.*
import android.widget.Toast
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.view.Menu
import android.view.MenuItem
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds


class HomeActivity : AppCompatActivity() {

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
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.settings, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId === R.id.your_item_id) {
            val intent = Intent(this,SettingsActivity::class.java)
            startActivity(intent)
            true
        } else super.onOptionsItemSelected(item)
    }

    private fun shareApp() {
        val sharingIntent = Intent(android.content.Intent.ACTION_SEND)
        sharingIntent.type = "text/plain"
        val shareBody =
            "Hey, i use this cool application: https://play.google.com/store/apps/details?id=$packageName"
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

    override fun onBackPressed() {
        super.onBackPressed()
        this.finishAffinity()
    }
}