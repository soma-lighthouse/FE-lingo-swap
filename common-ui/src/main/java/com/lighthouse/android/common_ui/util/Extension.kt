package com.lighthouse.android.common_ui.util

import android.app.Activity
import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.content.Intent
import android.content.res.Resources
import android.os.Build
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Patterns
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.core.os.bundleOf
import java.io.Serializable
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

fun View.setVisible() {
    this.visibility = View.VISIBLE
}

fun View.setInvisible() {
    this.visibility = View.INVISIBLE
}

fun View.setGone() {
    this.visibility = View.GONE
}

fun Context?.toast(text: CharSequence, duration: Int = Toast.LENGTH_LONG) =
    this?.let { Toast.makeText(it, text, duration).show() }

fun calSize(size: Float?): Int {
    val density = Resources.getSystem().displayMetrics.density

    return (size?.times(density) ?: 0).toInt()
}

fun Context.showKeyboard(view: View) {
    val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.showSoftInput(view, 0)
}

fun Context.closeKeyboard(view: View) {
    val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}

inline fun <reified T : Activity> Context.buildIntent(
    vararg argument: Pair<String, Any?>,
) = Intent(this, T::class.java).apply {
    putExtras(bundleOf(*argument))
}

inline fun <reified T : Activity> Context.navigateActivity(
    vararg argument: Pair<String, Any?>,
) {
    startActivity(buildIntent<T>(*argument))
}

fun <T : Serializable> Intent.intentSerializable(key: String, clazz: Class<T>): T? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        this.getSerializableExtra(key, clazz)
    } else {
        this.getSerializableExtra(key) as T?
    }
}

fun String.setColor(color: Int): SpannableString {
    val result = SpannableString(this)

    result.setSpan(
        ForegroundColorSpan(color),
        0,
        this.length,
        SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
    )
    return result
}

fun String?.isValidEmail() = !isNullOrEmpty() && Patterns.EMAIL_ADDRESS.matcher(this).matches()

fun String?.isValidBirthday(): Boolean {
    if (isNullOrEmpty()) {
        return false
    }

    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.KOREA)
    dateFormat.isLenient = false

    return try {
        val date = dateFormat.parse(this)

        val minDate = Calendar.getInstance()
        minDate.add(Calendar.YEAR, -120)
        val maxDate = Calendar.getInstance()
        maxDate.add(Calendar.YEAR, -1)

        date != null && date.after(minDate.time) && date.before(maxDate.time)
    } catch (e: ParseException) {
        // Parsing error, input is not in valid format
        false
    }
}

fun EditText.onCloseKeyBoard(context: Context) {
    this.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
        if (!hasFocus) {
            val inputMethodManager =
                context.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager?
            inputMethodManager!!.hideSoftInputFromWindow(this.windowToken, 0)
        }
    }
}