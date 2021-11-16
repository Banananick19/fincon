package banana.duo.fincon.models.record

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName="Categories")
data class Category (
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var name: String,
    var color: String,
    var imagePath: String,
    var isExpence: Boolean,
    var isIncome: Boolean
) {
    override fun toString(): String {
        return this.name
    }
}