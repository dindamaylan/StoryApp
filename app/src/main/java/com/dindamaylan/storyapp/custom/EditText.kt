package com.dindamaylan.storyapp.custom

import android.content.Context
import android.text.InputType
import android.util.AttributeSet
import android.util.Patterns
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.widget.doAfterTextChanged
import com.dindamaylan.storyapp.R
import com.google.android.material.textfield.TextInputLayout

class EditText: AppCompatEditText {
    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        doAfterTextChanged { inputText ->
            when (inputType) {
                (InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS or InputType.TYPE_CLASS_TEXT) -> {
                    val inputEmail = inputText.toString()
                    validationEmail(inputEmail)
                }
                (InputType.TYPE_TEXT_VARIATION_PASSWORD or InputType.TYPE_CLASS_TEXT) -> {
                    val inputPassword = inputText.toString()
                    validationPassword(inputPassword)
                }
            }
        }
    }

    private fun validationEmail(inputEmail: String) {
        val myEditTextEmailLayout = getMyEditTextLayout()
        if (inputEmail.isEmpty()) {
            myEditTextEmailLayout?.let {
                it.apply {
                    error = context.getString(R.string.validation_email_null)
                    isErrorEnabled = true
                }
            }
        } else if (inputEmail.isInvalidEmail()) {
            myEditTextEmailLayout?.let {
                it.apply {
                    error = context.getString(R.string.validation_email_invalid)
                    isErrorEnabled = true
                }
            }
        } else {
            myEditTextEmailLayout?.let {
                it.apply {
                    error = null
                    isErrorEnabled = false
                }
            }
        }
    }

    private fun validationPassword(inputPassword: String) {
        val myEditTextPasswordLayout = getMyEditTextLayout()
        if (inputPassword.isEmpty()){
            myEditTextPasswordLayout?.let {
                it.apply {
                    error = context.getString(R.string.validation_password_length)
                    isErrorEnabled = true
                }
            }
        } else if (inputPassword.isNotEmpty() && inputPassword.length<6){
            myEditTextPasswordLayout?.let {
                it.apply {
                    error = context.getString(R.string.validation_password_null)
                    isErrorEnabled = true
                }
            }
        }else {
            myEditTextPasswordLayout?.let {
                it.apply {
                    error = null
                    isErrorEnabled = false
                }
            }
        }
    }

    private fun getMyEditTextLayout(): TextInputLayout? {
        var parentLayout = parent
        while (parentLayout is View) {
            if (parentLayout is TextInputLayout) {
                return parentLayout
            }
            parentLayout = parent.parent
        }
        return null
    }

    private fun String.isInvalidEmail() = this.isNotEmpty() && !Patterns.EMAIL_ADDRESS.matcher(this).matches()
}