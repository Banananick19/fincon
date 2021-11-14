package banana.duo.fincon

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.*
import banana.duo.fincon.db.CategoryDBContainer
import banana.duo.fincon.db.RecordDBContainer
import banana.duo.fincon.models.record.Category
import banana.duo.fincon.models.record.Record
import banana.duo.fincon.utils.dateToString
import com.google.android.material.datepicker.MaterialDatePicker
import kotlinx.coroutines.*
import org.w3c.dom.Text
import java.util.*

class CreateRecordActivity : AppCompatActivity() {

    var dateSelected: Date = Date()

    lateinit var categories: List<Category>
    lateinit var categorySelected: Category
    lateinit var dateView: TextView
    lateinit var valueInputView: TextView
    lateinit var descriptionInputView: TextView
    lateinit var categoriesRadioGroup: RadioGroup

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_record)
        dateView = findViewById(R.id.dateView)
        dateView.setText(dateToString(dateSelected))
        valueInputView = findViewById(R.id.valueInput)
        descriptionInputView = findViewById(R.id.descriptionInput)
        showCategories()


    }

    @SuppressLint("ClickableViewAccessibility")
    fun showCategories() {
        runBlocking {
            launch(Dispatchers.IO) {
                categories = CategoryDBContainer.categoryDao.getAllCategories()
            }
        }
        categoriesRadioGroup = findViewById(R.id.categoriesListGroup)
        for (category in categories) {
            var button = RadioButton(this)
            button.setText(category.name)
            button.setOnTouchListener {v: View, event: MotionEvent -> onTouchCategoryButton(v as RadioButton) }
            categoriesRadioGroup.addView(button)
        }
    }

    fun onTouchCategoryButton(v: RadioButton): Boolean {
        v.isChecked = true
        categorySelected = categories.find { category: Category ->  when {
            category.name == v.text -> true
            else -> false
        }}!!
        return true
    }

//    fun showCategoriesListView() {
//        runBlocking {
//            launch(Dispatchers.IO) {
//                categories = CategoryDBContainer.categoryDao.getAllCategories()
//            }
//        }
//        val categoriesListView: ListView = findViewById(R.id.categoriesList)
//        val adapter:ArrayAdapter<String>  = ArrayAdapter(this, android.R.layout.simple_list_item_1,  categories.map {it.toString()})
//        categoriesListView.adapter = adapter
//        categoriesListView.setOnItemClickListener(AdapterView.OnItemClickListener() {
//                parent: AdapterView<*>, v: View, position: Int, id: Long ->
//            val selectedItem: Category = categories[position]
//            categorySelected = selectedItem
//
//        })
//    }

    fun onDatePick(view: View) {
        val datePicker =
            MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select date")
                .build()
        datePicker.show(supportFragmentManager, "tag");
        datePicker.addOnPositiveButtonClickListener {
            dateSelected = Date(datePicker.selection!!)
            dateView.setText(dateToString(dateSelected))
        }
    }

    fun createRecord(v: View) {
        runBlocking {
            launch(Dispatchers.IO) {
                RecordDBContainer.recordDao.insertRecord(Record(0, categorySelected.toString(), valueInputView.text.toString().toInt(), descriptionInputView.toString(), dateSelected ))
            }
        }
    }
}