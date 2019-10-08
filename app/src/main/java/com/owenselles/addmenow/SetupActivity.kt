/*
 * Copyright (c) 2019 Owen Selles
 * All rights reserved.
 */

package com.owenselles.addmenow

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.RadioButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_setup.*
import java.text.SimpleDateFormat
import java.util.*

class SetupActivity : AppCompatActivity() {

    val db = FirebaseFirestore.getInstance()

    val TAG = "AddMeNow"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setup)
        title = "Setup"

        val textView: TextView = BirthdayInput
        textView.text = SimpleDateFormat("dd.MM.yyyy").format(System.currentTimeMillis())
        val cal = Calendar.getInstance()
        val dateSetListener =
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                val myFormat = "dd.MM.yyyy" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                textView.text = sdf.format(cal.time)
            }

        textView.setOnClickListener {
            DatePickerDialog(
                this@SetupActivity, dateSetListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        ContinueButton.setOnClickListener {
            //get snapname
            val snapName = SnapNameInput.text.toString()

            //Get gender
            val selectedId = GenderRadioGroup.checkedRadioButtonId
            val radioButton = findViewById<View>(selectedId) as RadioButton
            val gender = radioButton.text.toString()

            //format date
            val myFormat = "dd.MM.yyyy" // mention the format you need
            val sdf = SimpleDateFormat(myFormat, Locale.US)

            //check if 18+
            val ageCheck = Calendar.getInstance()
            ageCheck.add(Calendar.YEAR, -13)
            var allGood: Boolean? = true
            if (!cal.time.before(ageCheck.time)) {
                BirthdateInputLayout.error = "You need to be over 13 years old!"
                allGood = false
            }

            //check if name is valid
            if (snapName.length <= 2) {
                SnapInputLayout.error = "Enter username please!"
                allGood = false
            }
            //add user and data to firestore
            if (allGood == true) {
                val currentUser = FirebaseAuth.getInstance().currentUser
                val user = hashMapOf(
                    "snap" to snapName,
                    "birthdate" to sdf.format(cal.time),
                    "gender" to gender,
                    "updated" to FieldValue.serverTimestamp()

                )
                if (currentUser != null) {
                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                    db.collection("users").document(currentUser.uid)
                        .update(user as Map<String, Any>)
                        .addOnSuccessListener { Log.d(TAG, "User successfully written!") }
                        .addOnFailureListener { _ -> Log.w(TAG, "Error writing user!") }


                }
            }
        }
    }

    override fun onBackPressed() {
        //Deletes the user auth and firestore since it not finished setup
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            db.collection("users").document(user.uid)
                .delete()
                .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully deleted!") }
                .addOnFailureListener { e -> Log.w(TAG, "Error deleting document", e) }
        }
        user?.delete()
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "User account deleted.")
                }
            }

        super.onBackPressed()
        this.finishAffinity()
    }
}
