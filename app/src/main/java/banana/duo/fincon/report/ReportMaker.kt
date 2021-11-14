package banana.duo.fincon.report

import banana.duo.fincon.models.record.Category
import banana.duo.fincon.models.record.Record
import java.util.*
import kotlin.collections.HashMap

class ReportMaker {
    companion object {
        fun makeReport(records: List<Record>): Report {
            val expences: Map<String, Int> = HashMap()
            val incomes: Map<String, Int> = HashMap()
            for (record in records) {
                when {
                    record.value < 0 -> expences.plus(Pair(record.category, record.value + (expences.get(record.category) ?: 0)))
                    record.value > 0 -> incomes.plus(Pair(record.category, record.value + (incomes.get(record.category) ?: 0)))
                }
            }
            return Report(expences, incomes)
        }
    }
}