package com.sherifnasser.themarketmanager

import android.app.Activity
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.google.android.material.textfield.TextInputLayout

fun getImm(v:View)=v.context.getSystemService(Activity.INPUT_METHOD_SERVICE)as InputMethodManager

fun showKeyboard(v:View){
    v.post{
        v.requestFocus()
        getImm(v).showSoftInput(v,InputMethodManager.SHOW_FORCED)
    }
}

fun hideKeyboard(v:View){getImm(v).hideSoftInputFromWindow(v.windowToken,0)}

fun TextInputLayout.clearError()=showError(null,false)

fun TextInputLayout.showError(error:String?,isErrorEnabled:Boolean=true){
    this.isErrorEnabled=isErrorEnabled
    this.error=error
}

