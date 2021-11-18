package banana.duo.fincon

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.annotation.RequiresApi
import androidx.room.Room
import banana.duo.fincon.db.CategoryDBContainer
import banana.duo.fincon.db.RecordDBContainer
import banana.duo.fincon.models.record.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.time.LocalDate
import java.util.*
import android.content.SharedPreferences
import banana.duo.fincon.utils.fullCategoryDB


class MainActivity : AppCompatActivity() {
    private lateinit var prefs: SharedPreferences

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefs = getSharedPreferences("FinCon", MODE_PRIVATE);
        setContentView(R.layout.activity_main)
        val recordDB = Room.databaseBuilder(
            applicationContext,
            RecordDatabase::class.java, "record_database"
        ).build()
        RecordDBContainer.recordDao = recordDB.recordDao()
        val categoryDB = Room.databaseBuilder(applicationContext,
                                CategoryDatabase::class.java, "category_database").build()
        CategoryDBContainer.categoryDao = categoryDB.categoryDao()

    }


    override fun onResume() {
        super.onResume()
        if (prefs.getBoolean("firstrun", true)) {
            fullCategoryDB()
            prefs.edit().putBoolean("firstrun", false).apply()
        }
    }


    fun onRecordsList(view: View) {
        startActivity(Intent(this, ReportActivity::class.java))
    }

    fun onCreateRecord(view: View) {
        startActivity(Intent(this, CreateRecordActivity::class.java))
    }

}