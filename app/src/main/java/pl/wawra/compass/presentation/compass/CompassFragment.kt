package pl.wawra.compass.presentation.compass

import android.content.Context.SENSOR_SERVICE
import android.hardware.Sensor
import android.hardware.Sensor.TYPE_ACCELEROMETER
import android.hardware.Sensor.TYPE_MAGNETIC_FIELD
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.hardware.SensorManager.SENSOR_DELAY_NORMAL
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation.RELATIVE_TO_SELF
import android.view.animation.RotateAnimation
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.fragment_compass.*
import pl.wawra.compass.R

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
        setupClickListeners()
    }

    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(this, accelerometer, SENSOR_DELAY_NORMAL)
        sensorManager.registerListener(this, magneticField, SENSOR_DELAY_NORMAL)
    }

    private fun setupObservers() {
        viewModel.compassRotation.observe(
            viewLifecycleOwner,
            Observer { rotateImage(fragment_compass_compass_image, it) }
        )
    }

    private fun setupClickListeners() {
        // TODO
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

}