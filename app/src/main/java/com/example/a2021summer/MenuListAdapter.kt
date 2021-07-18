package com.example.a2021summer

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MenuListAdapter(var context: Context,var data: MutableList<MenuData>) : RecyclerView.Adapter<MenuListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.menu_list,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount(): Int {
        return data.size
    }
    inner class ViewHolder(view : View) : RecyclerView.ViewHolder(view) {
        var menuname = itemView.findViewById<TextView>(R.id.menuname)
        var menuprice = itemView.findViewById<TextView>(R.id.menuprice)
        fun bind(menudata: MenuData){
            menuname.text = menudata.name
            menuprice.text = menudata.price.toString()
        }
    }
}