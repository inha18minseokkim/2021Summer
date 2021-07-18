package com.example.a2021summer

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import android.widget.Toast.*
import androidx.recyclerview.widget.RecyclerView

class ShopListAdapter(private val context: Context, var data: MutableList<ShopData>) : RecyclerView.Adapter<ShopListAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShopListAdapter.ViewHolder {

        Log.d("recycleView","onCreateViewHolder")
        val view = LayoutInflater.from(context).inflate(R.layout.shop_list,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ShopListAdapter.ViewHolder, position: Int) {

        Log.d("recycleView","onBindViewHolder")
        holder.bind(data[position])
    }

    override fun getItemCount(): Int {
        
        return data.size
    }
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
        private val shoplogo = itemView.findViewById<ImageView>(R.id.shoplogo)
        private val shopname = itemView.findViewById<TextView>(R.id.shopname)
        private val shopscore = itemView.findViewById<TextView>(R.id.shopscore)
        fun bind(item: ShopData){
            Log.d("recycleview",item.name.toString())
            shopname.text = item.name
            shopscore.text = item.score.toString()
            itemView.setOnClickListener{
                Intent(context, SubActivity::class.java).apply{
                    putExtra("passdata",arrayOf(item.menutable,item.info,item.name))
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }.run{context.startActivity(this)}
            }
        }
    }
}
