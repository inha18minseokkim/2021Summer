package com.example.a2021summer

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
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
        var menuidx: Int = 0
        @SuppressLint("ResourceAsColor")
        fun bind(menudata: MenuData){
            menuname.text = menudata.name
            menuprice.text = menudata.price.toString()
            menuidx = menudata.idx
            itemView.setOnClickListener{
                Log.d("MenuListAdapter",menuidx.toString())
                var tmpactivity = context as SubActivity
                var status = tmpactivity.selected[menuidx]
                if(status == 0){//선택안됨
                    tmpactivity.selected[menuidx] = 1
                    //itemView.setBackgroundColor(R.color.design_default_color_on_secondary)
                    Log.d("MenuListAdapter","Selected")
                } else {//선택됨
                    tmpactivity.selected[menuidx] = 0
                    //itemView.setBackgroundColor(R.color.design_default_color_background)gigigigiugig
                    Log.d("MenuListAdapter","UnSelected")
                }
            }
        }
    }
}