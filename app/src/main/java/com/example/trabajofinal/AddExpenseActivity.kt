package com.example.trabajofinal
import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import java.util.Calendar

class AddExpenseActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private var currentUserEmail: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_expense)

        dbHelper = DatabaseHelper(this)
        currentUserEmail = intent.getStringExtra("USER_EMAIL") ?: ""

        val etAmount = findViewById<TextInputEditText>(R.id.etAmount)
        val etDescription = findViewById<TextInputEditText>(R.id.etDescription)
        val spCategory = findViewById<Spinner>(R.id.spCategory)
        val etDate = findViewById<TextInputEditText>(R.id.etDate)
        val btnSave = findViewById<Button>(R.id.btnSaveExpense)

        // Configurar Opciones del Spinner/Categorías
        val categories = arrayOf("Comida", "Transporte", "Entretenimiento", "Salud", "Servicios", "Otros")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, categories)
        spCategory.adapter = adapter

        // Selector de Fecha (DatePickerDialog)
        val calendar = Calendar.getInstance()
        etDate.setOnClickListener {
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val dpd = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                val formattedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                etDate.setText(formattedDate)
            }, year, month, day)
            dpd.show()
        }

        // Guardar Gasto
        btnSave.setOnClickListener {
            val amountText = etAmount.text.toString().trim()
            val description = etDescription.text.toString().trim()
            val category = spCategory.selectedItem.toString()
            val date = etDate.text.toString().trim()

            if (amountText.isEmpty() || description.isEmpty() || date.isEmpty()) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val amount = amountText.toDoubleOrNull()
            if (amount == null || amount <= 0) {
                Toast.makeText(this, "Ingresa un monto válido", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val isInserted = dbHelper.addExpense(currentUserEmail, amount, description, category, date)
            if (isInserted) {
                Toast.makeText(this, "¡Gasto registrado con éxito!", Toast.LENGTH_SHORT).show()
                finish() // Regresa al Dashboard automáticamente
            } else {
                Toast.makeText(this, "Error al guardar el gasto", Toast.LENGTH_SHORT).show()
            }
        }
    }
}