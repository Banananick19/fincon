package banana.duo.fincon

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import banana.duo.fincon.db.RecordDBContainer
import banana.duo.fincon.models.record.Category
import banana.duo.fincon.models.record.Record
import banana.duo.fincon.report.Report
import banana.duo.fincon.report.ReportMaker
import banana.duo.fincon.utils.dateToString
import com.google.android.material.datepicker.MaterialDatePicker
import kotlinx.coroutines.*
import kotlinx.datetime.DatePeriod
import java.util.*

class ReportActivity : AppCompatActivity() {
    lateinit var datePeriodStart: Date
    lateinit var datePeriodEnd: Date

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report)
    }

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
        }

    }

    fun renderReport(reportMap: Map<String, Int>) {
        val listView: ListView = findViewById(R.id.listReport)
        val adapter: ArrayAdapter<String> = ArrayAdapter(this, android.R.layout.simple_list_item_1, reportMap.keys.map {it}.zip(reportMap.values, String::plus).toTypedArray())
        listView.adapter = adapter
    }

    suspend fun getReport(): Report? {
        var report: Report? = null
        runBlocking {
            launch(Dispatchers.IO) {
                val records: List<Record> =RecordDBContainer.recordDao.getAllRecords().filter { record -> record.date > datePeriodStart && record.date < datePeriodEnd }
                report = ReportMaker.makeReport(records)
            }
        }
        return report
    }

    fun onShowReport(view: View) {
        val tag = view.tag
        var report: Report? = null
        val coroutineScope = CoroutineScope(Dispatchers.Main)
        coroutineScope.launch {
            withContext(Dispatchers.IO) {
                report = getReport()
            }
            when(tag) {
                "showExpences" -> renderReport(report!!.expences)
                "showIncomes" -> renderReport(report!!.income)
            }
        }
    }
}