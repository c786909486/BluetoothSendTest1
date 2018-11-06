package com.axun.bluetoothsendtest

import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v7.app.AppCompatActivity
import android.widget.ArrayAdapter
import android.widget.Toast
import com.inuker.bluetooth.library.BluetoothClient
import com.inuker.bluetooth.library.search.SearchRequest
import com.inuker.bluetooth.library.search.SearchResult
import com.inuker.bluetooth.library.search.response.SearchResponse
import kotlinx.android.synthetic.main.activity_bluetooth_list.*

class BluetoothListActivity :AppCompatActivity() {
    private lateinit var mClient: BluetoothClient
    private lateinit var arrayAdapter: ArrayAdapter<String>
    private var items:ArrayList<String> = arrayListOf()

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bluetooth_list)
        ToolbarUtils.setToolbar(this,tool_bar)
        ToolbarUtils.addMiddleTitle(this,"搜索蓝牙",tool_bar)
        mClient = BluetoothClient(this)
        arrayAdapter = ArrayAdapter(this,android.R.layout.simple_list_item_1,android.R.id.text1,items)
        lv_item.adapter = arrayAdapter
        searchBluttooth()
    }

    private fun searchBluttooth(){
        val request:SearchRequest = SearchRequest.Builder()
                .searchBluetoothLeDevice(3000, 3)   // 先扫BLE设备3次，每次3s
                .searchBluetoothClassicDevice(5000) // 再扫经典蓝牙5s
                .searchBluetoothLeDevice(2000)      // 再扫BLE设备2s
                .build()
        mClient.search(request,object :SearchResponse{
            override fun onSearchStopped() {
                showToast("停止搜索")
            }

            override fun onSearchStarted() {
                showToast("开始搜索")
            }

            override fun onDeviceFounded(device: SearchResult?) {
                if (!items.contains(device!!.name+device.address)){
                    items.add(device!!.name+device.address)
                }
                arrayAdapter.notifyDataSetChanged()
            }

            override fun onSearchCanceled() {
                showToast("取消搜索")
            }

        })
    }
    private fun showToast(msg:String){
        Toast.makeText(this,msg, Toast.LENGTH_SHORT).show()
    }

}