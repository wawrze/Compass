package pl.wawra.compass.presentation.compass

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorManager
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import pl.wawra.compass.database.daos.LocationDao
import javax.inject.Inject
import kotlin.math.abs

class CompassViewModel : ViewModel() {

    @Inject
    lateinit var locationDao: LocationDao

    val compassRotation = MutableLiveData<RotationModel>()

    private var lastCompassDegree: Float = 0.0F
    private var lastCompassUpdate = 0L
    private var lastAnimationLength = 0L

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

    private fun updateCompassRotation(newDegree: Float) {
        val timestamp = System.currentTimeMillis()
        if (lastCompassUpdate != 0L && timestamp - lastCompassUpdate < lastAnimationLength) return
        lastCompassUpdate = timestamp

        val calculatedDegree = (-Math.toDegrees(newDegree.toDouble()) + 360).toFloat() % 360

        val change = abs(calculatedDegree - lastCompassDegree)
        if (change < 2.0) return

        var toDegree = calculatedDegree
        var fromDegree = lastCompassDegree
        if (change > 180) {
            if (lastCompassDegree > calculatedDegree) {
                toDegree += 360
            } else {
                fromDegree += 360
            }
        }

        var animationLength = abs(toDegree - fromDegree).toLong() * 10
        if (animationLength < 100) animationLength = 100

        compassRotation.postValue(RotationModel(fromDegree, toDegree, animationLength))

        lastCompassDegree = calculatedDegree
        lastAnimationLength = animationLength
    }

    fun handleSensorEvent(event: SensorEvent) {
        when (event.sensor.type) {
            Sensor.TYPE_ACCELEROMETER -> accelerometerValues?.let {
                for (i: Int in 0..2) it[i] = (it[i] + event.values[i]) / 2
            }
            Sensor.TYPE_MAGNETIC_FIELD -> magneticFieldValues?.let {
                for (i: Int in 0..2) it[i] = (it[i] + event.values[i]) / 2
            }
            else -> return
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
                updateCompassRotation(orientation[0])
            }
        }
    }

}