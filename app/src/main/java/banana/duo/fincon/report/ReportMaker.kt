package banana.duo.fincon.report

import banana.duo.fincon.models.record.Category
import banana.duo.fincon.models.record.Record
import java.util.*
import kotlin.collections.HashMap

class ReportMaker {
    companion object {
        fun makeReport(records: List<Record>): Report {
            var expences: Map<String, Int> = HashMap()
            var income: Map<String, Int> = HashMap()
            for (record in records) {
                when {
                    record.value < 0 -> expences = expences.plus(Pair(record.category, record.value + (expences.get(record.category) ?: 0)))
                    record.value > 0 -> income = income.plus(Pair(record.category, record.value + (income.get(record.category) ?: 0)))
                }
            }
            return Report(expences, income)
        }
    }
}