package com.example.myktactil

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView

class MyAdapter(private val context: Context): BaseAdapter() {
    private lateinit var lblcordX: TextView
    private lateinit var lblcordY: TextView
    private lateinit var lblminombre: TextView
    private lateinit var imgimagen: ImageView
    override fun getCount(): Int {
        return MainActivity.nodes.size
    }
    override fun getItemId(position: Int): Long {
        return position.toLong()
    }
    override fun getItem(position: Int): Any {
        return position
    }
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var convertView = convertView
        convertView = LayoutInflater.from(context).inflate(R.layout.misfilas,parent,false)
        lblcordX = convertView.findViewById(R.id.lblcordX)
        lblcordY = convertView.findViewById(R.id.lblcordY)
        lblminombre = convertView.findViewById(R.id.lblminombre)
        imgimagen = convertView.findViewById(R.id.imgimagen)

        lblcordX.setText(MainActivity.nodes[position].x.toString())
        lblcordY.setText(MainActivity.nodes[position].y.toString())
        lblminombre.setText(MainActivity.nodes[position].nombre)

        val primeraletra: String = lblminombre.getText().toString().toLowerCase().substring(0,1)
        val idimagen = context.resources.getIdentifier(primeraletra,"drawable",context.packageName)
        imgimagen.setImageResource(idimagen)
        return  convertView
    }
}