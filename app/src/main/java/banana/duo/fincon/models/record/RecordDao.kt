package banana.duo.fincon.models.record

import androidx.room.*
import java.util.*

@Dao
interface RecordDao {

    @Insert
    suspend fun insertRecord(record: Record)

    @Query("SELECT * FROM Records")
    fun getAllRecords(): List<Record>

    @Query("SELECT * FROM Records LIMIT :count")
    fun getRecords(count: Int): List<Record>

    @Query("SELECT * FROM Records WHERE date > :dateStart AND date < :dateEnd")
    fun getRecords(dateStart: Date, dateEnd: Date) {

    }


    @Update
    suspend fun updateRecord(record: Record)

    @Delete
    suspend fun deleteRecord(record: Record)

}