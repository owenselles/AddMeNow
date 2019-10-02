package com.owenselles.addmenow

import android.content.Context
import android.graphics.Color
import android.graphics.Color.*
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.list_item.view.*
import androidx.cardview.widget.CardView



class UsersAdapter(val users: List<User>) : RecyclerView.Adapter<UsersAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        return ViewHolder(view)    }

    override fun getItemCount() = users.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.setBackgroundColor(parseColor("#F5F5F5"))
        holder.snapName.text = users[position].snapName
        holder.age.text = users[position].age
        holder.lastSeen.text = users[position].lastSeen
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val snapName: TextView = view.list_username
        val age: TextView = view.list_age
        val lastSeen: TextView = view.list_lastseen

    }
}


