package pl.kalinowski.w2bscanner

import android.content.ContentValues.TAG
import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import android.widget.Toast
import com.googlecode.tesseract.android.TessBaseAPI
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class ScannerOCR(context: Context, language: String?) {
    private val mTess: TessBaseAPI?


    fun getOCRResult(bitmap: Bitmap?): String {
        mTess!!.setImage(bitmap)
        return mTess.utF8Text
    }

    fun onDestroy() {
        mTess?.end()
    }


    fun setPolish(){

    }

    fun setEnglish(){

    }

    init {
        mTess = TessBaseAPI()
        var fileExistFlag = false
        val assetManager = context.assets
        var dstPathDir = "/tesseract/tessdata/"
        val srcFile = "pol.traineddata"
        var inFile: InputStream? = null
        dstPathDir = context.filesDir.toString() + dstPathDir
        val dstInitPathDir = context.filesDir.toString() + "/tesseract"
        val dstPathFile = dstPathDir + srcFile
        var outFile: FileOutputStream? = null
        try {
            inFile = assetManager.open(srcFile)
            val f = File(dstPathDir)
            if (!f.exists()) {
                if (!f.mkdirs()) {
                    Toast.makeText(context, "$srcFile can't be created.", Toast.LENGTH_SHORT).show()
                }
                outFile = FileOutputStream(File(dstPathFile))
            } else {
                fileExistFlag = true
            }
        } catch (ex: Exception) {
            Log.e(TAG, ex.message!!)
        } finally {
            if (fileExistFlag) {
                try {
                    if (inFile != null) inFile.close()
                    mTess.init(dstInitPathDir, language)
                } catch (ex: Exception) {
                    Log.e(TAG, ex.message!!)
                }
            }
            if (inFile != null && outFile != null) {
                try {
                    //copy file
                    val buf = ByteArray(1024)
                    var len: Int
                    while (inFile.read(buf).also { len = it } != -1) {
                        outFile.write(buf, 0, len)
                    }
                    inFile.close()
                    outFile.close()
                    mTess.init(dstInitPathDir, language)
                } catch (ex: Exception) {
                    Log.e(TAG, ex.message!!)
                }
            } else {

            }
        }
    }
}