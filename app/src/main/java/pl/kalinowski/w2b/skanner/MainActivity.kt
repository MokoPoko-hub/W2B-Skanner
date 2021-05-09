package pl.kalinowski.w2b.skanner

import androidx.annotation.RequiresApi
import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.widget.Button
import com.itextpdf.text.Document
import com.itextpdf.text.Paragraph
import com.itextpdf.text.pdf.PdfWriter
import java.io.BufferedReader
import java.io.File
import java.io.FileOutputStream
import java.io.FileReader
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Locale


@Suppress("DEPRECATION")
class MainActivity : Activity() {
    private var tekst = "tekst"

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val button = findViewById<Button>(R.id.button)
        val button2 = findViewById<Button>(R.id.button2)
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED || checkSelfPermission(
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_DENIED
            ) {
                val permissions = arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
                requestPermissions(permissions, STORAGE_CODE)
            } else {
                println("Zgody udzielone")
            }
        }
        button.setOnClickListener {
            savePDF(
                tekst
            )
        }
        button2.setOnClickListener { searchFile() }
    }

    private fun readTextFile(fileinput: String): String {
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

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    fun searchFile() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "text/*"
        startActivityForResult(intent, READ_FILE_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == READ_FILE_CODE && resultCode == RESULT_OK) {
            val uri = data?.data
            var path = uri!!.path
            path = path!!.substring(path.indexOf(":") + 1)
            println(readTextFile(path))
        }
        else {
            println("Nie wybrano pliku")
        }
    }

    private fun savePDF(tekstToSave: String?) {
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
            println(getExternalFilesDir(null))
            document.open()
            document.addAuthor("Lewandowski")
            document.add(Paragraph(tekstToSave))
            document.close()
            println("Zapisano")
        } catch (e: Exception) {
            println(e)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == STORAGE_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                println("Dziala")
                savePDF(tekst)
            } else {
                println("Nie udalo dodac sie elementow")
            }
        }
    }

    companion object {
        private const val STORAGE_CODE = 1000
        private const val READ_FILE_CODE = 1010
    }
}