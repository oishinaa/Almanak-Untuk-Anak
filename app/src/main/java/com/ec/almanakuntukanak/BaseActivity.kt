package com.ec.almanakuntukanak

import android.app.DatePickerDialog
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import java.util.*

open class BaseActivity : AppCompatActivity() {
    fun showDatePickerDialog(onDateSetListener: DatePickerDialog.OnDateSetListener) {
        val c = Calendar.getInstance()
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