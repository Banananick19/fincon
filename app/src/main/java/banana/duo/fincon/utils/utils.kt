package banana.duo.fincon.utils

import banana.duo.fincon.db.CategoryDBContainer
import banana.duo.fincon.models.record.Category
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.*

fun dateToString(date: Date): String {
    return "${date.date}.${date.month}.${date.year}"
}


fun fullCategoryDB() {
    runBlocking {
        launch(Dispatchers.IO) {
            with(CategoryDBContainer.categoryDao) {
                insertCategory(Category(0,"Транспорт"))
                insertCategory(Category(0, "Магазины"))
            }
        }
    }
}