package com.ec.almanakuntukanak.controller.peserta

import android.os.Bundle
import com.ec.almanakuntukanak.BaseActivity
import com.ec.almanakuntukanak.R

class PesertaFormActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_peserta_form)

        val actionBar = supportActionBar
        actionBar!!.title = "Tambah Data Peserta"
        actionBar.setDisplayHomeAsUpEnabled(true)
    }
}