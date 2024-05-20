package com.lefu.ppblutoothkit.view

import android.graphics.Color
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.fragment.app.FragmentManager
import com.lefu.ppblutoothkit.R

/**
 *    author : whs
 *    e-mail : haisilen@163.com
 *    date   : 2021/9/4 9:21
 *    desc   :例子的使用
 */
class MsgDialog : BaseLeFuWhsDialog<MsgDialog>() {

    private var isShowTitle = false
    private var isShowPosBtn = false
    private var isShowPosBtn2 = false
    private var isShowNegBtn = false
    private var isShowNegBtn2 = false
    private var isShowTips = false

    private var titleText: CharSequence = ""

    private var messageText: CharSequence = ""

    private var mMessageTV:TextView? = null
    private var mTipsTV:TextView? = null

    private var negativeButtonText: CharSequence = ""
    private var negativeButtonClickListener: View.OnClickListener? = null
    private var negativeButtonColor: Int = Color.parseColor("#14CCAD")

    private var negativeButtonText2: CharSequence = ""
    private var negativeButtonClickListener2: View.OnClickListener? = null
    private var negativeButtonColor2: Int = Color.parseColor("#14CCAD")

    private var positiveButtonText: CharSequence = ""
    private var positiveButtonClickListener: View.OnClickListener? = null
    private var positiveButtonColor: Int = Color.parseColor("#FFFFFF")

    private var positiveButtonText2: CharSequence = ""
    private var positiveButtonClickListener2: View.OnClickListener? = null
    private var positiveButtonColor2: Int = Color.parseColor("#FFFFFF")

    /**
     * View Handler
     * The management of the relevant state of the view is written here
     */
    override fun viewHandler(): ViewHandlerListener? {
        return object : ViewHandlerListener() {
            override fun convertView(holder: ViewHolderComnonDialogWhs, dialog: BaseLeFuWhsDialog<*>) {
                holder.getView<TextView>(R.id.title_tv).apply {
                    visibility = if (isShowTitle) View.VISIBLE else View.GONE
                    text = titleText
                }

                mMessageTV = holder.getView<TextView>(R.id.msg_tv)
                mTipsTV = holder.getView<TextView>(R.id.mTipsTV)
                if (isShowTips){
                    mTipsTV?.visibility = View.VISIBLE
                }else{
                    mTipsTV?.visibility = View.GONE
                }
                holder.getView<TextView>(R.id.msg_tv).apply {
                    visibility = if (messageText == "") View.GONE else View.VISIBLE
                    text = messageText
                }

                holder.getView<Button>(R.id.neg_btn).apply {
                    visibility = if (isShowNegBtn) View.VISIBLE else View.GONE
                    text = negativeButtonText
                    setTextColor(negativeButtonColor)
                    setOnClickListener {
                        negativeButtonClickListener?.onClick(it)
                        dialog.dismiss()
                    }
                }

                holder.getView<Button>(R.id.pos_btn).apply {
                    visibility = if (isShowPosBtn) View.VISIBLE else View.GONE
                    text = positiveButtonText
                    setTextColor(positiveButtonColor)
                    setOnClickListener {
                        positiveButtonClickListener?.onClick(it)
                        dialog.dismiss()
                    }
                }

                holder.getView<LinearLayout>(R.id.btn_ll).apply {
                    visibility = if (isShowPosBtn2) View.GONE else View.VISIBLE
                }

            }
        }
    }

    override fun layoutRes(): Int = R.layout.layout_message_dialog

    override fun layoutView(): View? = null

    /**
     * Title Text(Support Rich text)
     */
    fun setTitle(title: CharSequence): MsgDialog {
        isShowTitle = true
        titleText = title
        return this
    }

    /**
     * Message Text(Support Rich text)
     */
    fun setMessage(msg: CharSequence): MsgDialog {
        isShowTips = false
        messageText = msg
        return this
    }

    /**
     * Message Text(Support Rich text)
     */
    fun setMessageHasTips(msg: CharSequence): MsgDialog {
        messageText = msg
        isShowTips = true
        return this
    }

    fun setMessageTV(msg:String){
        mMessageTV?.setText(msg)
    }


    /**
     * Left Button
     */
    @JvmOverloads
    fun setNegativeButton(text: CharSequence,
                          listener: View.OnClickListener? = null,
                          @ColorInt color: Int = negativeButtonColor): MsgDialog {
        isShowNegBtn = true
        negativeButtonText = text
        negativeButtonClickListener = listener
        negativeButtonColor = color
        return this
    }

    /**
     * 确定，这是我今天的体重
     */
    @JvmOverloads
    fun setNegativeButton2(text: CharSequence,
                          listener: View.OnClickListener? = null,
                          @ColorInt color: Int = negativeButtonColor2): MsgDialog {
        isShowNegBtn2 = true
        negativeButtonText2 = text
        negativeButtonClickListener2 = listener
        negativeButtonColor2 = color
        return this
    }

    /**
     * Right Button
     */
    @JvmOverloads
    fun setPositiveButton(text: CharSequence,
                          listener: View.OnClickListener? = null,
                          @ColorInt color: Int = positiveButtonColor): MsgDialog {
        isShowPosBtn = true
        positiveButtonText = text
        positiveButtonClickListener = listener
        positiveButtonColor = color
        return this
    }

    /**
     * 不确定,需要重新记录
     */
    @JvmOverloads
    fun setPositiveButton2(text: CharSequence,
                          listener: View.OnClickListener? = null,
                          @ColorInt color: Int = positiveButtonColor): MsgDialog {
        isShowPosBtn2 = true
        positiveButtonText2 = text
        positiveButtonClickListener2 = listener
        positiveButtonColor2 = color
        return this
    }

    companion object {
        fun init(fragmentManager: FragmentManager): MsgDialog {
            val dialog = MsgDialog()
            dialog.setFragmentManager(fragmentManager)
            dialog.setBackgroundDrawableRes(R.drawable.shape_dialog_bg)
            dialog.setWidthScale(0.9f)
            return dialog
        }
    }

}
