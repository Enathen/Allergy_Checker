package creative.endless.growing.allergychecker

import android.Manifest
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.Random


class MainActivity : AppCompatActivity() {

    // Intent request code to handle updating play services if needed.
    private val RC_HANDLE_GMS = 9001

    // Permission request codes need to be < 256
    private val RC_HANDLE_CAMERA_PERM = 2

    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {

            R.id.scan -> {

                checkPermission()
                return@OnNavigationItemSelectedListener true
            }
            R.id.allergies -> {

                replaceFragment(MyAllergiesNew.getInstance(), R.id.featureContainer)
                return@OnNavigationItemSelectedListener true
            }
           /* R.id.settings -> {
                replaceFragment(SettingsFragment.newInstance(),R.id.featureContainer)
                return@OnNavigationItemSelectedListener true
            }*/
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        navView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)
        // Check for the camera permission before accessing the camera.  If the
        // permission is not granted yet, request permission.
        checkPermission()




    }

    private fun checkPermission() {
        val rc = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
        if (rc == PackageManager.PERMISSION_GRANTED) {

            replaceFragment(ScannerFragment.newInstance(), R.id.featureContainer)

        } else {
            requestCameraPermission()
        }
    }

    /**
     * Handles the requesting of the camera permission.  This includes
     * showing a "Snackbar" message of why the permission is needed then
     * sending the request.
     */
    private fun requestCameraPermission() {

        val permissions = arrayOf(Manifest.permission.CAMERA)

        if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions(this, permissions, RC_HANDLE_CAMERA_PERM)
            return
        }

        val thisActivity = this


        val listener = DialogInterface.OnClickListener { dialog, id ->
            ActivityCompat.requestPermissions(thisActivity, permissions, RC_HANDLE_CAMERA_PERM)
            return@OnClickListener
        }
        AlertDialog.Builder(this, AlertDialog.THEME_HOLO_DARK)
            .setTitle(R.string.varning)
            .setMessage(R.string.thisAppOnlySave)
            .setPositiveButton(R.string.ok, listener)
            .show()

    }

    inline fun FragmentManager.inTransaction(func: FragmentTransaction.() -> Unit) {
        val fragmentTransaction = beginTransaction()
        fragmentTransaction.func()
        fragmentTransaction.commit()
    }

    fun AppCompatActivity.addFragment(fragment: Fragment, frameId: Int) {
        supportFragmentManager.inTransaction { add(frameId, fragment) }
    }

    /**
     * Callback for the result from requesting permissions. This method
     * is invoked for every call on [.requestPermissions].
     *
     *
     * **Note:** It is possible that the permissions request interaction
     * with the user is interrupted. In this case you will receive empty permissions
     * and results arrays which should be treated as a cancellation.
     *
     *
     * @param requestCode  The request code passed in [.requestPermissions].
     * @param permissions  The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     * which is either [PackageManager.PERMISSION_GRANTED]
     * or [PackageManager.PERMISSION_DENIED]. Never null.
     * @see .requestPermissions
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        @NonNull permissions: Array<String>,
        @NonNull grantResults: IntArray
    ) {
        if (requestCode != RC_HANDLE_CAMERA_PERM) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            return
        }

        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // We have permission, so create the camerasource
            replaceFragment(ScannerFragment.newInstance(), R.id.featureContainer)
            return
        }


        val listener = DialogInterface.OnClickListener { dialog, id -> finish() }

        val builder = AlertDialog.Builder(this, AlertDialog.THEME_HOLO_DARK)
        builder.setTitle(R.string.cannotUseCamera)
            .setMessage(R.string.no_camera_permission)
            .setPositiveButton(R.string.ok, listener)
            .show()
    }

    fun AppCompatActivity.replaceFragment(fragment: Fragment, frameId: Int) {
        supportFragmentManager.inTransaction { replace(frameId, fragment) }
    }


}
