package com.example.pdfreader.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.ViewGroup
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.pdfreader.R

class ExcelActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_excel)

        // Tạo một TableLayout để chứa dữ liệu giống như Excel
        val tableLayout = TableLayout(this)
        tableLayout.layoutParams = TableLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )

        // Tạo tiêu đề (Header) cho bảng Excel
        val headerRow = TableRow(this)
        headerRow.setBackgroundColor(ContextCompat.getColor(this, R.color.yellow))

        val headerText1 = createTextView("A")
        val headerText2 = createTextView("B")
        val headerText3 = createTextView("C")
        val headerText4 = createTextView("D")
        val headerText5 = createTextView("E")
        val headerText6 = createTextView("F")
        val headerText7 = createTextView("G")

        headerRow.addView(headerText1)
        headerRow.addView(headerText2)
        headerRow.addView(headerText3)
        headerRow.addView(headerText4)
        headerRow.addView(headerText5)
        headerRow.addView(headerText6)
        headerRow.addView(headerText7)

        tableLayout.addView(headerRow)

        // Tạo dữ liệu cho bảng Excel
        for (i in 1..10) {
            val tableRow = TableRow(this)

            val dataText1 = createTextView("Data $i,1")
            val dataText2 = createTextView("Data $i,2")
            val dataText3 = createTextView("Data $i,3")
            val dataText4 = createTextView("Data $i,4")
            val dataText5 = createTextView("Data $i,5")
            val dataText6 = createTextView("Data $i,6")
            val dataText7 = createTextView("Data $i,7")

            tableRow.addView(dataText1)
            tableRow.addView(dataText2)
            tableRow.addView(dataText3)
            tableRow.addView(dataText4)
            tableRow.addView(dataText5)
            tableRow.addView(dataText6)
            tableRow.addView(dataText7)

            tableLayout.addView(tableRow)
        }

        val scrollView = TableLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        scrollView.weight = 1f
        tableLayout.layoutParams = scrollView
        tableLayout.isStretchAllColumns = true

        val scrollableLayout = TableLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )

        val scrollable = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )

        val mainTable = findViewById<TableLayout>(R.id.main_table)
        mainTable.addView(tableLayout)
    }

    private fun createTextView(text: String): TextView {
        val textView = TextView(this)
        textView.text = text
        textView.setPadding(8, 8, 8, 8)
        return textView
    }
}
