package pl.wawra.compass.presentation

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationServices
import pl.wawra.compass.App
import pl.wawra.compass.R
import pl.wawra.compass.presentation.targetDialog.TargetDialogListener

class MainActivity : AppCompatActivity(), TargetDialogListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (application as App).appComponent?.inject(this)
        setContentView(R.layout.activity_main)

        checkPermission()
        initGoogleClient()
    }

    private fun initGoogleClient() {
        val apiAvailability = GoogleApiAvailability.getInstance()
        val resultCode = apiAvailability.isGooglePlayServicesAvailable(this)
        if (resultCode == ConnectionResult.SUCCESS) {
            GoogleApiClient.Builder(this).addApi(LocationServices.API).build()
        }
    }

    private fun checkPermission() {
        if (
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
            || ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
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

    companion object {
        private const val PERMISSIONS_REQUEST = 777
    }

    var targetDialogListener: TargetDialogListener? = null
    override fun onNewLongitude() {
        targetDialogListener?.onNewLongitude()
    }

}