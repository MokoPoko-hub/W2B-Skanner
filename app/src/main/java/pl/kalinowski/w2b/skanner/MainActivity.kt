package pl.kalinowski.w2b.skanner

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.SurfaceView
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import org.opencv.android.*
import org.opencv.core.*
import org.opencv.imgproc.Imgproc
import kotlin.jvm.internal.Intrinsics


class MainActivity : AppCompatActivity(), CameraBridgeViewBase.CvCameraViewListener2 {

    var cameraBridgeViewBase: CameraBridgeViewBase? = null
    var baseLoaderCallback: BaseLoaderCallback? = null
    var startCanny = false


    fun Canny(Button: View?) {
        startCanny = startCanny == false
    }




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



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
        Intrinsics.checkNotNullParameter(inputFrame, "inputFrame")
        val frame = inputFrame!!.rgba()
        val frame1 = frame.clone()
        if (startCanny) {
            val preProcessing = preProcessing(frame)
            Intrinsics.checkNotNullExpressionValue(frame1, "frame1")
            getContours(preProcessing, frame1)
            return frame1
        }
        val preProcessing2 = preProcessing(frame)
        Intrinsics.checkNotNull(preProcessing2)
        return preProcessing2

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
        Imgproc.dilate(pre_processed_image, pre_processed_image, Imgproc.getStructuringElement(1, Size(5.0, 5.0)), Point(-1.0, -1.0), 2)
        Imgproc.erode(pre_processed_image, pre_processed_image, Imgproc.getStructuringElement(1, Size(5.0, 5.0)), Point(-1.0, -1.0), 1)
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
        Imgproc.findContours(processed, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_NONE)
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


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {

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