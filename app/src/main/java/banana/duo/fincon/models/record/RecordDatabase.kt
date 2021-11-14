package banana.duo.fincon.models.record

import androidx.room.Database
import androidx.room.RoomDatabase


@Database(entities = [Record::class], version = 1, exportSchema = true)
abstract class RecordDatabase: RoomDatabase() {
    abstract fun recordDao(): RecordDao
}