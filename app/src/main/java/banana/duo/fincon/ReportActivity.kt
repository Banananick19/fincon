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
import android.annotation.SuppressLint
import android.view.Gravity
import android.widget.*
import androidx.core.content.res.ResourcesCompat
import banana.duo.fincon.db.CategoryDBContainer
import banana.duo.fincon.utils.getResId

import lecho.lib.hellocharts.util.ChartUtils

import lecho.lib.hellocharts.model.SliceValue
import lecho.lib.hellocharts.view.PieChartView
import android.view.ViewGroup
import org.w3c.dom.Text


class ReportActivity : AppCompatActivity() {
    lateinit var datePeriodStart: Date
    lateinit var datePeriodEnd: Date
    lateinit var categories: List<Category>
    lateinit var expencesChartView: PieChartView
    lateinit var incomesChartView: PieChartView
    lateinit var incomesCountTextView: TextView
    lateinit var expencesCountTextView: TextView
    lateinit var layoutCategories: LinearLayout
    lateinit var dateTextView: TextView
    var dateSelected = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report)
        initCategories()
        expencesChartView = findViewById(R.id.expencesChart)
        incomesChartView = findViewById(R.id.incomesChart)
        incomesCountTextView = findViewById(R.id.incomesCount)
        expencesCountTextView = findViewById(R.id.expencesCount)
        dateTextView = findViewById(R.id.dateView)
        layoutCategories = findViewById(R.id.categoriesListLayout)
    }

    @SuppressLint("SetTextI18n")
    fun onDateRangePick(view: View) {
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
            dateSelected = true
            dateTextView.text = "${dateToString(datePeriodStart)}-${dateToString(datePeriodEnd)}"
        }
    }

    fun initCategories() {
        runBlocking {
            launch(Dispatchers.IO) {
                categories = CategoryDBContainer.categoryDao.getAllCategories()
            }
        }
    }

    fun onIncomesShow(view: View) {
        if (dateSelected) renderReport(getReport(), "incomes")
    }
    fun onExpencesShow(view: View) {
        if (dateSelected) renderReport(getReport(), "expences")
    }

    fun showCategories(report: Report, type: String) {
        val categoriesForShow: List<Category>
        when (type) {
            "incomes" -> categoriesForShow = categories.filter {category:Category -> report.income.keys.contains(category.name) }
            "expences" -> categoriesForShow = categories.filter {category:Category -> report.expences.keys.contains(category.name) }
            else -> categoriesForShow = listOf()
        }
        for (category in categoriesForShow) {
            val container = LinearLayout(this)
            container.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            val imageView = ImageView(this)
            imageView.setImageResource(getResId(category.imagePath, R.drawable::class.java))
            imageView.requestLayout()
            val params = LinearLayout.LayoutParams(
                60,
                60
            )
            params.setMargins(0, 0, 20, 0);
            imageView.layoutParams = params
            val categoryNameTextView = TextView(this)
            categoryNameTextView.text = category.name
            categoryNameTextView.textSize = 24F
            categoryNameTextView.setTextColor(Color.parseColor(category.color))
            val categoryCountTextView = TextView(this)
            categoryCountTextView.text = when (type) {
                "incomes" -> report.income[category.name].toString()
                "expences" -> report.expences[category.name].toString()
                else -> ""
            }
            categoryCountTextView.gravity = Gravity.END
            categoryCountTextView.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            categoryCountTextView.textSize = 20F
            container.addView(imageView)
            container.addView(categoryNameTextView)
            container.addView(categoryCountTextView)
            layoutCategories.addView(container, 0)
        }
    }

    @SuppressLint("SetTextI18n")
    fun renderReport(report: Report, type: String) {
        when (type) {
            "expences" -> {
                clearReport()
                reportExpencesPrepareRender()
                val expencesValues: MutableList<SliceValue> = ArrayList()
                for (key in report.expences.keys) {
                    val sliceValue = SliceValue(report.expences[key]!!.toFloat(), Color.parseColor((categories.find {category -> category.name == key })!!.color))
                    expencesValues.add(sliceValue)
                }
                val dataExpences = PieChartData(expencesValues)
                expencesChartView.pieChartData = dataExpences
                expencesCountTextView.text = report.expences.values.fold(0) {count: Int, elem: Int -> count + elem }.toString()
                showCategories(report, "expences")
            }
            "incomes" -> {
                clearReport()
                reportIncomesPrepareRender()
                val incomesValues: MutableList<SliceValue> = ArrayList()
                for (key in report.income.keys) {
                    val sliceValue = SliceValue(report.income[key]!!.toFloat(), Color.parseColor((categories.find {category -> category.name == key })!!.color))
                    incomesValues.add(sliceValue)
                }
                val dataIncomes = PieChartData(incomesValues)
                incomesChartView.pieChartData = dataIncomes
                incomesCountTextView.text = report.income.values.fold(0) {count: Int, elem: Int -> count + elem }.toString()
                showCategories(report, "incomes")
            }
        }
    }

    fun reportExpencesPrepareRender() {
        incomesChartView.alpha = 0.3F
        expencesChartView.alpha = 1F
    }

    fun reportIncomesPrepareRender() {
        expencesChartView.alpha = 0.3F
        incomesChartView.alpha = 1F
    }

    fun clearReport() {
        layoutCategories.removeAllViews()
        val view: View = View(this)
        view.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        layoutCategories.addView(view)
        incomesChartView.pieChartData = null
        expencesChartView.pieChartData = null
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