package com.owenselles.addmenow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_all.*


/**
 * A simple [Fragment] subclass.
 */
class AllFragment : Fragment() {

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
        AddAllToList()

        swipeContainer.setOnRefreshListener {
            AddAllToList()
        }
    }

    fun AddAllToList() {
        val users = mutableListOf<User>()
        for (i in 0..25) {
            users.add(User("owenselles", "18", "just now"))
        }

        
        AllRecycler.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = UsersAdapter(users)
        }
        swipeContainer.isRefreshing = false
    }
}