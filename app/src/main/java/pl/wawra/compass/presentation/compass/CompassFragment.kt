package pl.wawra.compass.presentation.compass

import android.annotation.SuppressLint
import android.content.Context.SENSOR_SERVICE
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
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import kotlinx.android.synthetic.main.fragment_compass.*
import pl.wawra.compass.R
import pl.wawra.compass.base.BaseFragment
import pl.wawra.compass.base.ViewModelProviderFactory
import pl.wawra.compass.models.RotationModel
import pl.wawra.compass.presentation.MainActivity
import pl.wawra.compass.presentation.targetDialog.TargetDialogListener
import javax.inject.Inject

class CompassFragment : BaseFragment(), SensorEventListener, TargetDialogListener {

    @Inject
    lateinit var viewModelFactory: ViewModelProviderFactory
    private lateinit var viewModel: CompassViewModel
    private lateinit var sensorManager: SensorManager
    private lateinit var accelerometer: Sensor
    private lateinit var magneticField: Sensor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupSensors()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this, viewModelFactory).get(CompassViewModel::class.java)
        return inflater.inflate(R.layout.fragment_compass, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()
        (activity as MainActivity).targetDialogListener = this
    }

    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(this, accelerometer, SENSOR_DELAY_NORMAL)
        sensorManager.registerListener(this, magneticField, SENSOR_DELAY_NORMAL)
        viewModel.updateTargetLocation()
        setupTargetMarker()
    }

    private fun setupSensors() {
        sensorManager = activity?.getSystemService(SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(TYPE_ACCELEROMETER)
        magneticField = sensorManager.getDefaultSensor(TYPE_MAGNETIC_FIELD)
        if (!::accelerometer.isInitialized || !::magneticField.isInitialized) {
            navigate?.navigate(R.id.to_no_sensors_dialog)
        }
    }

    private fun setupObservers() {
        viewModel.compassRotation.observe { rotateImage(fragment_compass_compass_image, it) }
        viewModel.targetMarkerRotation.observe { rotateImage(fragment_compass_target_image, it) }
        viewModel.targetLocationString.observe {
            if (it.isEmpty()) {
                changeTargetVisibility(false)
            } else {
                fragment_compass_current_target.text = it
                setupTargetMarker()
            }
        }
        viewModel.targetAddressString.observe { fragment_compass_current_target_address.text = it }
    }

    private fun changeTargetVisibility(isVisible: Boolean) {
        if (isVisible) {
            fragment_compass_current_target_label.visibility = View.VISIBLE
            fragment_compass_current_target.visibility = View.VISIBLE
            fragment_compass_current_target_address.visibility = View.VISIBLE
            fragment_compass_target_image.visibility = View.VISIBLE
        } else {
            fragment_compass_current_target_label.visibility = View.INVISIBLE
            fragment_compass_current_target.visibility = View.INVISIBLE
            fragment_compass_current_target_address.visibility = View.INVISIBLE
            fragment_compass_target_image.visibility = View.GONE
        }
    }

    private fun setupTargetMarker() {
        context?.let {
            if (MainActivity.checkArePermissionsGranted(it)) {
                setupTargetMarkerAndButtons(enabled = true)
                getLocations()
            } else {
                setupTargetMarkerAndButtons(enabled = false)
            }
        }
    }

    private fun setupTargetMarkerAndButtons(enabled: Boolean) {
        if (enabled) {
            changeTargetVisibility(true)
            fragment_compass_target_button.setOnClickListener {
                navigate?.navigate(R.id.to_target_dialog)
            }
        } else {
            changeTargetVisibility(false)
            fragment_compass_target_button.setOnClickListener {
                context?.showToast(R.string.have_to_grant_permission)
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

    @SuppressLint("MissingPermission", "checked before function called")
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

    override fun onNewLongitude() {
        viewModel.updateTargetLocation()
    }

}