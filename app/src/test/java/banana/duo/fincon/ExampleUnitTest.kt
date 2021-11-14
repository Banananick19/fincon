package banana.duo.fincon

import android.util.Log
import banana.duo.fincon.db.RecordDBContainer
import banana.duo.fincon.models.record.Record
import banana.duo.fincon.report.Report
import banana.duo.fincon.report.ReportMaker
import org.junit.Test

import org.junit.Assert.*
import java.util.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        val recordsList: List<Record> = listOf(Record(0, "Транcпорт", -100, "10", Date(2021, 10, 20)))
        assertEquals(ReportMaker.makeReport(recordsList).expences, mapOf(Pair("Транcпорт", -100)))
    }
}