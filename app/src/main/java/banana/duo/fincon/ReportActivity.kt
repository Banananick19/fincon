package banana.duo.fincon

import android.R.attr
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import banana.duo.fincon.db.RecordDBContainer
import banana.duo.fincon.models.record.Category
import banana.duo.fincon.models.record.Record
import banana.duo.fincon.report.Report
import banana.duo.fincon.report.ReportMaker
import banana.duo.fincon.utils.dateToString
import com.google.android.material.datepicker.MaterialDatePicker
import kotlinx.coroutines.*
import kotlinx.datetime.DatePeriod
import lecho.lib.hellocharts.model.Line
import java.util.*
import lecho.lib.hellocharts.view.LineChartView

import lecho.lib.hellocharts.model.LineChartData

import lecho.lib.hellocharts.model.PointValue
import lecho.lib.hellocharts.model.PieChartData

import android.R.attr.data
import android.widget.*
import androidx.core.content.res.ResourcesCompat
import banana.duo.fincon.db.CategoryDBContainer
import banana.duo.fincon.utils.getResId

import lecho.lib.hellocharts.util.ChartUtils

import lecho.lib.hellocharts.model.SliceValue
import lecho.lib.hellocharts.view.PieChartView
import android.view.ViewGroup





class ReportActivity : AppCompatActivity() {
    lateinit var datePeriodStart: Date
    lateinit var datePeriodEnd: Date
    lateinit var categories: List<Category>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report)
        initCategories()
        showCategories()
    }

    fun onReportMake(view: View) {
        val dateRangePicker =
            MaterialDatePicker.Builder.dateRangePicker()
                .setTitleText("Select dates")
                .setSelection(
                    androidx.core.util.Pair(
                        MaterialDatePicker.thisMonthInUtcMilliseconds(),
                        MaterialDatePicker.todayInUtcMilliseconds()
                    )
                )
                .build()
        dateRangePicker.show(supportFragmentManager, "tag");
        dateRangePicker.addOnPositiveButtonClickListener {
            datePeriodStart = Date(dateRangePicker.selection!!.first)
            datePeriodEnd = Date(dateRangePicker.selection!!.second)
            renderReport(getReport())
        }
    }

    fun initCategories() {
        runBlocking {
            launch(Dispatchers.IO) {
                categories = CategoryDBContainer.categoryDao.getAllCategories()
            }
        }
    }

    fun showCategories() {
        val layout = findViewById<LinearLayout>(R.id.categoriesListLayout)
        for (category in categories) {
            Log.i("categoryCheck", category.name)
            val container = LinearLayout(this)
            val imageView = ImageView(this)
            imageView.setImageResource(getResId(category.imagePath, R.drawable::class.java))
            imageView.requestLayout()
            val params = LinearLayout.LayoutParams(
                60,
                60
            )
            params.setMargins(0, 0, 20, 0);
            imageView.layoutParams = params
            val textView = TextView(this)
            textView.setText(category.name)
            textView.textSize = 24F
            textView.setTextColor(Color.parseColor(category.color));
            container.addView(imageView)
            container.addView(textView)
            layout.addView(container, 0)
        }
    }

    fun renderReport(report: Report) {
        val expencesValues: MutableList<SliceValue> = ArrayList()
        for (key in report.expences.keys) {
            val sliceValue = SliceValue(report.expences[key]!!.toFloat(), Color.parseColor((categories.find {category -> category.name == key })!!.color))
            expencesValues.add(sliceValue)
        }
        val incomesValues: MutableList<SliceValue> = ArrayList()
        for (key in report.income.keys) {
            val sliceValue = SliceValue(report.income[key]!!.toFloat(), Color.parseColor((categories.find {category -> category.name == key })!!.color))
            incomesValues.add(sliceValue)
        }
        val expencesChartView: PieChartView = findViewById<PieChartView>(R.id.expencesChart)
        val incomesChartView: PieChartView = findViewById<PieChartView>(R.id.incomesChart)
        val dataExpences = PieChartData(expencesValues)
        val dataIncomes = PieChartData(incomesValues)
        expencesChartView.pieChartData = dataExpences
        incomesChartView.pieChartData = dataIncomes
    }

    fun getReport(): Report {
        var report: Report = ReportMaker.makeReport(listOf())
        runBlocking {
            launch(Dispatchers.IO) {
                val records: List<Record> = RecordDBContainer.recordDao.getAllRecords()
                    .filter { record -> record.date > datePeriodStart && record.date < datePeriodEnd }
                report = ReportMaker.makeReport(records)
            }
        }
        return report
    }
}