package com.delonagames.funnystory.activities.host

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.delonagames.funnystory.R

class ListUsersAdapter(private val usersId: List<Int>, val buttonClickListener: ButtonClickListener) :
    RecyclerView.Adapter<ListUsersAdapter.ListUsersViewHolder>() {

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

    inner class ListUsersViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewId: TextView = itemView.findViewById(R.id.textViewUserId)
        val buttonRemove: Button = itemView.findViewById(R.id.buttonRemove)

        init {
            buttonRemove.setOnClickListener {
                Log.d("buttonRemove","Click buttonRemove")
                buttonClickListener.onButtonRemoveClick(usersId[adapterPosition])
            }
        }
    }

    interface ButtonClickListener {
        fun onButtonRemoveClick(id: Int)
    }

}