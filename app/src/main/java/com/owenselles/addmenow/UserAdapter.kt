/*
 * Copyright (c) 2019 Owen Selles
 * All rights reserved.
 */

package com.owenselles.addmenow

import android.graphics.Color.*
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.list_item.view.*
import androidx.core.content.ContextCompat.startActivity
import android.content.Intent
import android.net.Uri


class UsersAdapter(val users: List<User>) : RecyclerView.Adapter<UsersAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = users.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.setBackgroundColor(parseColor("#F5F5F5"))
        holder.snapName.text = users[position].snapName
        holder.age.text = users[position].age.plus("yo")
        if (users[position].gender.equals("male")) {
            holder.gender.text = """👦"""
        } else {
            holder.gender.text = """👧"""
        }
        if (users[position].looking.equals("streak")) {
            holder.looking.text = """🔥"""
        } else if (users[position].looking.equals("friend")) {
            holder.looking.text = """🎉"""
        } else if (users[position].looking.equals("friend+")) {
            holder.looking.text = """🍑"""
        } else {
            holder.looking.text = """👀"""
        }
        holder.button.setOnClickListener() {
            //TODO open add screen
            val alertDialogBuilder = AlertDialog.Builder(holder.itemView.context, R.style.ThemeOverlay_MaterialComponents_Dialog_Alert)
//                .setTitle(users[position].snapName)
                .setMessage("Username copied to clipboard")
                .setPositiveButton("\uD83D\uDC7B Open in Snapchat") { dialog, which ->
                    val openURL = Intent(Intent.ACTION_VIEW)
                    openURL.data = Uri.parse("https://www.snapchat.com/add/" + holder.snapName)
                    startActivity(holder.itemView.context, openURL, null)

                }
                .setNegativeButton("\uD83D\uDEAB Dismiss") { dialog, which ->
                    dialog.dismiss()
                }
                .show()
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val snapName: TextView = view.list_username
        val age: TextView = view.list_age
        val gender: TextView = view.textView3
        val looking: TextView = view.emoji
        val button: Button = view.button
    }
}


