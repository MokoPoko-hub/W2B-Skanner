package pl.kalinowski.w2bscanner

import android.content.Context
import android.os.Environment
import android.util.Log
import com.itextpdf.text.Document
import com.itextpdf.text.Paragraph
import com.itextpdf.text.pdf.PdfWriter
import org.omg.CORBA.Environment
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

class ConvertPDF(context: Context) {

    private var context: Context = context


    fun readTextFile(fileinput: String): String {
        val path = "/storage/emulated/0/$fileinput"
        println(fileinput)
        val file = File(path)
        val text = StringBuilder()
        try {
            val br = BufferedReader(FileReader(file))
            var line: String?
            while (br.readLine().also { line = it } != null) {
                text.append(line)
                text.append("\n")
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return text.toString()
    }


    fun savePDF(tekstToSave: String?) {

        val document = Document()

        try {
            val fileName = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(
                System.currentTimeMillis()
            )
            val filePath = "$fileName.pdf"
            PdfWriter.getInstance(
                document,
                FileOutputStream(
                    File(
                        Environment.getExternalStoragePublicDirectory("Documents"),
                        filePath
                    )
                )
            )
            document.open()
            document.add(Paragraph(tekstToSave))
            document.close()
            Log.i("Svae file", "File saved in" + getExternalStoragePublicDirectory("Documents") + filePath)
            Toast.makeText(context, "Zapisano plik", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Log.e("Svae file", "File saving failed")
        }
    }
}