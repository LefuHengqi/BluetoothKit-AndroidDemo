package com.lefu.ppblutoothkit.view

import android.util.SparseArray
import android.view.View
import android.widget.TextView

/**
 *    author : whs
 *    e-mail : haisilen@163.com
 *    date   : 2021/9/4 8:54
 *    desc   :对话框的holder
 */
class ViewHolderComnonDialogWhs private constructor(private val convertView: View) {
    private val views: SparseArray<View> = SparseArray()

    fun <T : View> getView(viewId: Int): T {
        var view: View? = views.get(viewId)
        if (view == null) {
            view = convertView.findViewById(viewId)
            views.put(viewId, view)
        }
        return view as T
    }

    companion object {

        fun create(view: View): ViewHolderComnonDialogWhs {
            return ViewHolderComnonDialogWhs(view)
        }
    }
}

fun ViewHolderComnonDialogWhs.setText(viewId: Int, textId: Int) {
    val textView = getView<TextView>(viewId)
    textView.setText(textId)
}

fun ViewHolderComnonDialogWhs.setText(viewId: Int, text: CharSequence) {
    val textView = getView<TextView>(viewId)
    textView.text = text
}

fun ViewHolderComnonDialogWhs.setTextColor(viewId: Int, colorId: Int) {
    val textView = getView<TextView>(viewId)
    textView.setTextColor(colorId)
}

fun ViewHolderComnonDialogWhs.setOnClickListener(viewId: Int, clickListener: View.OnClickListener?) {
    val view = getView<View>(viewId)
    view.setOnClickListener(clickListener)
}

fun ViewHolderComnonDialogWhs.setBackgroundResource(viewId: Int, resId: Int) {
    val view = getView<View>(viewId)
    view.setBackgroundResource(resId)
}

fun ViewHolderComnonDialogWhs.setBackgroundColor(viewId: Int, colorId: Int) {
    val view = getView<View>(viewId)
    view.setBackgroundColor(colorId)
}
