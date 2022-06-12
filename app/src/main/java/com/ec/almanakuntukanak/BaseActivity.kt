package com.ec.almanakuntukanak

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.ec.almanakuntukanak.utils.DateUtils
import java.util.*

open class BaseActivity : AppCompatActivity() {
    fun showTimePickerDialog(onTimeSetListener: TimePickerDialog.OnTimeSetListener) {
        val c = Calendar.getInstance()
        val timePickerDialog = TimePickerDialog(this, onTimeSetListener, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true)
        timePickerDialog.show()
    }

    fun showDatePickerDialog(onDateSetListener: DatePickerDialog.OnDateSetListener, date: Calendar?) {
        val c = date ?: Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(this, onDateSetListener, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH))
        datePickerDialog.show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}