package pl.wawra.compass.presentation

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.navigation.findNavController
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationServices
import pl.wawra.compass.R
import pl.wawra.compass.base.Navigation
import pl.wawra.compass.presentation.targetDialog.TargetDialogListener

class MainActivity : AppCompatActivity(), TargetDialogListener, Navigation {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        askForPermissionIfNeeded()
        initGoogleClient()
    }

    private fun initGoogleClient() {
        val apiAvailability = GoogleApiAvailability.getInstance()
        val resultCode = apiAvailability.isGooglePlayServicesAvailable(this)
        if (resultCode == ConnectionResult.SUCCESS) {
            GoogleApiClient.Builder(this).addApi(LocationServices.API).build()
        }
    }

    private fun askForPermissionIfNeeded() {
        if (!checkArePermissionsGranted(this)) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ),
                PERMISSIONS_REQUEST
            )
        }
    }

    var targetDialogListener: TargetDialogListener? = null
    override fun onNewLongitude() {
        targetDialogListener?.onNewLongitude()
    }

    override fun getNavigationController() =
        findNavController(R.id.activity_main_fragment_container)

    companion object {

        private const val PERMISSIONS_REQUEST = 777

        fun checkArePermissionsGranted(context: Context): Boolean {
            val fineLocationPermission = ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            val coarseLocationPermission = ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
            return fineLocationPermission == PackageManager.PERMISSION_GRANTED
                    && coarseLocationPermission == PackageManager.PERMISSION_GRANTED
        }

    }

}