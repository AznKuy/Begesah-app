package com.example.storyapp.customview.validation

import android.content.Context
import android.graphics.Canvas
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText

class PasswordEditText @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatEditText(context, attrs) {

    private var validationListener: ValidationListener? = null

    init {
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                validateInput()
            }

            override fun afterTextChanged(s: Editable) {}
        })
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
    }

    fun validateInput() {
        val input = text?.toString().orEmpty()
        val isValid = isPasswordValid(input)
        error = if (isValid) null else "Password minimal 8 karakter"
        validationListener?.onValidationChanged(isValid)
    }

    private fun isPasswordValid(password: String): Boolean {
        return password.length >= 8
    }

    fun isInputValid(): Boolean {
        val input = text?.toString().orEmpty()
        return isPasswordValid(input)
    }

    fun setValidationListener(listener: ValidationListener) {
        this.validationListener = listener
    }

    fun interface ValidationListener {
        fun onValidationChanged(isValid: Boolean)
    }
}