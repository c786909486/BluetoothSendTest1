package com.axun.bluetoothsendtest

import android.app.AlertDialog
import android.bluetooth.BluetoothDevice
import android.content.*
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.os.RemoteException
import android.support.annotation.RequiresApi
import android.support.v7.app.AppCompatActivity
import android.util.Base64
import android.widget.Toast
import com.gprinter.aidl.GpService
import com.gprinter.command.EscCommand
import com.gprinter.command.GpCom
import com.gprinter.command.GpUtils
import com.gprinter.command.LabelCommand
import com.gprinter.io.GpDevice
import com.gprinter.service.GpPrintService
import com.inuker.bluetooth.library.BluetoothClient
import com.inuker.bluetooth.library.Constants.REQUEST_SUCCESS
import com.inuker.bluetooth.library.connect.response.BleConnectResponse
import com.inuker.bluetooth.library.model.BleGattProfile
import com.inuker.bluetooth.library.search.SearchRequest
import com.inuker.bluetooth.library.search.SearchResult
import com.inuker.bluetooth.library.search.response.SearchResponse
import com.lidroid.xutils.util.LogUtils
import kotlinx.android.synthetic.main.activity_bluetooth_list.*




class BluetoothListActivity : AppCompatActivity() {
    private lateinit var mClient: BluetoothClient
    private lateinit var arrayAdapter: DevicesAdapter
    private var items: ArrayList<BluetoothDevice> = arrayListOf()

    var mGpService: GpService? = null

    var select = 0

    private var conn: PrinterServiceConnection? = null

    inner class PrinterServiceConnection : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {
            mGpService = null
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            mGpService = GpService.Stub.asInterface(service);
        }

    }

    private fun bindPrintService() {
        conn = PrinterServiceConnection();
        var intent = Intent(this, GpPrintService::class.java)
        this.bindService(intent, conn, Context.BIND_AUTO_CREATE); // bindService
        // 注册实时状态查询广播
        registerReceiver(mBroadcastReceiver, IntentFilter(GpCom.ACTION_DEVICE_REAL_STATUS));
        registerReceiver(mBroadcastReceiver, IntentFilter(GpCom.ACTION_RECEIPT_RESPONSE));
        registerReceiver(mBroadcastReceiver, IntentFilter(GpCom.ACTION_LABEL_RESPONSE));
    }

    private val mBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (GpCom.ACTION_CONNECT_STATUS == intent!!.getAction()) {
                val type = intent.getIntExtra(GpPrintService.CONNECT_STATUS, 0)
                val id = intent.getIntExtra(GpPrintService.PRINTER_ID, 0)

                when (type) {
                    GpDevice.STATE_CONNECTING -> {


                    }
                    GpDevice.STATE_NONE -> {

                    }
                    GpDevice.STATE_VALID_PRINTER -> {

                    }
                    GpDevice.STATE_INVALID_PRINTER -> {

                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unBindPrintService()
    }

    /**
     * 解绑打印服务
     */
    public fun unBindPrintService() {
        unregisterReceiver(mBroadcastReceiver);
        disConnectToPrinter();
    }

    /**
     * 断开与打印机的连接
     */
    public fun disConnectToPrinter() {
        try {
            mGpService!!.closePort(select)
        } catch (e: RemoteException) {
            // TODO Auto-generated catch block e.printStackTrace();
        }
    }


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bluetooth_list)
        ToolbarUtils.setToolbar(this, tool_bar)
        ToolbarUtils.addMiddleTitle(this, "搜索蓝牙", tool_bar)

        bindPrintService()
        mClient = BluetoothClient(this)
        arrayAdapter = DevicesAdapter(this)
        lv_item.adapter = arrayAdapter
        searchBluttooth()

        lv_item.setOnItemClickListener { parent, view, position, id ->
            val device = arrayAdapter.getItem(position) as BluetoothDevice
            mClient.connect(device.address, object : BleConnectResponse {
                override fun onResponse(code: Int, data: BleGattProfile?) {
                    if (code == REQUEST_SUCCESS) {
                        var rel = 0
//                        Toast.makeText(this@BluetoothListActivity, "连接成功", Toast.LENGTH_SHORT).show()
                        select = position
                        rel = mGpService!!.openPort(position, 4, device.address, 0);

                        val r = GpCom.ERROR_CODE.values()[rel]
                        if (r == GpCom.ERROR_CODE.SUCCESS) {
                            LogUtils.d("打印机连接成功");
                            val dialog:AlertDialog = AlertDialog.Builder(this@BluetoothListActivity)
                                    .setMessage("确定打印吗？")
                                    .setNegativeButton("取消") { dialog, which ->
                                        dialog.dismiss()
                                    }.setPositiveButton("确定") { dialog, which ->
                                        dialog.dismiss()
                                        printTicket()
//                                        printTexr()
//                                        PrintSplitUtil.getPrintText(this@BluetoothListActivity,initData(),"三斧王","18869978285","123456789")
                                    }.create()
                            dialog.show()
                        } else if(r == GpCom.ERROR_CODE.DEVICE_ALREADY_OPEN){
                            val dialog:AlertDialog = AlertDialog.Builder(this@BluetoothListActivity)
                                    .setMessage("确定打印吗？")
                                    .setNegativeButton("取消") { dialog, which ->
                                        dialog.dismiss()
                                    }.setPositiveButton("确定") { dialog, which ->
                                        dialog.dismiss()
                                        printTicket()
//                                        printTexr()
//                                        PrintSplitUtil.getPrintText(this@BluetoothListActivity,initData(),"三斧王","18869978285","123456789")
                                    }.create()
                            dialog.show()
                        }else {
                            showToast(GpCom.getErrorText(r))
                        }

                    }
                }

            })
        }
    }

    private fun printTexr() {
        val esc = EscCommand()
        esc.addInitializePrinter()
        esc.addPrintAndFeedLines(3.toByte())
        esc.addSelectJustification(EscCommand.JUSTIFICATION.CENTER);// 设置打印居中
         esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF, EscCommand.ENABLE.ON, EscCommand.ENABLE.ON, EscCommand.ENABLE.OFF);
        // 设置为倍高倍宽
         esc.addText("Sample\n");
        // 打印文字
         esc.addPrintAndLineFeed();
        esc.addPrintAndLineFeed();
        esc.addText("Print bitmap!\n");
        esc.addSelectJustification(EscCommand.JUSTIFICATION.CENTER);
        esc.addGeneratePlus(LabelCommand.FOOT.F5, 255.toByte(), 255.toByte())
        val datas = esc.command // 发送数据
        val bytes = GpUtils.ByteTo_byte(datas)
        val sss = Base64.encodeToString(bytes, Base64.DEFAULT)
        val rs: Int

        try {
            rs = mGpService!!.sendEscCommand(select, sss);
            val r = GpCom.ERROR_CODE.values()[rs]
            if (r != GpCom.ERROR_CODE.SUCCESS) {
                Toast.makeText(applicationContext, GpCom.getErrorText(r), Toast.LENGTH_SHORT).show();
            }
        }catch (e:RemoteException ){

        }

    }

    private fun printTicket(){
        var ss = PrintSplitUtil.getPrintText(this,initData(),"三斧王","18869978285","18869978285");
        val rs: Int
        try {
            rs = mGpService!!.sendEscCommand(select, ss);
            val r = GpCom.ERROR_CODE.values()[rs]
            if (r != GpCom.ERROR_CODE.SUCCESS) {
                Toast.makeText(applicationContext, GpCom.getErrorText(r), Toast.LENGTH_SHORT).show();
            }
        }catch (e:RemoteException ){

        }
    }


    private fun searchBluttooth() {
        val request: SearchRequest = SearchRequest.Builder()
                .searchBluetoothLeDevice(3000, 3)   // 先扫BLE设备3次，每次3s
                .searchBluetoothClassicDevice(5000) // 再扫经典蓝牙5s
                .searchBluetoothLeDevice(2000)      // 再扫BLE设备2s
                .build()
        mClient.search(request, object : SearchResponse {
            override fun onSearchStopped() {
                showToast("停止搜索")
            }

            override fun onSearchStarted() {
                showToast("开始搜索")
            }

            override fun onDeviceFounded(device: SearchResult?) {
                if (!items.contains(device!!.device)) {
                    items.add(device.device)
                }
                arrayAdapter.items = items;
                arrayAdapter.notifyDataSetChanged()
            }

            override fun onSearchCanceled() {
                showToast("取消搜索")
            }

        })
    }

    private fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    private fun initData() : GoodsListInfo{
        val info:GoodsListInfo = GoodsListInfo();
        info.setExpress_sn("22222222")
        info.setGoods_amount("20")
        info.setOrder_amount("dasdasdadas")
        info.setSubsidy("asdadsada")
        return info;
    }

}