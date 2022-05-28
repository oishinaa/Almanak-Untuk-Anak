package com.ec.almanakuntukanak.controller.panduan

import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.Bundle
import android.view.View
import android.widget.Button
import com.ec.almanakuntukanak.BaseActivity
import com.ec.almanakuntukanak.R
import com.github.chrisbanes.photoview.PhotoView

@Suppress("DEPRECATION")
class PdfActivity : BaseActivity() {
    private lateinit var img: PhotoView
    private lateinit var btnPrev: Button
    private lateinit var btnNext: Button
    private lateinit var renderer: PdfRenderer
    private var curPage = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pdf)

        val actionBar = supportActionBar
        actionBar!!.title = "Panduan Makan Anak"
        actionBar.setDisplayHomeAsUpEnabled(true)

        img = findViewById(R.id.img)
        btnPrev = findViewById(R.id.btnPrev)
        btnNext = findViewById(R.id.btnNext)
        renderer = PdfRenderer(assets.openFd("sample2.pdf").parcelFileDescriptor)

        btnPrev.setOnClickListener {
            if (curPage == 0) return@setOnClickListener
            curPage--
            loadImg()
        }
        btnNext.setOnClickListener {
            if (curPage+1 == renderer.pageCount) return@setOnClickListener
            curPage++
            loadImg()
        }

        loadImg()
    }

    private fun loadImg() {
        if(curPage == 0) btnPrev.visibility = View.INVISIBLE
        else btnPrev.visibility = View.VISIBLE

        if(curPage+1 == renderer.pageCount) btnNext.visibility = View.INVISIBLE
        else btnNext.visibility = View.VISIBLE

        val page = renderer.openPage(curPage)
        val bitmap = Bitmap.createBitmap(page.width, page.height, Bitmap.Config.ARGB_4444)

        page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
        page.close()

        img.setImageBitmap(bitmap)
        img.invalidate()
    }
}