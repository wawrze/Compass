package pl.wawra.compass.presentation.compass

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorManager
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import pl.wawra.compass.database.daos.LocationDao
import pl.wawra.compass.database.entities.Location
import javax.inject.Inject
import kotlin.math.*

class CompassViewModel : ViewModel() {

    @Inject
    lateinit var locationDao: LocationDao

    val compassRotation = MutableLiveData<RotationModel>()
    val targetMarkerRotation = MutableLiveData<RotationModel>()

    private var targetLocation = Location(53.130014, 23.144562)
    private val lastLocation = Location(0.0, 0.0)
    private var lastLocationUpdate: Long = 0L

    private var lastCompassDegree: Float = 0.0F
    private var lastTargetMarkerDegree: Float = 0.0F
    private var lastCompassUpdate: Long = 0L
    private var lastAnimationLength: Long = 0L

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

        val targetDegreeFromNorth = calculateTargetDegree(
            targetLocation.lat,
            targetLocation.lon,
            lastLocation.lat,
            lastLocation.lon
        )
        toDegree = (calculatedDegree + targetDegreeFromNorth) % 360
        lastTargetMarkerDegree = toDegree % 360
        targetMarkerRotation.postValue(RotationModel(lastTargetMarkerDegree, toDegree, animationLength))
    }

    private fun calculateTargetDegree(
        targetLat: Double,
        targetLon: Double,
        currentLat: Double,
        currentLon: Double
    ): Float {
        val targetY = ((targetLat - currentLat) * 10000000).toLong().toDouble()
        if (targetY == 0.0) return 0F
        val targetX = ((targetLon - currentLon) * 10000000).toLong().toDouble()

        var degrees = (radiansToDegrees(atan(targetX / targetY)) + 360) % 360
        if (targetY < 0) degrees += (if (targetX < 0) 180 else -180)

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

    fun updateLocation(latitude: Double, longitude: Double) {
        val timestamp = System.currentTimeMillis()
        if (lastLocationUpdate != 0L && timestamp - lastLocationUpdate < 5000) return

        val distanceFromLastLocation =
            calculateDistance(latitude, longitude, lastLocation.lat, lastLocation.lon)
        if (distanceFromLastLocation < 5) return

        lastLocationUpdate = timestamp
        lastLocation.lat = latitude
        lastLocation.lon = longitude
    }

    private fun calculateDistance(
        lat1: Double,
        lon1: Double,
        lat2: Double,
        lon2: Double
    ): Int {
        val theta = lon1 - lon2
        var distance = sin(degreesToRadians(lat1)) * sin(degreesToRadians(lat2)) + cos(degreesToRadians(lat1)) * cos(degreesToRadians(lat2)) * cos(degreesToRadians(theta))
        distance = acos(distance)
        distance = radiansToDegrees(distance)
        distance *= 0.1112
        return distance.toInt()
    }

    private fun degreesToRadians(deg: Double): Double {
        return deg * Math.PI / 180.0
    }

    private fun radiansToDegrees(rad: Double): Double {
        return rad * 180.0 / Math.PI
    }

}