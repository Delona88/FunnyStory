package com.delonagames.funnystory.activities.host

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.delonagames.funnystory.R

class ListUsersAdapter(private val usersId: List<Int>) : RecyclerView.Adapter<ListUsersAdapter.ListUsersViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ListUsersViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.string_item, parent, false)
        return ListUsersViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ListUsersViewHolder,
        position: Int
    ) {
        holder.textViewId.text = "" + usersId[position]
    }

    override fun getItemCount(): Int {
        return usersId.size
    }

    inner class ListUsersViewHolder(itemView: View) :  RecyclerView.ViewHolder(itemView){
        val textViewId: TextView = itemView.findViewById(R.id.textViewId)
    }

}