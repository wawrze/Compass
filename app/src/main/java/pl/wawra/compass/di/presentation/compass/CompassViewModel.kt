package pl.wawra.compass.di.presentation.compass

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorManager
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import pl.wawra.compass.database.daos.LocationDao
import javax.inject.Inject

class CompassViewModel : ViewModel() {

    @Inject
    lateinit var locationDao: LocationDao

    val degreeChange = MutableLiveData<Pair<Float, Float>>()

    private var lastDegree: Float = 0.0F
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

    fun updateDegree(degree: Float? = null) {
        if (degree == null) {
            degreeChange.postValue(Pair(0.0F, lastDegree))
        } else {
            val calculatedDegree = (Math.toDegrees(degree.toDouble()) + 360).toFloat() % 360
            degreeChange.postValue(Pair(lastDegree, calculatedDegree))
            lastDegree = -calculatedDegree
        }
    }

    fun handleSensorEvent(event: SensorEvent) {
        when (event.sensor.type) {
            Sensor.TYPE_ACCELEROMETER -> accelerometerValues?.let {
                it[0] = (it[0] + event.values[0]) / 2
                it[1] = (it[1] + event.values[1]) / 2
                it[2] = (it[2] + event.values[2]) / 2
            }
            Sensor.TYPE_MAGNETIC_FIELD -> magneticFieldValues?.let {
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
                updateDegree(orientation[0])
            }
        }
    }

}