/*
 * Copyright (c) 2019 Owen Selles
 * All rights reserved.
 */

package com.owenselles.addmenow

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.widget.Toast
import androidx.preference.PreferenceScreen
import com.firebase.ui.auth.AuthUI


class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.settings, SettingsFragment())
            .commit()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


    }

    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)



        }
        override fun onPreferenceTreeClick(preference: Preference?): Boolean {
            return if (preference!!.key == context!!.getString(R.string.logout_button)) {
                //TODO add logout code
                AuthUI.getInstance()
                    .signOut(activity!!.applicationContext)
                    .addOnCompleteListener {
                        val intent = Intent(activity,MainActivity::class.java)
                        startActivity(intent)
                    }
                true
            } else false
        }
    }
}