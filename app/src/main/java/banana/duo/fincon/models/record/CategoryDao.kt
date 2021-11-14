package banana.duo.fincon.models.Category

import androidx.room.*
import banana.duo.fincon.models.record.Category

@Dao
interface CategoryDao {
    @Insert
    suspend fun insertCategory(category: Category)

    @Query("SELECT * FROM Categories")
    fun getAllCategories(): List<Category>

    @Query("SELECT * FROM Categories LIMIT :count")
    fun getCategories(count: Int): List<Category>

    @Update
    suspend fun updateCategory(Category: Category)

    @Delete
    suspend fun deleteCategory(Category: Category)

}