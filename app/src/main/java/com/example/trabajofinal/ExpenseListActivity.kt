package com.example.trabajofinal
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ExpenseListActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var adapter: ExpenseAdapter
    private var currentUserEmail: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_expense_list)

        dbHelper = DatabaseHelper(this)
        currentUserEmail = intent.getStringExtra("USER_EMAIL") ?: ""

        val rvExpenses = findViewById<RecyclerView>(R.id.rvExpenses)
        rvExpenses.layoutManager = LinearLayoutManager(this)

        val expensesList = dbHelper.getAllExpenses(currentUserEmail)

        adapter = ExpenseAdapter(expensesList) { expense ->
            val deleted = dbHelper.deleteExpense(expense.id)
            if (deleted) {
                Toast.makeText(this, "Gasto eliminado", Toast.LENGTH_SHORT).show()
                adapter.updateData(dbHelper.getAllExpenses(currentUserEmail))
            } else {
                Toast.makeText(this, "Error al eliminar", Toast.LENGTH_SHORT).show()
            }
        }

        rvExpenses.adapter = adapter
    }
}