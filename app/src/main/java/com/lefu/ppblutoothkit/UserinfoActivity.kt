package com.lefu.ppblutoothkit

import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import com.lefu.ppblutoothkit.util.DataUtil
import com.peng.ppscale.business.device.PPUnitType
import com.peng.ppscale.util.ByteUtil
import com.peng.ppscale.util.PPUtil
import com.peng.ppscale.util.UnitUtil
import com.peng.ppscale.util.UserUtil
import com.peng.ppscale.vo.PPUserGender
import com.peng.ppscale.vo.PPUserModel

class UserinfoActivity : Activity() {
    var height = 180
    var age = 18
    var unit = PPUnitType.Unit_KG
    var sex = PPUserGender.PPUserGenderMale
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_userinfo)

        onBtnClck()

        val userModel = DataUtil.util().userModel

        if (userModel != null) {
            height = userModel.userHeight
            age = userModel.age
            sex = userModel.sex
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
    }

    private fun onBtnClck() {

    }

    override fun onPause() {
        super.onPause()
        val userModel = PPUserModel.Builder()
            .setAge(age)
            .setHeight(height)
            .setSex(sex)
            .build()
        DataUtil.util().userModel = userModel
        DataUtil.util().unit = unit
    }

    fun onSave(view: View) {
        onBackPressed()
    }


}