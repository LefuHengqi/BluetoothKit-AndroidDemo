package com.lefu.ppscale.ble.userinfo

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView
import com.peng.ppscale.business.device.PPUnitType
import com.peng.ppscale.vo.PPUserGender
import android.os.Bundle
import com.lefu.ppscale.ble.R
import com.peng.ppscale.business.ble.PPScale
import android.content.Intent
import com.lefu.ppscale.ble.function.FunctionListActivity
import com.lefu.ppscale.ble.wififunction.WifiFunctionListActivity
import com.lefu.ppscale.ble.activity.BindingDeviceActivity
import com.lefu.ppscale.ble.activity.DeviceListActivity
import android.widget.EditText
import android.text.TextWatcher
import android.text.Editable
import com.peng.ppscale.util.ByteUtil
import com.peng.ppscale.util.UnitUtil
import com.peng.ppscale.vo.PPBodyFatModel
import com.peng.ppscale.vo.PPUserModel
import android.content.pm.PackageManager
import android.os.UserManager
import android.view.View
import android.widget.Button
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.lefu.ppscale.ble.model.DataUtil
import com.peng.ppscale.util.PPUtil
import com.peng.ppscale.util.UserUtil
import kotlinx.android.synthetic.main.activity_main.*

class UserinfoActivity : AppCompatActivity() {
    var height = 180
    var age = 18
    var maternityMode = 0 //孕妇模式1  默认0
    var sportMode = 0 //运动员模式1  默认0
    var unit = PPUnitType.Unit_KG
    var sex = PPUserGender.PPUserGenderMale
    var group = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_userinfo)

        onBtnClck()

        val userModel = DataUtil.util().userModel

        if (userModel != null) {
            height = userModel.userHeight
            age = userModel.age
            sex = userModel.sex
            sportMode = if (userModel.isAthleteMode) 1 else 0
            maternityMode = if (userModel.isPregnantMode) 1 else 0
        }
        unit = DataUtil.util().unit

        // 身高
        val heightET = findViewById<EditText>(R.id.editText3)
        heightET.setText(height.toString())
        heightET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                val number = s.toString()
                if (number.length > 0) {
                    val height = number.toInt()
                    this@UserinfoActivity.height = height
                }
            }
        })

        //年龄
        val ageET = findViewById<EditText>(R.id.editText6)
        ageET.setText(age.toString())
        ageET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                val number = s.toString()
                if (number.length > 0) {
                    val age = number.toInt()
                    this@UserinfoActivity.age = age
                }
            }
        })
        //单位
        val unitET = findViewById<EditText>(R.id.editText4)
        unitET.setText(PPUtil.getWeightUnitNum(unit).toString())
        unitET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                val number = s.toString()
                if (number.length > 0) {
                    val unit = ByteUtil.hexToTen(number)
                    this@UserinfoActivity.unit = UnitUtil.getUnitType(unit, "")
                }
            }
        })

        //性别
        val sexET = findViewById<EditText>(R.id.editText5)
        sexET.setText(UserUtil.getEnumSex(sex).toString())
        sexET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                val number = s.toString()
                if (number.length > 0) {
                    val sex = number.toInt()
                    if (sex == 0) {
                        this@UserinfoActivity.sex = PPUserGender.PPUserGenderFemale
                    } else {
                        this@UserinfoActivity.sex = PPUserGender.PPUserGenderMale
                    }
                }
            }
        })

        //用户组
        val groupET = findViewById<EditText>(R.id.editText7)
        groupET.setText("0")
        groupET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                val number = s.toString()
                if (number.length > 0) {
                    val group = number.toInt()
                    this@UserinfoActivity.group = group
                }
            }
        })

        //孕妇模式
        val maternityModeET = findViewById<EditText>(R.id.editText8)
        maternityModeET.setText(maternityMode.toString())
        maternityModeET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                val number = s.toString()
                if (number.length > 0) {
                    val maternityMode = number.toInt()
                    this@UserinfoActivity.maternityMode = maternityMode
                }
            }
        })

        //运动员模式
        val sportModeET = findViewById<EditText>(R.id.editText9)
        sportModeET.setText(sportMode.toString())
        sportModeET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                val number = s.toString()
                if (number.length > 0) {
                    val sportMode = number.toInt()
                    this@UserinfoActivity.sportMode = sportMode
                }
            }
        })
    }

    private fun onBtnClck() {

    }

    override fun onPause() {
        super.onPause()
        val userModel = PPUserModel.Builder()
            .setAge(age)
            .setHeight(height)
            .setGroupNum(group)
            .setSex(sex)
            .setPregnantMode(maternityMode == 1) //孕妇模式 1  正常模式0  默认0
            .setAthleteMode(sportMode == 1) //运动员模式 1  正常模式0  默认0
            .build()
        DataUtil.util().userModel = userModel
        DataUtil.util().unit = unit
    }

    fun onSave(view: View) {
        onBackPressed()
    }


}