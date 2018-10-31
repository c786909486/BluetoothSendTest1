package com.axun.bluetoothsendtest

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import com.inuker.bluetooth.library.BluetoothClient
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(),View.OnClickListener {


    private lateinit var mClient:BluetoothClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mClient = BluetoothClient(this@MainActivity)
        btn_open.setOnClickListener(this)
        btn_close.setOnClickListener(this)
        btn_search.setOnClickListener(this)


    }
    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.btn_open ->openBluetooth()
            R.id.btn_close -> closeBluetooth()
            R.id.btn_search -> startActivity(Intent(this@MainActivity,BluetoothListActivity::class.java))
        }
    }

    private fun openBluetooth(){
        if (!mClient.isBluetoothOpened){
            mClient.openBluetooth()
        }else{
            showToast("蓝牙已打开")
        }
    }

    private fun closeBluetooth(){
        if (mClient.isBluetoothOpened){
            mClient.closeBluetooth()
        }else{
            showToast("蓝牙已关闭")
        }
    }

    private fun showToast(msg:String){
        Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();
    }
}
