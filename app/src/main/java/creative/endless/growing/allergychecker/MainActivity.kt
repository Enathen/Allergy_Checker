package creative.endless.growing.allergychecker

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.media.Image
import android.os.Bundle
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.text.TextBlock
import com.google.android.gms.vision.text.TextRecognizer
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*


class MainActivity : AppCompatActivity() {

    private lateinit var scannerContainer: FrameLayout
    private lateinit var linLayAllergies: LinearLayout

    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.scan -> {
                return@OnNavigationItemSelectedListener true
            }
            R.id.allergies -> {
                return@OnNavigationItemSelectedListener true
            }
            R.id.settings -> {
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        val undo = findViewById<ImageView>(R.id.undo)
        linLayAllergies = findViewById(R.id.allergies)

        navView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)
        val analyzeText = AnalyzeText(this@MainActivity)
        undo.setOnClickListener {
            linLayAllergies.removeAllViews()
            analyzeText.clear()
        }
        val textRecognizer = TextRecognizer.Builder(this).build()

        val mCameraSource = CameraSource.Builder(this, textRecognizer)
            .setFacing(CameraSource.CAMERA_FACING_BACK)
            .setRequestedPreviewSize(1280, 1024)
            .setAutoFocusEnabled(true)
            .setRequestedFps(120.0f)
            .build()

        val preview = findViewById<SurfaceView>(R.id.preview)
        preview.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceChanged(p0: SurfaceHolder?, p1: Int, p2: Int, p3: Int) {
            }

            override fun surfaceDestroyed(p0: SurfaceHolder?) {
                mCameraSource.stop()
            }

            @SuppressLint("MissingPermission")
            override fun surfaceCreated(p0: SurfaceHolder?) {

                if (isCameraPermissionGranted()) {
                    mCameraSource.start(preview.holder)
                } else {

                    ActivityCompat.requestPermissions(
                        this@MainActivity,
                        arrayOf(Manifest.permission.CAMERA),
                        1
                    )
                }

            }
        })
        textRecognizer.setProcessor(object : Detector.Processor<TextBlock> {
            override fun release() {}

            override fun receiveDetections(detections: Detector.Detections<TextBlock>) {
                val items = detections.detectedItems

                if (items.size() <= 0) {
                    return
                }
                val stringBuilder = StringBuilder()
                for (i in 0 until items.size()) {
                    val item = items.valueAt(i)
                    stringBuilder.append(item.value)
                }
                analyzeText.analyzeString(stringBuilder.toString())
            }
        })
    }

    inline fun FragmentManager.inTransaction(func: FragmentTransaction.() -> Unit) {
        val fragmentTransaction = beginTransaction()
        fragmentTransaction.func()
        fragmentTransaction.commit()
    }

    fun AppCompatActivity.addFragment(fragment: Fragment, frameId: Int) {
        supportFragmentManager.inTransaction { add(frameId, fragment) }
    }


    fun AppCompatActivity.replaceFragment(fragment: Fragment, frameId: Int) {
        supportFragmentManager.inTransaction { replace(frameId, fragment) }
    }

    private fun isCameraPermissionGranted(): Boolean {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    fun insertData(
            allFoundAllergies: HashMap<String, Int>,
            allFoundENumbers: ArrayList<AllergyList.E_Numbers>
    ) {

        linLayAllergies.removeAllViews()
        for (allergy in allFoundAllergies.keys) {
            val inflate = layoutInflater.inflate(R.layout.allergy_list_view, container, false)
            val name = inflate.findViewById<TextView>(R.id.name)
            val amount = inflate.findViewById<TextView>(R.id.amount)
            val amounts = allFoundAllergies.get(allergy)
            name.text = allergy
            amount.text = amounts.toString()
            linLayAllergies.addView(inflate)
        }
    }

}
