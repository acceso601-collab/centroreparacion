package com.ejemplo.centroreparacion.util

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.net.Uri
import androidx.core.content.FileProvider
import com.ejemplo.centroreparacion.data.entity.*
import com.ejemplo.centroreparacion.repository.RepairRepository
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class PdfGenerator(private val context: Context, private val repo: RepairRepository) {

    suspend fun generateRepairPdf(repairId: Long): Uri? {
        val repair = repo.getRepair(repairId) ?: return null
        val checklist = repo.checklistFor(repairId)
        val measurements = repo.measurementsFor(repairId)
        val photos = repo.photosFor(repairId)
        val parts = repo.usedPartsFor(repairId)
        val financials = repo.financialsFor(repairId)

        val document = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4 en puntos
        var page = document.startPage(pageInfo)
        var canvas = page.canvas

        val paintTitle = Paint().apply { textSize = 20f; isFakeBoldText = true; color = Color.BLACK }
        val paintSection = Paint().apply { textSize = 14f; isFakeBoldText = true; color = Color.DKGRAY }
        val paintText = Paint().apply { textSize = 11f; color = Color.BLACK }
        val paintSmall = Paint().apply { textSize = 9f; color = Color.GRAY }

        var y = 40f
        val margin = 40f
        val lineHeight = 16f
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

        fun newPageIfNeeded(needed: Float = 60f) {
            if (y + needed > 800f) {
                document.finishPage(page)
                val pi = PdfDocument.PageInfo.Builder(595, 842, document.pages.size + 1).create()
                page = document.startPage(pi)
                canvas = page.canvas
                y = 40f
            }
        }

        // Encabezado
        canvas.drawText("Centro de Reparación", margin, y, paintTitle); y += 24f
        canvas.drawText("Informe de Reparación #${repair.id}", margin, y, paintSection); y += 20f
        canvas.drawText("Generado: ${sdf.format(Date())}", margin, y, paintSmall); y += 20f

        // Cliente
        canvas.drawText("Cliente", margin, y, paintSection); y += lineHeight
        canvas.drawText("Nombre: ${repair.clientName}", margin, y, paintText); y += lineHeight
        canvas.drawText("Teléfono: ${repair.clientPhone}", margin, y, paintText); y += lineHeight
        y += 6f

        // Dispositivo
        canvas.drawText("Dispositivo", margin, y, paintSection); y += lineHeight
        canvas.drawText("Marca / Modelo: ${repair.brand} ${repair.model}", margin, y, paintText); y += lineHeight
        canvas.drawText("IMEI / Serie: ${repair.imei}", margin, y, paintText); y += lineHeight
        canvas.drawText("Estado físico: ${repair.physicalState}", margin, y, paintText); y += lineHeight
        canvas.drawText("Accesorios: ${repair.accessories}", margin, y, paintText); y += lineHeight
        y += 6f

        // Fechas
        canvas.drawText("Entrada: ${sdf.format(Date(repair.entryDate))}", margin, y, paintText); y += lineHeight
        canvas.drawText("Entrega estimada: ${sdf.format(Date(repair.estimatedDate))}", margin, y, paintText); y += lineHeight
        canvas.drawText("Estado actual: ${Constants.RepairStatus.label(repair.status)}", margin, y, paintText); y += lineHeight
        y += 6f

        // Problema
        canvas.drawText("Problema reportado", margin, y, paintSection); y += lineHeight
        y = drawWrapped(canvas, repair.problem, margin, y, 515f, paintText, lineHeight); y += 6f

        // Checklist
        newPageIfNeeded(80f)
        canvas.drawText("Checklist", margin, y, paintSection); y += lineHeight
        var lastCat = ""
        for (item in checklist.value.orEmpty()) {
            if (item.category != lastCat) {
                newPageIfNeeded(40f)
                y += 4f
                canvas.drawText("· ${item.category}", margin, y, paintSection); y += lineHeight
                lastCat = item.category
            }
            newPageIfNeeded(20f)
            val state = when (item.status) {
                Constants.Checklist.OK -> "[✓]"
                Constants.Checklist.FAILED -> "[✗]"
                else -> "[ ]"
            }
            canvas.drawText("$state ${item.name}", margin + 10f, y, paintText); y += lineHeight
        }
        y += 6f

        // Mediciones
        newPageIfNeeded(60f)
        canvas.drawText("Mediciones", margin, y, paintSection); y += lineHeight
        for (m in measurements.value.orEmpty()) {
            newPageIfNeeded(20f)
            val date = sdf.format(Date(m.timestamp))
            canvas.drawText("${m.type}: ${m.value} ${m.unit} @ ${m.point}  ·  $date", margin, y, paintText)
            y += lineHeight
        }
        y += 6f

        // Piezas usadas
        newPageIfNeeded(60f)
        canvas.drawText("Piezas utilizadas", margin, y, paintSection); y += lineHeight
        for (p in parts.value.orEmpty()) {
            newPageIfNeeded(20f)
            canvas.drawText("${p.name}  x${p.quantity}  ·  ${MoneyUtils.format(p.unitPrice)} = ${MoneyUtils.format(p.subtotal)}",
                margin, y, paintText)
            y += lineHeight
        }
        y += 6f

        // Costos
        newPageIfNeeded(100f)
        canvas.drawText("Resumen financiero", margin, y, paintSection); y += lineHeight
        canvas.drawText("Costo piezas: ${MoneyUtils.format(financials.piecesCost)}", margin, y, paintText); y += lineHeight
        canvas.drawText("Mano de obra: ${MoneyUtils.format(financials.laborCost)}", margin, y, paintText); y += lineHeight
        canvas.drawText("Otros gastos: ${MoneyUtils.format(financials.otherCosts)}", margin, y, paintText); y += lineHeight
        canvas.drawText("Costo total: ${MoneyUtils.format(financials.totalCost)}", margin, y, paintText); y += lineHeight
        canvas.drawText("Precio cobrado: ${MoneyUtils.format(financials.chargedPrice)}", margin, y, paintText); y += lineHeight
        canvas.drawText("Ganancia: ${MoneyUtils.format(financials.profit)}", margin, y, paintText); y += lineHeight
        y += 6f

        // Notas
        if (repair.notes.isNotBlank()) {
            newPageIfNeeded(60f)
            canvas.drawText("Notas", margin, y, paintSection); y += lineHeight
            y = drawWrapped(canvas, repair.notes, margin, y, 515f, paintText, lineHeight)
        }

        // Fotos (miniaturas)
        val photoList = photos.value.orEmpty()
        if (photoList.isNotEmpty()) {
            newPageIfNeeded(200f)
            canvas.drawText("Fotografías", margin, y, paintSection); y += lineHeight + 4f
            var x = margin
            var imgY = y
            var perRow = 0
            val thumbSize = 110f
            for (photo in photoList) {
                val f = File(photo.path)
                if (!f.exists()) continue
                val bmp = BitmapFactory.decodeFile(f.absolutePath) ?: continue
                val scaled = android.graphics.Bitmap.createScaledBitmap(bmp, thumbSize.toInt(), thumbSize.toInt(), true)
                if (imgY + thumbSize > 800f) {
                    document.finishPage(page)
                    val pi = PdfDocument.PageInfo.Builder(595, 842, document.pages.size + 1).create()
                    page = document.startPage(pi)
                    canvas = page.canvas
                    imgY = 40f; x = margin; perRow = 0
                }
                canvas.drawBitmap(scaled, x, imgY, null)
                x += thumbSize + 8f
                perRow++
                if (perRow >= 4) { x = margin; imgY += thumbSize + 8f; perRow = 0 }
            }
            y = imgY + thumbSize + 20f
        }

        document.finishPage(page)

        // Guardar
        val dir = File(context.filesDir, "reports").apply { mkdirs() }
        val file = File(dir, "reparacion_${repair.id}_${System.currentTimeMillis()}.pdf")
        FileOutputStream(file).use { document.writeTo(it) }
        document.close()

        return FileProvider.getUriForFile(
            context,
            "${context.packageName}${Constants.FILE_PROVIDER}",
            file
        )
    }

    private fun drawWrapped(
        canvas: android.graphics.Canvas,
        text: String,
        x: Float, startY: Float, maxWidth: Float,
        paint: Paint, lineHeight: Float
    ): Float {
        var y = startY
        val words = text.split(" ")
        var line = ""
        for (w in words) {
            val test = if (line.isEmpty()) w else "$line $w"
            if (paint.measureText(test) > maxWidth) {
                canvas.drawText(line, x, y, paint)
                y += lineHeight
                line = w
            } else line = test
        }
        if (line.isNotEmpty()) { canvas.drawText(line, x, y, paint); y += lineHeight }
        return y
    }
}
