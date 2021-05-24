package pl.kalinowski.w2bscanner

import android.content.Context
import android.os.Environment
import android.util.Log
import com.itextpdf.text.Document
import com.itextpdf.text.Paragraph
import com.itextpdf.text.pdf.PdfWriter
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




        println(Environment.getExternalStoragePublicDirectory("Documents"))
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
            println(context.getExternalFilesDir(null))
            document.open()
            Log.d(this.toString(), tekstToSave!!)
            document.add(Paragraph(tekstToSave))
            document.close()
            println("Zapisano")
        } catch (e: Exception) {
            println(e)
        }
    }

}