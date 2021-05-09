package pl.kalinowski.w2bscanner

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Matrix
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.SurfaceView
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.googlecode.tesseract.android.TessBaseAPI
import org.opencv.android.*
import org.opencv.core.*
import org.opencv.imgproc.Imgproc
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException

class ScannerActivity : AppCompatActivity(), CameraBridgeViewBase.CvCameraViewListener2 {

    var cameraBridgeViewBase: CameraBridgeViewBase? = null
    var baseLoaderCallback: BaseLoaderCallback? = null
    var startCanny = false

    val tess = TessBaseAPI()


    lateinit var biggest: MatOfPoint
    lateinit var imgWarped: Mat


    private val FILE_NAME = "photo.png"

    private lateinit var photoFile: File


    fun Canny(Button: View?) {
        startCanny = startCanny == false
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scanner)



        cameraBridgeViewBase = findViewById<JavaCameraView>(R.id.CameraView)
        cameraBridgeViewBase!!.visibility = SurfaceView.VISIBLE
        (cameraBridgeViewBase as JavaCameraView).setCameraPermissionGranted();
        cameraBridgeViewBase!!.setCvCameraViewListener(this)


        //System.loadLibrary(Core.NATIVE_LIBRARY_NAME);


        //System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        baseLoaderCallback = object : BaseLoaderCallback(this) {
            override fun onManagerConnected(status: Int) {
                when (status) {
                    LoaderCallbackInterface.SUCCESS -> {

                        cameraBridgeViewBase!!.enableView()
                    }
                    else -> super.onManagerConnected(status)
                }
            }
        }


    }



    override fun onCameraViewStarted(width: Int, height: Int) {

    }

    override fun onCameraViewStopped() {

    }

    override fun onCameraFrame(inputFrame: CameraBridgeViewBase.CvCameraViewFrame?): Mat {
        val frame = inputFrame!!.rgba()
        val frame1 = frame.clone()
        val frame2 = frame.clone()

        val photobtn: FloatingActionButton =findViewById(R.id.canny)





        val preProcessing = preProcessing(frame)
        biggest = getContours(preProcessing, frame1)!!
        if(!biggest.empty()) {
            //imgWarped = getWarp(frame2, biggest)!!
            imgWarped = frame1

            photobtn.setOnClickListener { view ->
                Toast.makeText(this, "Photo", Toast.LENGTH_SHORT).show()
                var bmp: Bitmap
                val image = getWarp(frame2, biggest)!!
                bmp = converMat2Bitmat(getWarp(frame2, biggest)!!)!!

                bmp = rotateBitmap(bmp, 90F)!!

                val scanner = ScannerOCR(this, "pol")
                Log.d(this.toString(), scanner.getOCRResult(bmp))
                storeImage(bmp)
            }
            return imgWarped
        }else {
            //System.out.println(biggest);
            imgWarped=frame2
            photobtn.setOnClickListener { view ->
                Toast.makeText(this, "Wait for red rectangle", Toast.LENGTH_SHORT).show()
            }

            return imgWarped;
        }


        //return frame1
    }

    fun rotateBitmap(source: Bitmap, angle: Float): Bitmap? {
        val matrix = Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
    }



    fun converMat2Bitmat(img: Mat): Bitmap? {
        val width = img.width()
        val hight = img.height()
        val bmp: Bitmap
        bmp = Bitmap.createBitmap(width, hight, Bitmap.Config.ARGB_8888)
        val tmp: Mat
        tmp = if (img.channels() == 1) Mat(width, hight, CvType.CV_8UC1, Scalar(1.0)) else Mat(
                width, hight, CvType.CV_8UC3, Scalar(
                3.0
        )
        )
        try {
            if (img.channels() == 3) Imgproc.cvtColor(
                    img,
                    tmp,
                    Imgproc.COLOR_RGB2BGRA
            ) else if (img.channels() == 1) Imgproc.cvtColor(
                    img,
                    tmp,
                    Imgproc.COLOR_GRAY2RGBA
            )
            Utils.matToBitmap(img, bmp)
        } catch (e: CvException) {
            e.message?.let { Log.d("Expection", it) }
        }
        return bmp
    }

    fun save(bmp: Bitmap){
        try {
            val fileOutputStream: FileOutputStream = openFileOutput(
                    "photo.png",
                    Context.MODE_PRIVATE
            )
            bmp.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)
            Log.d("TEST", "SAVE")
            fileOutputStream.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun storeImage(image: Bitmap) {
        val pictureFile = getOutputMediaFile()

        Log.d(
                "TAG",
                "f "
        )
        if (pictureFile == null) {
            Log.d(
                    "TAG",
                    "Error creating media file, check storage permissions: "
            ) // e.getMessage());
            return
        }
        try {
            val fos = FileOutputStream(pictureFile)
            image.compress(Bitmap.CompressFormat.PNG, 90, fos)
            fos.close()
        } catch (e: FileNotFoundException) {
            Log.d("TAG", "File not found: ")
        } catch (e: IOException) {
            Log.d("TAG", "Error accessing file: ")
        }
    }




    private fun getOutputMediaFile(): File? {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        val mediaStorageDir = File(
                Environment.getExternalStorageDirectory()
                        .toString() + "/Android/data/"
                        + applicationContext.packageName
                        + "/Files"
        )

        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null
            }
        }
        // Create a media file name
        val mediaFile: File
        val mImageName = "photo.jpg"
        mediaFile = File(mediaStorageDir.path + File.separator + mImageName)
        return mediaFile
    }


    override fun onPause() {
        super.onPause()
        if (cameraBridgeViewBase != null) {
            cameraBridgeViewBase!!.disableView()
        }
    }



    override fun onDestroy() {
        super.onDestroy()
        if (cameraBridgeViewBase != null) {
            cameraBridgeViewBase!!.disableView()
        }
    }

    override fun onResume() {
        super.onResume()

        if (!OpenCVLoader.initDebug()) {
            Toast.makeText(applicationContext, "There's a problem, yo!", Toast.LENGTH_SHORT).show()
        } else {
            baseLoaderCallback!!.onManagerConnected(LoaderCallbackInterface.SUCCESS)
        }


    }


    fun preProcessing(pre_processed_image: Mat): Mat {
        Imgproc.cvtColor(pre_processed_image, pre_processed_image, 6)
        Imgproc.GaussianBlur(pre_processed_image, pre_processed_image, Size(5.0, 5.0), 1.0)
        Imgproc.Canny(pre_processed_image, pre_processed_image, 200.0, 200.0)
        Imgproc.dilate(
                pre_processed_image, pre_processed_image, Imgproc.getStructuringElement(
                1, Size(
                5.0,
                5.0
        )
        ), Point(-1.0, -1.0), 2
        )
        Imgproc.erode(
                pre_processed_image, pre_processed_image, Imgproc.getStructuringElement(
                1, Size(
                5.0,
                5.0
        )
        ), Point(-1.0, -1.0), 1
        )
        return pre_processed_image
    }



    fun getContours(processed: Mat?, raw: Mat): MatOfPoint? {
        val contours: List<MatOfPoint> = ArrayList()
        val bb = MatOfPoint()
        val contours2f = MatOfPoint2f()
        val aprox = MatOfPoint2f()
        var biggest = MatOfPoint2f()
        val gg: MutableList<MatOfPoint> = ArrayList()
        val hierarchy = Mat()
        val color = Scalar(255.toDouble(), 0.toDouble(), 0.toDouble())
        var peri: Double
        var maxArea = 0.0
        var objCor: Long
        var rect: Rect
        Imgproc.findContours(
                processed,
                contours,
                hierarchy,
                Imgproc.RETR_EXTERNAL,
                Imgproc.CHAIN_APPROX_NONE
        )
        for (i in contours.indices) {
            val cont_area = Imgproc.contourArea(contours[i])
            contours[i].convertTo(contours2f, CvType.CV_32F)
            if (cont_area > 5000) {
                peri = Imgproc.arcLength(contours2f, true)
                Imgproc.approxPolyDP(contours2f, aprox, 0.1 * peri, true)
                if (cont_area > maxArea && aprox.total() == 4L) {
                    //System.out.println(aprox);
                    biggest = aprox
                    maxArea = cont_area
                    biggest.convertTo(bb, CvType.CV_32S)
                    gg.add(bb)
                    Imgproc.drawContours(raw, gg, 0, color, 3)
                }
            }
        }
        return bb
    }


    fun getWarp(img: Mat, approx: MatOfPoint): Mat? {
        println(approx.toList())


        //calculate the center of mass of our contour image using moments
        val moment = Imgproc.moments(approx)
        val x = (moment._m10 / moment._m00).toInt()
        val y = (moment._m01 / moment._m00).toInt()

        //SORT POINTS RELATIVE TO CENTER OF MASS
        val sortedPoints = arrayOfNulls<Point>(4)
        var data: DoubleArray
        var count = 0
        for (i in 0 until approx.rows()) {
            data = approx[i, 0]
            val datax = data[0]
            val datay = data[1]
            if (datax < x && datay < y) {
                println("1")
                sortedPoints[0] = Point(datax, datay)
                count++
            } else if (datax > x && datay < y) {
                println("2")
                sortedPoints[1] = Point(datax, datay)
                count++
            } else if (datax < x && datay > y) {
                println("3")
                sortedPoints[2] = Point(datax, datay)
                count++
            } else if (datax > x && datay > y) {
                println("4")
                sortedPoints[3] = Point(datax, datay)
                count++
            }
        }
        for (i in 0..3) {
            if (sortedPoints[i] == null) {
                return img
            }
        }
        val src = MatOfPoint2f(
                sortedPoints[0],
                sortedPoints[1],
                sortedPoints[2],
                sortedPoints[3]
        )
        println("dst")
        val dst = MatOfPoint2f(
                Point(0.0, 0.0),
                Point(img.width().toDouble(), 0.0),
                Point(0.0, img.height().toDouble()),
                Point(img.width().toDouble(), img.height().toDouble())
        )
        val warpMat = Imgproc.getPerspectiveTransform(src, dst)
        val destImage = Mat()
        Imgproc.warpPerspective(img, destImage, warpMat, img.size())
        println(src.toList())
        return destImage
    }


    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
    ) {

        when (requestCode) {
            1 -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    cameraBridgeViewBase!!.setCameraPermissionGranted() // <------ THIS!!!
                } else {
                    // permission denied
                }
                return
            }
        }
    }

}