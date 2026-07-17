package com.dsm.miprofeenlinea.presentacion.chart

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.dsm.miprofeenlinea.model.Tarea

class DashboardLocalDataSource(context: Context) :
    SQLiteOpenHelper(context.applicationContext, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS tareas_cache (
                id TEXT PRIMARY KEY,
                imagen TEXT NOT NULL DEFAULT '',
                estado TEXT NOT NULL DEFAULT '',
                tarifa REAL,
                docenteNombre TEXT NOT NULL DEFAULT '',
                alumnoId TEXT NOT NULL DEFAULT '',
                docenteId TEXT NOT NULL DEFAULT '',
                createdAt INTEGER,
                acceptedAt INTEGER,
                completedAt INTEGER,
                cancelledAt INTEGER,
                tarifaTimestamp INTEGER,
                finalizadoAt INTEGER,
                ratingTeacher REAL,
                ratingStudent REAL,
                cachedAt INTEGER NOT NULL
            )
            """.trimIndent()
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS tareas_cache")
        onCreate(db)
    }

    fun replaceForUser(role: DashboardRole, userId: String, tareas: List<Tarea>) {
        writableDatabase.use { db ->
            db.beginTransaction()
            try {
                db.delete("tareas_cache", "${role.firestoreField} = ?", arrayOf(userId))
                tareas.forEach { tarea ->
                    db.insertWithOnConflict(
                        "tareas_cache",
                        null,
                        tarea.toValues(),
                        SQLiteDatabase.CONFLICT_REPLACE
                    )
                }
                db.setTransactionSuccessful()
            } finally {
                db.endTransaction()
            }
        }
    }

    fun readForUser(role: DashboardRole, userId: String): List<Tarea> =
        readableDatabase.use { db ->
            db.query(
                "tareas_cache",
                null,
                "${role.firestoreField} = ?",
                arrayOf(userId),
                null,
                null,
                "createdAt DESC"
            ).use { cursor ->
                buildList {
                    while (cursor.moveToNext()) {
                        add(cursor.toTarea())
                    }
                }
            }
        }

    private fun Tarea.toValues(): ContentValues =
        ContentValues().apply {
            put("id", id)
            put("imagen", imagen)
            put("estado", estado)
            put("tarifa", tarifa)
            put("docenteNombre", docenteNombre)
            put("alumnoId", alumnoId)
            put("docenteId", docenteId)
            put("createdAt", createdAt)
            put("acceptedAt", acceptedAt)
            put("completedAt", completedAt)
            put("cancelledAt", cancelledAt)
            put("tarifaTimestamp", tarifaTimestamp)
            put("finalizadoAt", finalizadoAt)
            put("ratingTeacher", ratingTeacher)
            put("ratingStudent", ratingStudent)
            put("cachedAt", System.currentTimeMillis())
        }

    private fun Cursor.toTarea(): Tarea =
        Tarea(
            id = getStringValue("id"),
            imagen = getStringValue("imagen"),
            estado = getStringValue("estado"),
            tarifa = getDoubleValue("tarifa"),
            docenteNombre = getStringValue("docenteNombre"),
            alumnoId = getStringValue("alumnoId"),
            docenteId = getStringValue("docenteId"),
            createdAt = getLongValue("createdAt"),
            acceptedAt = getLongValue("acceptedAt"),
            completedAt = getLongValue("completedAt"),
            cancelledAt = getLongValue("cancelledAt"),
            tarifaTimestamp = getLongValue("tarifaTimestamp"),
            finalizadoAt = getLongValue("finalizadoAt"),
            ratingTeacher = getDoubleValue("ratingTeacher"),
            ratingStudent = getDoubleValue("ratingStudent")
        )

    private fun Cursor.getStringValue(column: String): String =
        getString(getColumnIndexOrThrow(column)) ?: ""

    private fun Cursor.getLongValue(column: String): Long? {
        val index = getColumnIndexOrThrow(column)
        return if (isNull(index)) null else getLong(index)
    }

    private fun Cursor.getDoubleValue(column: String): Double? {
        val index = getColumnIndexOrThrow(column)
        return if (isNull(index)) null else getDouble(index)
    }

    companion object {
        private const val DATABASE_NAME = "dashboard_cache.db"
        private const val DATABASE_VERSION = 1
    }
}
