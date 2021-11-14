package banana.duo.fincon.models.record

import androidx.room.Database
import androidx.room.RoomDatabase
import banana.duo.fincon.models.Category.CategoryDao

@Database(entities = [Category::class], version = 1, exportSchema = true)
abstract class CategoryDatabase: RoomDatabase() {
    abstract fun categoryDao(): CategoryDao
}