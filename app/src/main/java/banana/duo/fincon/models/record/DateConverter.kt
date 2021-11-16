package banana.duo.fincon.models.record

import androidx.room.TypeConverter
import banana.duo.fincon.db.CategoryDBContainer
import java.util.*

class DateConverter {
    companion object {
    @TypeConverter
    @JvmStatic
    fun fromDate(date: Date): Long {
        return date.time
    }

    @TypeConverter
    @JvmStatic
    fun toDate(date: Long): Date {
        return Date(date)
    }

        @TypeConverter
        @JvmStatic
        fun fromCategory(category: Category): String {
            return category.name
        }

        @TypeConverter
        @JvmStatic
        fun toCategory(categoryName: String): Category {
            return CategoryDBContainer.categoryDao.getAllCategories().find {category -> category.name == categoryName }!!
        }
        }
}