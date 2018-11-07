package com.axun.bluetoothsendtest

import android.bluetooth.BluetoothDevice
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

class DevicesAdapter(private val context: Context) :BaseAdapter() {

    var items:List<BluetoothDevice>?=null

    set(items) {
        field = items
        notifyDataSetChanged()
    }


    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var convertView:View = LayoutInflater.from(context).inflate(R.layout.item_bluetooth,parent,false)
        var name:TextView = convertView.findViewById(R.id.tv_name)
        var address:TextView = convertView.findViewById(R.id.tv_address)
        name.text = items!![position].name
        address.text = items!![position].address

        return convertView
    }


    override fun getItem(position: Int): Any? {
        return if(items==null)null else items!![position]
    }

    override fun getItemId(position: Int): Long {
        return if (items==null)0 else position.toLong()
    }

    override fun getCount(): Int {
        return if (items==null) 0 else items!!.size
    }
}