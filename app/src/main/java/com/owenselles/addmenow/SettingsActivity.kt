/*
 * Copyright (c) 2019 Owen Selles
 * All rights reserved.
 */

package com.owenselles.addmenow

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import androidx.preference.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*


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

        val db = FirebaseFirestore.getInstance()

        val TAG = "AddMeNow"

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)

            val currentUser = FirebaseAuth.getInstance().currentUser

            val docRef =
                db.collection("users").document(currentUser!!.uid)
            docRef.get()
                .addOnSuccessListener { document ->
                    val snapname = document.get("snap")
                    val gender = document.get("gender")
                    val birthdate = document.get("birthdate")

                    val username = findPreference<EditTextPreference>("SnapName") as EditTextPreference
                    username.text = snapname.toString()

                    val genderSetting = findPreference<ListPreference>("list_preference_gender") as ListPreference
                    genderSetting.value = gender.toString()

                    val ageSetting = findPreference<ListPreference>("age") as EditTextPreference
                    val parts = birthdate.toString().split(".")
                    val year = parts[2].toInt()
                    val month = parts[1].toInt()
                    val day = parts[0].toInt()
                    ageSetting.text = getAge(year, month, day).plus("yo - You can't change this")
                }
                .addOnFailureListener { exception ->
                    Log.d(TAG, "get failed with ", exception)
                }
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

        override fun onPreferenceTreeClick(preference: Preference?): Boolean {
            return if (preference!!.key == context!!.getString(R.string.logout_button)) {
                AuthUI.getInstance()
                    .signOut(activity!!.applicationContext)
                    .addOnCompleteListener {
                        val intent = Intent(activity, MainActivity::class.java)
                        startActivity(intent)
                    }
                true
            } else false
        }
    }
}