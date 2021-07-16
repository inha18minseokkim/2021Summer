package com.example.a2021summer

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ShopListAdapter(private val context: Context, var data: MutableList<ShopData>) : RecyclerView.Adapter<ShopListAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShopListAdapter.ViewHolder {
        TODO("Not yet implemented")

        val view = LayoutInflater.from(context).inflate(R.layout.shop_list,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ShopListAdapter.ViewHolder, position: Int) {
        TODO("Not yet implemented")
        holder.bind(data[position])
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
        return data.size
    }
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
        private val shoplogo = itemView.findViewById<ImageView>(R.id.shoplogo)
        private val shopname = itemView.findViewById<TextView>(R.id.shopname)
        private val shopscore = itemView.findViewById<TextView>(R.id.shopscore)
        fun bind(item: ShopData){
            shopname.text = item.name
            shopscore.text = item.score.toString()
        }
    }
}
