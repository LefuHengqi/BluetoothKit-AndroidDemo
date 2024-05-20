package com.lefu.ppblutoothkit.view

import android.content.DialogInterface
import android.os.Parcel
import android.os.Parcelable

/**
 *    author : whs
 *    e-mail : haisilen@163.com
 *    date   : 2021/9/4 8:58
 *    desc   :关闭对话框的监听
 */
abstract class OnDialogDismissListener  : DialogInterface.OnDismissListener, Parcelable {

    override fun describeContents(): Int = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {}

    constructor()

    protected constructor(source: Parcel)

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<OnDialogDismissListener> = object : Parcelable.Creator<OnDialogDismissListener> {
            override fun createFromParcel(source: Parcel): OnDialogDismissListener {
                return object : OnDialogDismissListener(source) {
                    override fun onDismiss(dialog: DialogInterface) {

                    }
                }
            }

            override fun newArray(size: Int): Array<OnDialogDismissListener?> {
                return arrayOfNulls(size)
            }
        }
    }
}
