package com.example.mykcontactos

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView

class MyAdapter(private val context: Context): BaseAdapter() {
    private lateinit var lblminombre:TextView
    private lateinit var lblmialias:TextView
    private lateinit var lblmicodigo:TextView
    private lateinit var imgimagen: ImageView
    override fun getCount(): Int {
        return Global.miscontactos.size
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
        lblminombre = convertView.findViewById(R.id.lblminombre)
        lblmialias = convertView.findViewById(R.id.lblmialias)
        lblmicodigo = convertView.findViewById(R.id.lblmicodigo)
        imgimagen = convertView.findViewById(R.id.imgimagen)

        lblminombre.setText(Global.miscontactos[position].nombre)
        lblmialias.setText(Global.miscontactos[position].alias)
        lblmicodigo.setText(Global.miscontactos[position].codigo)

        val primeraletra: String = lblminombre.getText().toString().toLowerCase().substring(0,1)
        val idimagen = context.resources.getIdentifier(primeraletra,"drawable",context.packageName)
        imgimagen.setImageResource(idimagen)
        return  convertView
    }
}