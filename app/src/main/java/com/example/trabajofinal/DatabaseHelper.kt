package com.example.trabajofinal
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "UserDatabase.db"
        // Subimos a versión 2 para actualizar la estructura de la BD
        private const val DATABASE_VERSION = 3

        // Tabla Usuarios
        private const val COLUMN_USERNAME = "username"
        private const val TABLE_USERS = "users"
        private const val COLUMN_USER_ID = "id"
        private const val COLUMN_EMAIL = "email"
        private const val COLUMN_PASSWORD = "password"

        // Tabla Gastos
        private const val TABLE_EXPENSES = "expenses"
        private const val COLUMN_EXPENSE_ID = "expense_id"
        private const val COLUMN_EXPENSE_USER = "user_email"
        private const val COLUMN_AMOUNT = "amount"
        private const val COLUMN_DESCRIPTION = "description"
        private const val COLUMN_CATEGORY = "category"
        private const val COLUMN_DATE = "date"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createUsersTable = ("CREATE TABLE $TABLE_USERS ("
                + "$COLUMN_USER_ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "$COLUMN_USERNAME TEXT, "
                + "$COLUMN_EMAIL TEXT UNIQUE, "
                + "$COLUMN_PASSWORD TEXT)")

        val createExpensesTable = ("CREATE TABLE $TABLE_EXPENSES ("
                + "$COLUMN_EXPENSE_ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "$COLUMN_EXPENSE_USER TEXT, "
                + "$COLUMN_AMOUNT REAL, "
                + "$COLUMN_DESCRIPTION TEXT, "
                + "$COLUMN_CATEGORY TEXT, "
                + "$COLUMN_DATE TEXT)")

        db?.execSQL(createUsersTable)
        db?.execSQL(createExpensesTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) {
            val createExpensesTable = ("CREATE TABLE $TABLE_EXPENSES ("
                    + "$COLUMN_EXPENSE_ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "$COLUMN_EXPENSE_USER TEXT, "
                    + "$COLUMN_AMOUNT REAL, "
                    + "$COLUMN_DESCRIPTION TEXT, "
                    + "$COLUMN_CATEGORY TEXT, "
                    + "$COLUMN_DATE TEXT)")
            db?.execSQL(createExpensesTable)
        }
        if (oldVersion < 3) {
            db?.execSQL("ALTER TABLE $TABLE_USERS ADD COLUMN $COLUMN_USERNAME TEXT")
        }
    }

    // --- MÉTODOS DE USUARIO ---

    // Guardar el nombre al registrarse
    fun registerUser(username: String, email: String, pass: String): Boolean {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_USERNAME, username)
            put(COLUMN_EMAIL, email)
            put(COLUMN_PASSWORD, pass)
        }
        val result = db.insert(TABLE_USERS, null, values)
        db.close()
        return result != -1L
    }

    // Obtener el nombre de usuario mediante el correo
    fun getUsername(email: String): String {
        val db = this.readableDatabase
        var username = "Usuario"
        val cursor = db.rawQuery("SELECT $COLUMN_USERNAME FROM $TABLE_USERS WHERE $COLUMN_EMAIL = ?", arrayOf(email))
        if (cursor.moveToFirst()) {
            username = cursor.getString(0) ?: "Usuario"
        }
        cursor.close()
        db.close()
        return username
    }

    fun checkUser(email: String, pass: String): Boolean {
        val db = this.readableDatabase
        val query = "SELECT * FROM $TABLE_USERS WHERE $COLUMN_EMAIL = ? AND $COLUMN_PASSWORD = ?"
        val cursor = db.rawQuery(query, arrayOf(email, pass))
        val count = cursor.count
        cursor.close()
        db.close()
        return count > 0
    }

    fun checkEmailExists(email: String): Boolean {
        val db = this.readableDatabase
        val query = "SELECT * FROM $TABLE_USERS WHERE $COLUMN_EMAIL = ?"
        val cursor = db.rawQuery(query, arrayOf(email))
        val count = cursor.count
        cursor.close()
        db.close()
        return count > 0
    }

    // --- MÉTODOS CRUD DE GASTOS ---

    // 1. CREATE (Crear)
    fun addExpense(userEmail: String, amount: Double, description: String, category: String, date: String): Boolean {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_EXPENSE_USER, userEmail)
            put(COLUMN_AMOUNT, amount)
            put(COLUMN_DESCRIPTION, description)
            put(COLUMN_CATEGORY, category)
            put(COLUMN_DATE, date)
        }
        val result = db.insert(TABLE_EXPENSES, null, values)
        db.close()
        return result != -1L
    }

    // 2. READ (Leer todos los registros de un usuario)
    fun getAllExpenses(userEmail: String): ArrayList<Expense> {
        val list = ArrayList<Expense>()
        val db = this.readableDatabase
        val query = "SELECT * FROM $TABLE_EXPENSES WHERE $COLUMN_EXPENSE_USER = ? ORDER BY $COLUMN_EXPENSE_ID DESC"
        val cursor = db.rawQuery(query, arrayOf(userEmail))

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_EXPENSE_ID))
                val email = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EXPENSE_USER))
                val amount = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_AMOUNT))
                val description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION))
                val category = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY))
                val date = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE))

                list.add(Expense(id, email, amount, description, category, date))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return list
    }

    // 2.1 READ (Obtener el total gastado)
    fun getTotalExpenses(userEmail: String): Double {
        val db = this.readableDatabase
        var total = 0.0
        val cursor = db.rawQuery("SELECT SUM($COLUMN_AMOUNT) FROM $TABLE_EXPENSES WHERE $COLUMN_EXPENSE_USER = ?", arrayOf(userEmail))
        if (cursor.moveToFirst()) {
            total = cursor.getDouble(0)
        }
        cursor.close()
        db.close()
        return total
    }

    // 3. UPDATE (Actualizar registro)
    fun updateExpense(id: Int, amount: Double, description: String, category: String, date: String): Boolean {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_AMOUNT, amount)
            put(COLUMN_DESCRIPTION, description)
            put(COLUMN_CATEGORY, category)
            put(COLUMN_DATE, date)
        }
        val result = db.update(TABLE_EXPENSES, values, "$COLUMN_EXPENSE_ID = ?", arrayOf(id.toString()))
        db.close()
        return result > 0
    }

    // 4. DELETE (Eliminar registro)
    fun deleteExpense(id: Int): Boolean {
        val db = this.writableDatabase
        val result = db.delete(TABLE_EXPENSES, "$COLUMN_EXPENSE_ID = ?", arrayOf(id.toString()))
        db.close()
        return result > 0
    }
}