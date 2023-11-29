package com.lefu.ppblutoothkit.view

import android.os.Parcel
import android.os.Parcelable


/**
 *    author : whs
 *    e-mail : haisilen@163.com
 *    date   : 2021/9/4 8:53
 *    desc   :自定义ui的的对话框的事件点击监听
 */
abstract class ViewHandlerListener: Parcelable {
    abstract fun convertView(holder: ViewHolderComnonDialogWhs, dialog: BaseLeFuWhsDialog<*>)

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
    }

    constructor()

    protected constructor(source: Parcel)

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<ViewHandlerListener> = object : Parcelable.Creator<ViewHandlerListener> {
            override fun createFromParcel(source: Parcel): ViewHandlerListener {
                return object : ViewHandlerListener(source) {
                    override fun convertView(holder: ViewHolderComnonDialogWhs, dialog: BaseLeFuWhsDialog<*>) {

                    }
                }
            }

            override fun newArray(size: Int): Array<ViewHandlerListener?> {
                return arrayOfNulls(size)
            }
        }
    }
}
