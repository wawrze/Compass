package pl.wawra.compass.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationServices
import pl.wawra.compass.R
import pl.wawra.compass.di.components.Injector
import pl.wawra.compass.di.modules.DatabaseModule

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Injector.injector
            .databaseModule(DatabaseModule(this))
        setContentView(R.layout.activity_main)

        initGoogleClient()
    }

    private fun initGoogleClient() {
        val apiAvailability = GoogleApiAvailability.getInstance()
        val resultCode = apiAvailability.isGooglePlayServicesAvailable(this)
        if (resultCode == ConnectionResult.SUCCESS) {
            GoogleApiClient.Builder(this).addApi(LocationServices.API).build()
        }
    }

}