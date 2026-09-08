package com.example.trabajofinal

data class Expense(
    val id: Int,
    val userEmail: String,
    val amount: Double,
    val description: String,
    val category: String,
    val date: String
)