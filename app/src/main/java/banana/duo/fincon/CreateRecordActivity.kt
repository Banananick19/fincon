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
import android.graphics.Color
import android.text.InputType

import android.widget.EditText
import kotlin.properties.Delegates
import android.widget.RadioGroup

import android.widget.RadioButton
import androidx.core.view.get
import banana.duo.fincon.utils.getResId
import banana.duo.fincon.utils.randomColor
import com.google.android.material.snackbar.Snackbar


class CreateRecordActivity : AppCompatActivity() {

    var dateSelected: Date = Date()

    lateinit var categories: List<Category>
    lateinit var categorySelected: String
    lateinit var dateView: TextView
    lateinit var valueInputView: TextView
    lateinit var categoriesRadioGroup: RadioGroup
    lateinit var categorySelectButton: Button
    var newCategoryExpence = false
    var newCategoryIncome = false
    lateinit var newCategoryName: String
    var value: Int = 0
    var newCategory = false

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
        val button = RadioButton(this)
        button.text = resources.getText(R.string.makeOwnCategory)
        button.id = i + 100
        button.setOnTouchListener { _, _ -> onTouchCategoryButton(button)}
        categoriesRadioGroup.addView(button)
        builder.setTitle("").setMessage("").setView(categoriesRadioGroup)
        builder.setPositiveButton(resources.getText(R.string.save_button), object : DialogInterface.OnClickListener {
            @SuppressLint("SetTextI18n")
            override fun onClick(di: DialogInterface?, i: Int) {
                if (newCategory) {
                    makeNewCategoryForm()
                } else {
                    categorySelectButton.text = categorySelected
                }
            }
        })
        builder.setNegativeButton(resources.getText(R.string.cancel_button), object : DialogInterface.OnClickListener {
            override fun onClick(di: DialogInterface?, i: Int) {}
        })
        builder.create().show()

    }

    @SuppressLint("UseCompatLoadingForDrawables", "ClickableViewAccessibility")
    fun makeNewCategoryForm() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        val layout = LinearLayout(this)
        layout.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        layout.orientation = LinearLayout.VERTICAL
        val categoryNameInput = EditText(this)
        categoryNameInput.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        categoryNameInput.hint = "имя"
        val radioGroup = RadioGroup(this)
        radioGroup.orientation = RadioGroup.VERTICAL
        var i = 0
        for (type in listOf("Расход", "Доход")) {
            val button = RadioButton(this)
            button.text = type
            button.id = i + 100
            button.setOnTouchListener { _, _ -> onTouchTypeCategoryButton(button)}
            radioGroup.addView(button)
            i++
        }
        layout.addView(categoryNameInput)
        layout.addView(radioGroup)
        builder.setTitle("").setMessage(resources.getText(R.string.new_category_make)).setView(layout)
        builder.setPositiveButton(resources.getText(R.string.save_button), object : DialogInterface.OnClickListener {
            @SuppressLint("SetTextI18n")
            override fun onClick(di: DialogInterface?, i: Int) {
                if (newCategoryIncome == newCategoryExpence || categoryNameInput.text.toString() == "" ) return
                newCategoryName = categoryNameInput.text.toString()
                val category = Category(0, newCategoryName, randomColor(), "new_income3", newCategoryExpence, newCategoryIncome)
                runBlocking {
                    launch(Dispatchers.IO) {
                        CategoryDBContainer.categoryDao.insertCategory(category)
                    }
                }
                categorySelected = category.name
                categories = categories + category
                categorySelectButton.text = category.name
            }
        })
        builder.setNegativeButton(resources.getText(R.string.cancel_button), object : DialogInterface.OnClickListener {
            override fun onClick(di: DialogInterface?, i: Int) {}
        })
        builder.create().show()
    }

    fun onTouchTypeCategoryButton(v: RadioButton): Boolean {
        v.isChecked = true
        when (v.text) {
            "Расход" ->  {
                newCategoryExpence = true
                newCategoryIncome = false
            }
            "Доход" -> {
                newCategoryExpence = false
                newCategoryIncome = true
            }
        }
        return true
    }

    fun onTouchCategoryButton(v: RadioButton): Boolean {
        v.isChecked = true
        if (v.text == resources.getText(R.string.makeOwnCategory)) {
            newCategory = true
            return true
        }
        categorySelected = categories.map { category -> category.toString() }.find { category: String -> when {
            category == v.text -> true
            else -> false
        }}!!
        return true
    }


    fun onDatePick(view: View) {
        val datePicker =
            MaterialDatePicker.Builder.datePicker()
                .setTitleText(resources.getText(R.string.pick_date))
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
        text.inputType = InputType.TYPE_CLASS_NUMBER

        builder.setTitle(R.string.valueInputDialogTitle).setMessage("").setView(text)
        builder.setPositiveButton(resources.getText(R.string.save_button), object : DialogInterface.OnClickListener {
            @SuppressLint("SetTextI18n")
            override fun onClick(di: DialogInterface?, i: Int) {
                value = text.text.toString().toInt()
                valueInputView.setText(value.toString())
            }
        })
        builder.setNegativeButton(resources.getText(R.string.cancel_button), object : DialogInterface.OnClickListener {
            override fun onClick(di: DialogInterface?, i: Int) {}
        })
        builder.create().show()
    }



    fun createRecord(v: View) {
        if (value != 0 && this::categorySelected.isInitialized) {
            runBlocking {
                launch(Dispatchers.IO) {
                    RecordDBContainer.recordDao.insertRecord(Record(0, categories.find {category -> category.name == categorySelected }!!, value, dateSelected))
                }
            }
            Snackbar.make(v, resources.getText(R.string.record_saved), Snackbar.LENGTH_LONG)
                .show()
        } else {
            val builder: AlertDialog.Builder = AlertDialog.Builder(this)
            builder.setTitle(R.string.valueInputDialogTitle).setMessage(resources.getText(R.string.write_in_all_inputs))
            builder.setPositiveButton(resources.getText(R.string.ok_button), object : DialogInterface.OnClickListener {
                @SuppressLint("SetTextI18n")
                override fun onClick(di: DialogInterface?, i: Int) {
                }
            })
            builder.setNegativeButton(resources.getText(R.string.close_button), object : DialogInterface.OnClickListener {
                override fun onClick(di: DialogInterface?, i: Int) {}
            })
            builder.create().show()
        }
    }
}