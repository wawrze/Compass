package pl.wawra.compass.presentation.compass

import android.Manifest
import android.content.Context.SENSOR_SERVICE
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.Sensor.TYPE_ACCELEROMETER
import android.hardware.Sensor.TYPE_MAGNETIC_FIELD
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.hardware.SensorManager.SENSOR_DELAY_NORMAL
import android.os.Bundle
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation.RELATIVE_TO_SELF
import android.view.animation.RotateAnimation
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import kotlinx.android.synthetic.main.fragment_compass.*
import pl.wawra.compass.R
import pl.wawra.compass.models.RotationModel
import pl.wawra.compass.presentation.latitudeDialog.LatitudeDialog

class CompassFragment : Fragment(), SensorEventListener {

    private lateinit var viewModel: CompassViewModel
    private lateinit var sensorManager: SensorManager
    private lateinit var accelerometer: Sensor
    private lateinit var magneticField: Sensor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sensorManager = activity?.getSystemService(SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(TYPE_ACCELEROMETER)
        magneticField = sensorManager.getDefaultSensor(TYPE_MAGNETIC_FIELD)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this).get(CompassViewModel::class.java)
        return inflater.inflate(R.layout.fragment_compass, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()
    }

    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(this, accelerometer, SENSOR_DELAY_NORMAL)
        sensorManager.registerListener(this, magneticField, SENSOR_DELAY_NORMAL)
        setupTargetMarker()
    }

    private fun setupObservers() {
        viewModel.compassRotation.observe(
            viewLifecycleOwner,
            Observer { rotateImage(fragment_compass_compass_image, it) }
        )
        viewModel.targetMarkerRotation.observe(
            viewLifecycleOwner,
            Observer { rotateImage(fragment_compass_target_image, it) }
        )
    }

    private fun setupTargetMarker() {
        context?.let {
            if (
                ActivityCompat.checkSelfPermission(
                    it,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(
                    it,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                setupTargetMarkerAndButtons(enabled = true)
                getLocations()
            } else {
                setupTargetMarkerAndButtons(enabled = false)
            }
        }
    }

    private fun setupTargetMarkerAndButtons(enabled: Boolean) {
        if (enabled) {
            fragment_compass_target_image.visibility = View.VISIBLE
            fragment_compass_latitude_button.setOnClickListener {
                LatitudeDialog.createAndShow(parentFragmentManager)
            }
            fragment_compass_longitude_button.setOnClickListener {
                // TODO: show longitude dialog
            }
        } else {
            fragment_compass_target_image.visibility = View.GONE
            fragment_compass_latitude_button.setOnClickListener {
                Toast.makeText(
                    context,
                    getString(R.string.have_to_grant_permission),
                    Toast.LENGTH_LONG
                ).show()
            }
            fragment_compass_longitude_button.setOnClickListener {
                Toast.makeText(
                    context,
                    getString(R.string.have_to_grant_permission),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun rotateImage(image: ImageView, rotation: RotationModel) {
        image.startAnimation(
            RotateAnimation(
                rotation.fromDegree,
                rotation.toDegree,
                RELATIVE_TO_SELF,
                0.5f,
                RELATIVE_TO_SELF,
                0.5f
            ).apply {
                duration = rotation.animationLength
                fillAfter = true
            }
        )
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // do nothing
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let { viewModel.handleSensorEvent(it) }
    }

    private fun getLocations() {
        context?.let {
            val locationRequest = LocationRequest().apply {
                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                interval = 5000
                fastestInterval = 4000
            }
            LocationServices.getFusedLocationProviderClient(it).requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        }
    }

    private val locationCallback = object : LocationCallback() {

        override fun onLocationResult(lr: LocationResult?) {
            super.onLocationResult(lr)
            lr?.lastLocation?.let { viewModel.updateLocation(it.latitude, it.longitude) }
        }

    }

}