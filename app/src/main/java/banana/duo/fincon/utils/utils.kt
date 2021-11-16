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
                insertCategory(Category(0,"Транспорт", "#00A1FF", "drive"))
                insertCategory(Category(0, "Еда", "#FFF731", "food"))
                insertCategory(Category(0, "Одежда", "#C34FE4", "clothes"))
                insertCategory(Category(0, "Отдых и развлечения", "#FF7A28", "recreation"))
                insertCategory(Category(0, "Прочее", "#FF66F0", "other"))
                insertCategory(Category(0, "Прочее(Доход)", "#FFF731", "other_income"))
                insertCategory(Category(0, "Зарплата", "#00A1FF", "salary"))
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