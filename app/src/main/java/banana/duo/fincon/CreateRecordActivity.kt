package banana.duo.fincon

import android.annotation.SuppressLint
import android.app.AlertDialog
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
import banana.duo.fincon.utils.normalDate;
import android.content.DialogInterface
import android.text.InputType

import android.widget.EditText
import kotlin.properties.Delegates
import android.widget.RadioGroup

import android.widget.RadioButton
import androidx.core.view.get
import com.google.android.material.snackbar.Snackbar


class CreateRecordActivity : AppCompatActivity() {

    var dateSelected: Date = Date()

    lateinit var categories: List<Category>
    lateinit var categorySelected: String
    lateinit var dateView: TextView
    lateinit var valueInputView: TextView
    lateinit var categoriesRadioGroup: RadioGroup
    lateinit var categorySelectButton: Button
    var value: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_record)
        dateView = findViewById(R.id.dateSelectButton)
        dateView.setText(dateToString(dateSelected))
        valueInputView = findViewById(R.id.valueInputButton)
        categorySelectButton = findViewById(R.id.categorySelectButton)

    }

    @SuppressLint("ClickableViewAccessibility")
    fun onShowCategories(view: View) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        runBlocking {
            launch(Dispatchers.IO) {
                categories = CategoryDBContainer.categoryDao.getAllCategories()
            }
        }
        categoriesRadioGroup = RadioGroup(this)
        categoriesRadioGroup.orientation = RadioGroup.VERTICAL
        var i = 0
        for (category in categories) {
            val button = RadioButton(this)
            button.text =category.toString()
            button.id = i + 100
            button.setOnTouchListener { _, _ -> onTouchCategoryButton(button)}
            categoriesRadioGroup.addView(button)
            i++
        }
        builder.setTitle("").setMessage("").setView(categoriesRadioGroup)
        builder.setPositiveButton("Сохранить", object : DialogInterface.OnClickListener {
            @SuppressLint("SetTextI18n")
            override fun onClick(di: DialogInterface?, i: Int) {
                categorySelectButton.text = categorySelected
            }
        })
        builder.setNegativeButton("Отмена", object : DialogInterface.OnClickListener {
            override fun onClick(di: DialogInterface?, i: Int) {}
        })
        builder.create().show()

    }

    fun onTouchCategoryButton(v: RadioButton): Boolean {
        v.isChecked = true
        categorySelected = categories.map { category -> category.toString() }.find { category: String -> when {
            category == v.text -> true
            else -> false
        }}!!
        return true
    }

//    fun showCategoriesListView() {
//
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

    fun onValueInput(view: View) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        val text = EditText(this)
        text.inputType = (InputType.TYPE_CLASS_NUMBER.or(InputType.TYPE_NUMBER_FLAG_SIGNED))

        builder.setTitle(R.string.valueInputDialogTitle).setMessage("").setView(text)
        builder.setPositiveButton("Сохранить", object : DialogInterface.OnClickListener {
            @SuppressLint("SetTextI18n")
            override fun onClick(di: DialogInterface?, i: Int) {
                value = text.text.toString().toInt()
                valueInputView.setText(value.toString())
            }
        })
        builder.setNegativeButton("Отмена", object : DialogInterface.OnClickListener {
            override fun onClick(di: DialogInterface?, i: Int) {}
        })
        builder.create().show()
    }



    fun createRecord(v: View) {
        if (categorySelected != null && value != null  && dateSelected != null) {
            runBlocking {
                launch(Dispatchers.IO) {
                    RecordDBContainer.recordDao.insertRecord(Record(0, categorySelected, value, dateSelected))
                }
            }
            Snackbar.make(v, "Запись создана", Snackbar.LENGTH_LONG)
                .show();
        } else {
            val builder: AlertDialog.Builder = AlertDialog.Builder(this)
            builder.setTitle("Ошибка").setMessage("Заполните все поля")
            builder.setPositiveButton("Сохранить", object : DialogInterface.OnClickListener {
                @SuppressLint("SetTextI18n")
                override fun onClick(di: DialogInterface?, i: Int) {

                }
            })
            builder.setNegativeButton("Отмена", object : DialogInterface.OnClickListener {
                override fun onClick(di: DialogInterface?, i: Int) {}
            })
            builder.create().show()
        }

    }
}