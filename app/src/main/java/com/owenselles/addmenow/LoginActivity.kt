/*
 * Copyright (c) 2019 Owen Selles
 * All rights reserved.
 */

package com.owenselles.addmenow

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity : AppCompatActivity() {

    private val RC_SIGN_IN = 123

    val db = FirebaseFirestore.getInstance()

    val TAG = "AddMeNow"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Choose authentication providers
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build()
        )

        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setTheme(R.style.AppTheme)
                .setTosAndPrivacyPolicyUrls(
                    "https://owenselles.com/terms.html",
                    "https://owenselles.com/privacy.html"
                )
                .build(),
            RC_SIGN_IN
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)
            if (resultCode == Activity.RESULT_OK) {

                if (response!!.isNewUser) {
                    val currentUser = FirebaseAuth.getInstance().currentUser
                    val user = hashMapOf(
                        "uid" to currentUser!!.uid
                    )
                    db.collection("users").document(currentUser.uid)
                        .set(user as Map<String, Any>)
                        .addOnSuccessListener { Log.d(TAG, "User successfully written!") }
                        .addOnFailureListener { _ -> Log.w(TAG, "Error writing user!") }
                    Toast.makeText(this, "Welcome!", Toast.LENGTH_LONG).show()
                    val intent = Intent(this, SetupActivity::class.java)
                    startActivity(intent)

                } else {
                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                    Toast.makeText(this, "Welcome back!", Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(this, "Can't login!", Toast.LENGTH_LONG).show()
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }
        }
    }
}
