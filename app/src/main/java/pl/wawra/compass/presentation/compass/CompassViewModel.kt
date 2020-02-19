package pl.wawra.compass.presentation.compass

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorManager
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import pl.wawra.compass.database.daos.LocationDao
import pl.wawra.compass.database.entities.Location
import javax.inject.Inject
import kotlin.math.abs
import kotlin.math.acos
import kotlin.math.sqrt

class CompassViewModel : ViewModel() {

    @Inject
    lateinit var locationDao: LocationDao

    val compassRotation = MutableLiveData<RotationModel>()
    val targetMarkerRotation = MutableLiveData<RotationModel>()

    private var targetLocation = Location(53.129618, 23.163289)
    var myLocation = Location(53.134828, 23.163153)

    private var lastCompassDegree: Float = 0.0F
    private var lastTargetMarkerDegree: Float = 0.0F
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

    private fun updateRotations(newDegree: Float) {
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

        lastCompassDegree = calculatedDegree
        lastAnimationLength = animationLength
        compassRotation.postValue(RotationModel(fromDegree, toDegree, animationLength))

        val targetDegreeFromNorth = calculateTargetDegree()
        toDegree = (calculatedDegree + targetDegreeFromNorth) % 360
        fromDegree = lastTargetMarkerDegree
        if (change > 180) {
            if (lastTargetMarkerDegree > calculatedDegree) {
                toDegree += 360
            } else {
                fromDegree += 360
            }
        }
        lastTargetMarkerDegree = toDegree % 360
        targetMarkerRotation.postValue(RotationModel(fromDegree, toDegree, animationLength))
    }

    private fun calculateTargetDegree(): Float {
        val targetX = ((targetLocation.lon - myLocation.lon) * 10000000).toLong().toDouble()
        val targetY = ((targetLocation.lat - myLocation.lat) * 10000000).toLong().toDouble()
        val northY = sqrt(targetX * targetX + targetY * targetY)

        val radians = acos(targetY / northY)

        var degrees = radians * 180.0 / Math.PI
        if (targetLocation.lon < myLocation.lon) degrees = -degrees + 360.0

        return degrees.toFloat()
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
                updateRotations(orientation[0])
            }
        }
    }

}