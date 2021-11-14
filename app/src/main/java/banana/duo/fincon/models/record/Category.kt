package banana.duo.fincon.models.record

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName="Categories")
data class Category (
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var name: String
) {
    override fun toString(): String {
        return this.name
    }
}