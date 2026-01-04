package com.xluis.inventarioefa.System.Storage

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import java.io.BufferedReader
import java.io.InputStreamReader

class StorageManager(private val context: Context) {

    @Composable
    fun rememberCsvLauncher(onCsvLoaded: (List<List<String>>?) -> Unit)
            : () -> Unit {

        val launcher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.OpenDocument(),
            onResult = { uri: Uri? ->
                val result = uri?.let { readCsvFile(it) }
                onCsvLoaded(result)
            }
        )

        return { launcher.launch(getCsvMimeTypes()) }
    }

    fun readCsvFile(
        uri: Uri
    ): List<List<String>>? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val reader = BufferedReader(InputStreamReader(inputStream))

            val csvData = mutableListOf<List<String>>()

            reader.useLines { lines ->
                lines.forEach { line ->
                    val row = line.split(",")
                    csvData.add(row)
                }
            }
            csvData
        } catch (e: Exception) {
            null
        }
    }

    private fun getCsvMimeTypes() = arrayOf(
        "text/csv",
        "text/comma-separated-values",
        "application/csv",
        "application/vnd.ms-excel"
    )
}