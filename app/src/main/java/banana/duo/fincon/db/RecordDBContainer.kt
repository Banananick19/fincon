package banana.duo.fincon.db

import banana.duo.fincon.models.record.RecordDao

class RecordDBContainer {
    companion object {
        lateinit var recordDao: RecordDao
    }
}