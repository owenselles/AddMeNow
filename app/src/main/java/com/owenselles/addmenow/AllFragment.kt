/*
 * Copyright (c) 2019 Owen Selles
 * All rights reserved.
 */

package com.owenselles.addmenow

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.fragment_all.*


/**
 * A simple [Fragment] subclass.
 */
class AllFragment : Fragment() {

    val db = FirebaseFirestore.getInstance()

    val TAG = "AddMeNow"

    private lateinit var linearLayoutManager: LinearLayoutManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_all, container, false)


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addAllToList()

        swipeContainer.setOnRefreshListener {
            addAllToList()
        }
    }

    private fun addAllToList() {
        val users = mutableListOf<User>()
        val docRef =
            db.collection("posts").orderBy("timestamp", Query.Direction.DESCENDING).limit(25)
        docRef.get()
            .addOnSuccessListener { document ->
                document.documents.forEach {
                    val e = it.toObject(User::class.java)
                    users.add(e!!)
                }
                AllRecycler.apply {
                    layoutManager = LinearLayoutManager(activity)
                    adapter = UsersAdapter(users)
                }
                swipeContainer.isRefreshing = false
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
            }
    }
}