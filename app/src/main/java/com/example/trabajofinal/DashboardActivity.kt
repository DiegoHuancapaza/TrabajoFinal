package com.example.trabajofinal
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class DashboardActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var tvTotalAmount: TextView
    private var currentUserEmail: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        dbHelper = DatabaseHelper(this)

        // Recibimos el correo que viene del Login
        currentUserEmail = intent.getStringExtra("USER_EMAIL") ?: ""

        // Referenciamos las vistas
        val tvUserName = findViewById<TextView>(R.id.tvUserName)
        tvTotalAmount = findViewById<TextView>(R.id.tvTotalAmount)
        val btnAddExpense = findViewById<Button>(R.id.btnAddExpense)
        val btnViewExpenses = findViewById<Button>(R.id.btnViewExpenses)
        val tvLogout = findViewById<TextView>(R.id.tvLogout)

        // Consultamos el nombre de usuario asociado al correo mediante SQLite
        val username = dbHelper.getUsername(currentUserEmail)
        tvUserName.text = username

        // Cargar total gastado
        updateTotalExpenses()

        // Navegación
        btnAddExpense.setOnClickListener {
            val intent = Intent(this, AddExpenseActivity::class.java)
            intent.putExtra("USER_EMAIL", currentUserEmail)
            startActivity(intent)
        }

        btnViewExpenses.setOnClickListener {
            val intent = Intent(this, ExpenseListActivity::class.java)
            intent.putExtra("USER_EMAIL", currentUserEmail)
            startActivity(intent)
        }

        tvLogout.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        // Actualiza el total cuando el usuario regresa de agregar o eliminar gastos
        updateTotalExpenses()
    }

    private fun updateTotalExpenses() {
        val total = dbHelper.getTotalExpenses(currentUserEmail)
        tvTotalAmount.text = String.format("S/ %.2f", total)
    }
}