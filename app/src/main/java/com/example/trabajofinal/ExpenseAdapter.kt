package com.example.trabajofinal
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ExpenseAdapter(
    private var expenseList: ArrayList<Expense>,
    private val onDeleteClick: (Expense) -> Unit
) : RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder>() {

    class ExpenseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvDescription: TextView = itemView.findViewById(R.id.tvItemDescription)
        val tvDetails: TextView = itemView.findViewById(R.id.tvItemDetails)
        val tvAmount: TextView = itemView.findViewById(R.id.tvItemAmount)
        val btnDelete: ImageButton = itemView.findViewById(R.id.btnDeleteExpense)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_expense, parent, false)
        return ExpenseViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        val expense = expenseList[position]
        holder.tvDescription.text = expense.description
        holder.tvDetails.text = "${expense.category} • ${expense.date}"

        // Cambiado de -$%.2f a -S/ %.2f
        holder.tvAmount.text = String.format("-S/ %.2f", expense.amount)

        holder.btnDelete.setOnClickListener {
            onDeleteClick(expense)
        }
    }

    override fun getItemCount(): Int = expenseList.size

    fun updateData(newList: ArrayList<Expense>) {
        expenseList = newList
        notifyDataSetChanged()
    }
}