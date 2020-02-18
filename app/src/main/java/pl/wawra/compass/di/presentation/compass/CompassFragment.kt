package pl.wawra.compass.di.presentation.compass

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

    private var accelerometerValues: FloatArray? = null
        get() {
            if (field == null) field = FloatArray(3)
            return field
        }
    private var magneticFieldValues: FloatArray? = null
        get() {
            if (field == null) field = FloatArray(3)
            return field
        }

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
        viewModel.updateDegree()
    }

    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(this, accelerometer, SENSOR_DELAY_NORMAL)
        sensorManager.registerListener(this, magneticField, SENSOR_DELAY_NORMAL)
    }

    private fun setupObservers() {
        viewModel.degreeChange.observe(
            viewLifecycleOwner,
            Observer { rotateImage(it.first, it.second) }
        )
    }

    private fun setupClickListeners() {

    }

    private fun rotateImage(previousDegree: Float, degree: Float) {
        fragment_compass_compass_image.startAnimation(
            RotateAnimation(
                previousDegree,
                degree,
                RELATIVE_TO_SELF,
                0.5f,
                RELATIVE_TO_SELF,
                0.5f
            ).apply {
                duration = 2000
                fillAfter = true
            }
        )
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // do nothing
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event ?: return

        when (event.sensor.type) {
             TYPE_ACCELEROMETER -> accelerometerValues?.let {
                it[0] = (it[0] + event.values[0]) / 2
                it[1] = (it[1] + event.values[1]) / 2
                it[2] = (it[2] + event.values[2]) / 2
            }
            event.sensor.type -> magneticFieldValues?.let {
                it[0] = (it[0] + event.values[0]) / 2
                it[1] = (it[1] + event.values[1]) / 2
                it[2] = (it[2] + event.values[2]) / 2
            }
        }

        if (accelerometerValues != null && magneticFieldValues != null) {
            val rotation = FloatArray(9)
            val isRotation = SensorManager.getRotationMatrix(
                rotation,
                null,
                accelerometerValues,
                magneticFieldValues
            )
            if (isRotation) {
                val orientation = FloatArray(3)
                SensorManager.getOrientation(rotation, orientation)
                viewModel.updateDegree(orientation[0])
            }
        }
    }

}