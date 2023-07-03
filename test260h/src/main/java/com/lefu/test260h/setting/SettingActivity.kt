package com.lefu.test260h.setting

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import com.lefu.test260h.R

class SettingActivity : AppCompatActivity() {

    private var inputTimeEt: EditText? = null
    private var inputNumberEt: EditText? = null
    private var inputBleNameEt: EditText? = null
    private var inputRssiEt: EditText? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        initView()
    }

    fun initView() {
        inputTimeEt = findViewById(R.id.inputTimeEt)
        inputNumberEt = findViewById(R.id.inputNumberEt)
        inputBleNameEt = findViewById(R.id.inputBleNameEt)
        inputRssiEt = findViewById(R.id.inputRssiEt)

        inputTimeEt?.setText(SettingManager.get().time.div(1000).toString())
        inputNumberEt?.setText(SettingManager.get().count.toString())
        inputBleNameEt?.setText(SettingManager.get().deviceName.toString())
        inputRssiEt?.setText(SettingManager.get().rssi.toString())

    }

    override fun onBackPressed() {
        var time: String? = inputTimeEt?.text?.toString()
        var count: String? = inputNumberEt?.text?.toString()
        var deviceName = inputBleNameEt?.text?.toString()
        var rssi = inputRssiEt?.text?.toString()

        if (time?.isEmpty() ?: true) {
            SettingManager.get().setTime(10000L)
        } else {
            SettingManager.get().setTime(time?.toLong()?.times(1000L))
        }
        if (count?.isEmpty() ?: true) {
            SettingManager.get().setCount(20)
        } else {
            SettingManager.get().setCount(count?.toInt() ?: 20)
        }
        if (deviceName?.isEmpty() ?: true) {
//            SettingManager.get().setDeviceName(deviceName)
        } else {
            SettingManager.get().setDeviceName(deviceName)
        }
        if (rssi?.isEmpty() ?: true) {
            SettingManager.get().setRssi(60)
        } else {
            SettingManager.get().setRssi(rssi?.toInt() ?: 60)
        }
        super.onBackPressed()
    }


}