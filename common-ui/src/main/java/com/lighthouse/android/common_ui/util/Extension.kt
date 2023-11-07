package com.lighthouse.android.common_ui.util

import android.app.Activity
import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.content.Intent
import android.content.res.Resources
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.util.Patterns
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.databinding.BaseObservable
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.viewModelScope
import com.lighthouse.android.common_ui.base.BaseViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.io.Serializable
import java.net.URL
import java.net.URLDecoder
import java.time.LocalDate
import java.time.format.DateTimeFormatter

fun View.setVisible() {
    this.visibility = View.VISIBLE
}

fun View.setInvisible() {
    this.visibility = View.INVISIBLE
}

fun View.setGone() {
    this.visibility = View.GONE
}

fun View.disable() {
    this.isEnabled = false
}

fun View.enable() {
    this.isEnabled = true
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

    val formats = arrayOf(
        "dd/MM/yyyy",
        "dd-MM-yyyy",
        "ddMMyyyy"
    )

    for (format in formats) {
        try {
            val formatter = DateTimeFormatter.ofPattern(format)
            val parsedDate = LocalDate.parse(this, formatter)

            val minDate = LocalDate.now().minusYears(120)
            val maxDate = LocalDate.now().minusYears(1)

            return !parsedDate.isBefore(minDate) && !parsedDate.isAfter(maxDate)
        } catch (e: Exception) {
            // Ignore and try the next format
        }
    }

    // None of the formats matched
    return false
}


fun String?.isValidName(): Boolean {
    if (isNullOrEmpty()) {
        return false
    }
    val regex = "^[\\p{L}\\s'-]+$"
    return this.matches(Regex(regex))
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

fun <T> LiveData<T>.observeOnce(owner: LifecycleOwner, observer: (T) -> Unit) {
    var firstObservation = true

    observe(owner, object : Observer<T> {
        override fun onChanged(value: T) {
            if (firstObservation) {
                firstObservation = false
            } else {
                removeObserver(this)
                observer(value)
            }
        }
    })
}

inline fun <reified T : Fragment> AppCompatActivity.replace(@IdRes frameId: Int) {
    supportFragmentManager.commit {
        replace<T>(frameId)
        setReorderingAllowed(true)
    }
}


inline fun BaseViewModel.onMain(
    crossinline body: suspend CoroutineScope.() -> Unit
) = viewModelScope.launch {
    body(this)
}

inline fun BaseViewModel.onIO(
    crossinline body: suspend CoroutineScope.() -> Unit
) = viewModelScope.launch(io) {
    body(this)
}

inline fun BaseViewModel.onDefault(
    crossinline body: suspend CoroutineScope.() -> Unit
) = viewModelScope.launch(default) {
    body(this)
}

fun disableTabForSeconds(seconds: Long, action: () -> Unit) {
    Handler(Looper.getMainLooper()).postDelayed(action, seconds * 1000)
}

fun BaseObservable.observe(callBack: () -> Unit) {
    this.addOnPropertyChangedCallback(object :
        androidx.databinding.Observable.OnPropertyChangedCallback() {
        override fun onPropertyChanged(sender: androidx.databinding.Observable?, propertyId: Int) {
            callBack()
        }
    })
}

fun String.extractUrlParts(): Pair<String, List<String>> {
    val url = URL(this)
    val baseUrl = "${url.protocol}://${url.host}"
    val query = url.query?.let { "?$it" } ?: ""
    val pathSegments = url.path + query
    Log.d("TESTING PUSH32", pathSegments.splitPath().toString())
    return Pair(baseUrl, pathSegments.splitPath())
}

fun String.splitPath(): List<String> {
    val parts = this.split("/", limit = 3)
    Log.d("TESTING PUSH23", parts.toString())
    return if (parts.size == 3) {
        val firstPart = parts[1]
        val remainingPart = "/" + parts[2]
        listOf(firstPart, remainingPart)
    } else if (parts.size == 2) {
        listOf(parts[1])
    } else {
        listOf()
    }
}

fun String.getParams(): Map<String, String> {
    val paramsMap = mutableMapOf<String, String>()
    this.substringAfter("?")
        .split("&")
        .forEach {
            val (key, value) = it.split("=")
            paramsMap[URLDecoder.decode(key, "UTF-8")] = URLDecoder.decode(value, "UTF-8")
        }

    return paramsMap
}