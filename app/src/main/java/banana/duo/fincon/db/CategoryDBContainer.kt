package banana.duo.fincon.db

import banana.duo.fincon.models.Category.CategoryDao
import banana.duo.fincon.models.record.RecordDao

class CategoryDBContainer {
    companion object {
        lateinit var categoryDao: CategoryDao
    }
}