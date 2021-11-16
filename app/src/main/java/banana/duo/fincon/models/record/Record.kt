package banana.duo.fincon.models.record

import androidx.room.*
import java.time.LocalDate
import java.util.*

@Entity(tableName="Records")
@TypeConverters(DateConverter::class)
data class Record (

    @PrimaryKey(autoGenerate = true)
    var id: Int,

    var category: Category,

    var value: Int,

    var date: Date
)