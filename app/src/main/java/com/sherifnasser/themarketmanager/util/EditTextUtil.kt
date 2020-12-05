package com.sherifnasser.themarketmanager.util

import com.google.android.material.textfield.TextInputLayout

fun TextInputLayout.clearError()=showError(null,false)

fun TextInputLayout.showError(error:String?, isErrorEnabled:Boolean=true){
    this.isErrorEnabled=isErrorEnabled
    this.error=error
}