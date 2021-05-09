package pl.kalinowski.w2b.skanner



import android.provider.MediaStore
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import pl.kalinowski.w2bscanner.Scanner
import pl.kalinowski.w2bscanner.ScannerActivity

import androidx.annotation.RequiresApi
import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.widget.Button
import android.widget.Toast
import com.itextpdf.text.Document
import com.itextpdf.text.Paragraph
import com.itextpdf.text.pdf.PdfWriter
import pl.kalinowski.w2bscanner.ConvertPDF
import java.io.BufferedReader
import java.io.File
import java.io.FileOutputStream
import java.io.FileReader
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Locale


@Suppress("DEPRECATION")
class MainActivity : Activity() {

    val convert = ConvertPDF(this)

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
                Log.i("Resource access", "The application grants access to resources")
            }
        }
        button.setOnClickListener {
            val i = Intent(this, ScannerActivity::class.java)
            startActivity(i)
        }
        button2.setOnClickListener { searchFile() }
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
            println(convert.readTextFile(path))
        } else {
            Toast.makeText(this, "Nie wybrano pliku", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == STORAGE_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.i("Resource access", "Access to resources has been granted")
            } else {
                Log.e("Resource access", "Failed to access resources")
            }
        }
    }

    companion object {
        private const val STORAGE_CODE = 1000
        private const val READ_FILE_CODE = 1010

    }
}




