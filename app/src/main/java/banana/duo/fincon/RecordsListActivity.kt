package banana.duo.fincon

import android.annotation.SuppressLint
import android.app.ActionBar
import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.widget.LinearLayout
import android.widget.TextView
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import banana.duo.fincon.db.RecordDBContainer
import banana.duo.fincon.utils.dateToString
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class RecordsListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_records_list)
        showRecords(findViewById(R.id.RecordsListLayout), 10)
    }

    @SuppressLint("SetTextI18n")
    private fun showRecords(layout: LinearLayout?, count: Int) {
        val context: Context = this
        runBlocking {
            launch(Dispatchers.IO) {
                for (record in RecordDBContainer.recordDao.getRecords(count)) {
                    val textView = TextView(context)
                    textView.setText("${dateToString(record.date)}  ${getResources().getString(R.string.valueOfRecordName)}: ${record.value} ${record.category}")
                    layout?.addView(textView)
                }
            }
        }

    }
}