package com.ejemplo.centroreparacion.ui.repairs

import android.content.Context
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import java.io.File
import java.io.FileOutputStream

class PhotoPickerHelper(private val activity: ComponentActivity) {

    private var onPicked: ((Uri) -> Unit)? = null
    private val galleryLauncher: ActivityResultLauncher<String> =
        activity.registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let { onPicked?.invoke(it) }
        }

    fun showPicker(onPick: (Uri) -> Unit) {
        onPicked = onPick
        AlertDialog.Builder(activity)
            .setTitle("Añadir foto")
            .setItems(arrayOf("Galería")) { _, which ->
                if (which == 0) galleryLauncher.launch("image/*")
            }
            .show()
    }

    companion object {
        /** Copia la imagen a filesDir/photos/repair_{id}/ y devuelve la ruta. */
        fun copyToAppStorage(context: Context, uri: Uri, repairId: Long): String? {
            return runCatching {
                val dir = File(context.filesDir, "photos/repair_$repairId").apply { mkdirs() }
                val file = File(dir, "img_${System.currentTimeMillis()}.jpg")
                context.contentResolver.openInputStream(uri)?.use { input ->
                    FileOutputStream(file).use { output -> input.copyTo(output) }
                }
                file.absolutePath
            }.getOrNull()
        }
    }
}
