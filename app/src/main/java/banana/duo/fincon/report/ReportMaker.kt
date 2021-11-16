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
                    record.category.isExpence -> expences = expences.plus(Pair(record.category.toString(), record.value + (expences.get(record.category.toString()) ?: 0)))
                    record.category.isIncome -> income = income.plus(Pair(record.category.toString(), record.value + (income.get(record.category.toString()) ?: 0)))
                }
            }
            return Report(expences, income)
        }
    }
}