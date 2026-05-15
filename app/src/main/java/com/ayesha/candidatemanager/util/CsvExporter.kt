package com.ayesha.candidatemanager.util

import android.content.Context
import android.os.Environment
import android.widget.Toast
import com.ayesha.candidatemanager.data.CandidateEntity
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object CsvExporter {
    fun exportToCsv(context: Context, candidates: List<CandidateEntity>) {
        try {
            val dateStr = SimpleDateFormat("ddMMyyyy", Locale.getDefault()).format(Date())
            val filename = "candidates_$dateStr.csv"
            
            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            if (!downloadsDir.exists()) downloadsDir.mkdirs()
            
            val file = File(downloadsDir, filename)
            val writer = FileWriter(file)
            
            // Write Header
            writer.append("Date,Candidate Name,Job Position,Current Company,Contact Number,Email ID,Current CTC,CTC Type,Expected CTC,Notice Period,Available To Join From,Remarks\n")
            
            // Write Data
            for (c in candidates) {
                writer.append("${escapeCsv(c.date)},")
                writer.append("${escapeCsv(c.candidateName)},")
                writer.append("${escapeCsv(c.jobPosition)},")
                writer.append("${escapeCsv(c.currentCompany)},")
                writer.append("${escapeCsv(c.contactNumber)},")
                writer.append("${escapeCsv(c.emailId)},")
                writer.append("${escapeCsv(c.currentCtc)},")
                writer.append("${escapeCsv(c.ctcType)},")
                writer.append("${escapeCsv(c.expectedCtc)},")
                writer.append("${escapeCsv(c.noticePeriod)},")
                writer.append("${escapeCsv(c.availableToJoinFrom)},")
                writer.append("${escapeCsv(c.remarks)}\n")
            }
            
            writer.flush()
            writer.close()
            
            Toast.makeText(context, "Exported successfully to Downloads: $filename", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Toast.makeText(context, "Export failed: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun escapeCsv(value: String): String {
        var escaped = value
        if (escaped.contains("\"")) {
            escaped = escaped.replace("\"", "\"\"")
        }
        if (escaped.contains(",") || escaped.contains("\n")) {
            escaped = "\"$escaped\""
        }
        return escaped
    }
}
