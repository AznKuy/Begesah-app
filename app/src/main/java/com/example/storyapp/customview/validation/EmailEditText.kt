package com.example.storyapp.customview.validation

import android.content.Context
import android.graphics.Canvas
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatEditText

class EmailEditText @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatEditText(context, attrs) {

    private var validationListener: ValidationListener? = null


    init {
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                validateInput()
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        textAlignment = View.TEXT_ALIGNMENT_VIEW_START
    }

    private fun validateInput() {
        val input = text?.toString().orEmpty()
        val isValid = isEmailValid(input)
        error = if (isValid) null else "Email tidak valid"
        validationListener?.onValidationChanged(isValid)
    }

    // validasi email
    private fun isEmailValid(email: String): Boolean {
        // regex untuk validasi email
        val emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
        return email.matches(emailRegex.toRegex())
    }

    fun isInputValid(): Boolean {
        val input = text?.toString().orEmpty()
        return isEmailValid(input)
    }

    fun setValidationListener(listener: ValidationListener) {
        this.validationListener = listener
    }

    fun interface ValidationListener {
        fun onValidationChanged(isValid: Boolean)
    }

}

