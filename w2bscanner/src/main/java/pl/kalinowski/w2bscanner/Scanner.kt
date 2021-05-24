package pl.kalinowski.w2bscanner

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.opencv.android.CameraBridgeViewBase
import org.opencv.core.Mat
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*

class Scanner @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {



    init {
        LayoutInflater.from(context).inflate(R.layout.scanner, this, true)
        attrs?.let {
            val styledAttributes = context.obtainStyledAttributes(it, R.styleable.Scanner, 0, 0)
            val tintColor = styledAttributes.getResourceId(R.styleable.Scanner_tint, 0xFF808080.toInt())





            styledAttributes.recycle()
        }


    }


    fun setTintColor(@ColorRes primaryColorID: Int) {
        val floatButton = findViewById<FloatingActionButton>(R.id.canny)
        floatButton.backgroundTintList = ContextCompat.getColorStateList(context, primaryColorID)

    }




}