package banana.duo.fincon.utils

import banana.duo.fincon.db.CategoryDBContainer
import banana.duo.fincon.models.record.Category
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.lang.Exception
import java.lang.reflect.Field
import java.util.*

fun dateToString(date: Date): String {
    return "${date.date}.${date.month+1}.${date.year+1900}"
}


fun fullCategoryDB() {
    runBlocking {
        launch(Dispatchers.IO) {
            with(CategoryDBContainer.categoryDao) {
                insertCategory(Category(0, "Прочее", "#FF66F0", "other", true, false))
                insertCategory(Category(0, "Прочее(Доход)", "#FFF731", "other_income", false, true))
                insertCategory(Category(0, "Зарплата", "#00A1FF", "salary", false, true))
                insertCategory(Category(0, "Отдых и развлечения", "#FF7A28", "recreation", true, false))
                insertCategory(Category(0, "Одежда", "#C34FE4", "clothes", true, false))
                insertCategory(Category(0, "Еда", "#FFF731", "food", true, false))
                insertCategory(Category(0,"Транспорт", "#00A1FF", "drive", true, false))

            }
        }
    }
}

fun normalDate(): Date {
    val date = Date()
    return Date(date.date, date.month+1, date.year+1900)
}

fun getResId(resName: String, c: Class<*>): Int {
    return try {
        val idField: Field = c.getDeclaredField(resName)
        idField.getInt(idField)
    } catch (e: Exception) {
        e.printStackTrace()
        -1
    }
}

fun randomColor(): String {
    val zeros = "000000"
    val rnd = Random()
    var s = Integer.toString(rnd.nextInt(0X1000000), 16)
    s = "#${s}"
    s = zeros.substring(s.length-1) + s
    return s
}