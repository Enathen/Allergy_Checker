package creative.endless.growing.allergychecker

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.SurfaceHolder
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.text.TextBlock
import com.google.android.gms.vision.text.TextRecognizer
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_scanner.*
import java.util.ArrayList
import java.util.HashMap

class ScannerFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_scanner, container, false)

    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        var analyzeText = AnalyzeText(this, context?.let { Country.getLocale(it) })
        undo.setOnClickListener {
            allergies.removeAllViews()
            analyzeText.clear()
        }
        Country.setCorrectFlag(country, context!!)
        country.setOnClickListener {
            val dialog = Dialog(context!!)
            dialog.setContentView(R.layout.dialog_country)
            dialog.findViewById<LinearLayout>(R.id.sv).setOnClickListener {
                dialog.dismiss()
                androidx.preference.PreferenceManager.getDefaultSharedPreferences(context).edit().putInt(Country.country, Country.sv).apply()
                Country.setCorrectFlag(country, context!!)
                analyzeText = AnalyzeText(this, context?.let { Country.getLocale(it) })
            }
            dialog.findViewById<LinearLayout>(R.id.en).setOnClickListener {
                dialog.dismiss()
                androidx.preference.PreferenceManager.getDefaultSharedPreferences(context).edit().putInt(Country.country, Country.en).apply()
                Country.setCorrectFlag(country, context!!)
                analyzeText = AnalyzeText(this, context?.let { Country.getLocale(it) })
            }
            dialog.show()
        }
        val textRecognizer = TextRecognizer.Builder(context).build()

        val mCameraSource = CameraSource.Builder(context, textRecognizer)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setRequestedPreviewSize(1280, 1024)
                .setAutoFocusEnabled(true)
                .setRequestedFps(120.0f)
                .build()

        preview.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceChanged(p0: SurfaceHolder?, p1: Int, p2: Int, p3: Int) {
            }

            override fun surfaceDestroyed(p0: SurfaceHolder?) {
                mCameraSource.stop()
            }

            @SuppressLint("MissingPermission")
            override fun surfaceCreated(p0: SurfaceHolder?) {
                mCameraSource.start(preview.holder)
            }
        })
        if (!textRecognizer.isOperational()) {
            Log.w("Main", "Detector dependencies are not yet available.")

            // Check for low storage.  If there is low storage, the native library will not be
            // downloaded, so detection will not become operational.
            val lowstorageFilter = IntentFilter(Intent.ACTION_DEVICE_STORAGE_LOW)
            val hasLowStorage = activity!!.registerReceiver(null, lowstorageFilter) != null

            if (hasLowStorage) {
                Toast.makeText(context, getString(R.string.low_storage_error), Toast.LENGTH_LONG).show()
                Log.w("Main", getString(R.string.low_storage_error))
            }
        }
        textRecognizer.setProcessor(object : Detector.Processor<TextBlock> {

            override fun release() {


            }

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
        super.onViewCreated(view, savedInstanceState)

    }


    private fun isCameraPermissionGranted(): Boolean {
        if (ContextCompat.checkSelfPermission(
                        context!!,
                        Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    fun insertData(
        allFoundAllergies: HashMap<String, AllergiesClass>,
        allFoundENumbers: ArrayList<AllergyList.E_Numbers>
    ) {
        if(allergies == null){
            return
        }
        allergies.removeAllViews()
        allFoundAllergies["test"]?.amount
        for (allergyClass in allFoundAllergies.values) {
            val inflate = layoutInflater.inflate(R.layout.allergy_list_view, container, false)
            val name = inflate.findViewById<TextView>(R.id.name)
            val amount = inflate.findViewById<TextView>(R.id.amount)
            val amounts = allergyClass.amount
            name.text = allergyClass.motherAllergy
            amount.text = amounts.toString()
            allergies.addView(inflate)
        }
        for (allFoundENumber in allFoundENumbers) {
            val inflate = layoutInflater.inflate(R.layout.e_number_list_view, container, false)
            val name = inflate.findViewById<TextView>(R.id.name)
            name.text = allFoundENumber.url
            allergies.addView(inflate)
        }
    }

    companion object {
        fun newInstance(
        ): ScannerFragment = ScannerFragment()

    }
}
