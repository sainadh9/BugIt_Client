package com.bugit.bugit.utils

import android.content.res.ColorStateList
import android.graphics.Color
import android.widget.Button

object ExtensionFunctions {

    fun Button.disable() {
        this.isEnabled = false
        this.backgroundTintList = ColorStateList.valueOf(Color.GRAY)
    }

    fun Button.enable() {
        this.isEnabled = true
        this.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#57259F"))
    }


}