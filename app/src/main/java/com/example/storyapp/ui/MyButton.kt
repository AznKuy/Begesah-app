package com.example.storyapp.ui

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.ProgressBar
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import com.example.storyapp.R

@Suppress("DEPRECATION")
class MyButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {

    @SuppressLint("SetTextI18n")
    private val button: AppCompatButton = AppCompatButton(context).apply {
        text = "Submit"
        setTextColor(ContextCompat.getColor(context, android.R.color.background_light))
        background = ContextCompat.getDrawable(context, R.drawable.bg_button)
        layoutParams = LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.WRAP_CONTENT
        )
    }

    private val progressBar: ProgressBar
    private var isLoading: Boolean = false
    private var isLockedValid: Boolean = false

    private var isFormValid: (() -> Boolean)? = null

    init {
        addView(button)

        progressBar = ProgressBar(context).apply {
            visibility = View.GONE
            isIndeterminate = true
            indeterminateDrawable.setColorFilter(
                ContextCompat.getColor(context, android.R.color.white),
                android.graphics.PorterDuff.Mode.SRC_IN
            )
            val size = 60
            layoutParams = LayoutParams(size, size).apply {
                gravity = Gravity.CENTER
            }
        }
        addView(progressBar)
    }

    @SuppressLint("SetTextI18n")
    fun setLoading(loading: Boolean) {
        isLoading = loading
        if (loading) {
            progressBar.visibility = View.VISIBLE
            button.text = ""
            button.isEnabled = false
            button.background = ContextCompat.getDrawable(context, R.drawable.bg_button_disable)
        } else {
            progressBar.visibility = View.GONE
            button.isEnabled = isLockedValid
            Log.d("MyButton", "Loading finished, button enabled: ${button.isEnabled}")
        }
    }




    fun setButtonState(enabled: Boolean) {
        if (enabled) isLockedValid = true
        button.isEnabled = enabled || isLockedValid
        button.text = if (enabled || isLockedValid) "Submit" else "Isi Data Dulu"
        button.background = ContextCompat.getDrawable(
            context,
            if (enabled || isLockedValid) R.drawable.bg_button else R.drawable.bg_button_disable
        )
        Log.d("MyButton", "setButtonState: $enabled, isLockedValid: $isLockedValid")
    }


    fun setValidationCallback(callback: () -> Boolean) {
        isFormValid = {
            val valid = callback.invoke()
            Log.d("MyButton", "Validation check result: $valid")
            valid
        }
    }

    override fun setOnClickListener(listener: OnClickListener?) {
        button.setOnClickListener {
            Log.d("MyButton", "Button clicked, isLockedValid: $isLockedValid")
            val valid = isFormValid?.invoke() ?: false
            Log.d("MyButton", "Validation result at click: $valid")
            if (isLockedValid || valid) {
                listener?.onClick(it)
            } else {
                Log.d("MyButton", "Form not valid after click")
            }
        }
    }

}
