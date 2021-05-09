package pl.kalinowski.w2b.skanner


import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import pl.kalinowski.w2bscanner.Scanner
import pl.kalinowski.w2bscanner.ScannerActivity




class MainActivity : AppCompatActivity() {


    lateinit var camera:Scanner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        Log.d("this", this.filesDir.toString())
        

        val i = Intent(this, ScannerActivity::class.java)
        startActivity(i)






        }











    }




