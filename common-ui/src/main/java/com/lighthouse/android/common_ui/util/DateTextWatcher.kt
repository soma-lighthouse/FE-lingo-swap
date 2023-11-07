package com.lighthouse.android.common_ui.util

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import java.util.Calendar


class DateTextWatcher(private val date: EditText) : TextWatcher {

    private var current = ""
    private val ddmmyyyy = "ddmmyyyy"
    private val cal: Calendar = Calendar.getInstance()

    override fun afterTextChanged(s: Editable?) {
        // Do nothing
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        // Do nothing
    }

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        if (s.toString() != current) {
            var clean = s.toString().replace("[^\\d.]|\\.".toRegex(), "")
            val cleanC: String = current.replace(Regex("[^\\d.]|\\."), "")
            val cl = clean.length
            var sel = cl
            var i = 2
            while (i <= cl && i < 6) {
                sel++
                i += 2
            }
            //Fix for pressing delete next to a forward slash
            if (clean == cleanC) sel--
            if (clean.length < 8) {
                clean += ddmmyyyy.substring(clean.length)
            } else {
                //This part makes sure that when we finish entering numbers
                //the date is correct, fixing it otherwise
                var day = clean.substring(0, 2).toInt()
                var mon = clean.substring(2, 4).toInt()
                var year = clean.substring(4, 8).toInt()
                mon = if (mon < 1) 1 else if (mon > 12) 12 else mon
                cal.set(Calendar.MONTH, mon - 1)
                year = if (year < 1900) 1900 else if (year > 2100) 2100 else year
                cal.set(Calendar.YEAR, year)
                // ^ first set year for the line below to work correctly
                //with leap years - otherwise, date e.g. 29/02/2012
                //would be automatically corrected to 28/02/2012
                day =
                    if (day > cal.getActualMaximum(Calendar.DATE)) cal.getActualMaximum(Calendar.DATE) else day
                clean = String.format("%02d%02d%02d", day, mon, year)
            }
            clean = String.format(
                "%s/%s/%s", clean.substring(0, 2),
                clean.substring(2, 4),
                clean.substring(4, 8)
            )

            sel = if (sel < 0) 0 else sel
            current = clean
            date.setText(current)
            date.setSelection(if (sel < current.length) sel else current.length)
        }
    }
}
