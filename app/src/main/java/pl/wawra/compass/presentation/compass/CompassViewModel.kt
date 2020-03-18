package pl.wawra.compass.presentation.compass

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorManager
import android.location.Geocoder
import androidx.lifecycle.MutableLiveData
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import pl.wawra.compass.base.BaseViewModel
import pl.wawra.compass.database.daos.LocationDao
import pl.wawra.compass.models.Location
import pl.wawra.compass.models.RotationModel
import javax.inject.Inject
import kotlin.math.abs
import kotlin.math.atan


class CompassViewModel : BaseViewModel() {

    @Inject
    lateinit var locationDao: LocationDao

    @Inject
    lateinit var geocoder: Geocoder

    val compassRotation = MutableLiveData<RotationModel>()
    val targetMarkerRotation = MutableLiveData<RotationModel>()
    val targetLocationString = MutableLiveData<String>()
    val targetAddressString = MutableLiveData<String>()

    private var targetLocation: Location? = null
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

    fun updateTargetLocation() {
        targetLocation ?: targetLocationString.postValue("")

        locationDao.getTargetLocation()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe {
                it?.let {
                    targetLocation = it
                    targetLocationString.postValue("${it.lat}, ${it.lon}")
                    updateTargetAddress(it.lat, it.lon)
                } ?: targetLocationString.postValue("")
            }.addToDisposables()
    }

    // TODO: extract some code
    fun handleSensorEvent(event: SensorEvent) {
        when (event.sensor.type) {
            Sensor.TYPE_MAGNETIC_FIELD -> magneticFieldValues?.let {
                if (accelerometerValues == null) return
                for (i: Int in 0..2) it[i] = (it[i] + event.values[i]) / 2
            }
            Sensor.TYPE_ACCELEROMETER -> accelerometerValues?.let {
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
                accelerometerValues = null
                magneticFieldValues = null
            }
        }
    }

    fun updateLocation(latitude: Double, longitude: Double) {
        val timestamp = System.currentTimeMillis()
        if (lastLocationUpdate != 0L && timestamp - lastLocationUpdate < 5000) return
        lastLocationUpdate = timestamp
        lastLocation.lat = latitude
        lastLocation.lon = longitude
    }

    // TODO: decrease nesting
    private fun updateTargetAddress(lat: Double, lon: Double) {
        Observable.fromCallable { geocoder.getFromLocation(lat, lon, 1) }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(
                {
                    if (it.isNotEmpty()) {
                        val address = it[0]
                        val lines = address.maxAddressLineIndex
                        val addressStringBuilder = StringBuilder(address.getAddressLine(0))
                        if (lines > 1) {
                            for (i: Int in 1..lines) {
                                addressStringBuilder.append("\n")
                                addressStringBuilder.append(address.getAddressLine(i))
                            }
                        }
                        targetAddressString.postValue(addressStringBuilder.toString())
                    } else {
                        targetAddressString.postValue("")
                    }
                },
                {
                    println()
                }
            ).addToDisposables()
    }

    // TODO: move to separate class, split to few functions?, move to back thread
    private fun updateRotations(newDegree: Float) {
        val timestamp = System.currentTimeMillis()
        if (lastCompassUpdate != 0L && timestamp - lastCompassUpdate < lastAnimationLength) return
        lastCompassUpdate = timestamp

        val calculatedDegree = (-Math.toDegrees(newDegree.toDouble()) + 360).toFloat() % 360

        val change = abs(calculatedDegree - lastCompassDegree)
        if (change < 10.0) return

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
        compassRotation.postValue(
            RotationModel(
                fromDegree,
                toDegree,
                animationLength
            )
        )

        targetLocation ?: return
        val targetDegreeFromNorth = calculateTargetDegree(
            targetLocation?.lat ?: 0.0,
            targetLocation?.lon ?: 0.0,
            lastLocation.lat,
            lastLocation.lon
        )
        toDegree = (calculatedDegree + targetDegreeFromNorth) % 360
        lastTargetMarkerDegree = toDegree % 360
        targetMarkerRotation.postValue(
            RotationModel(
                lastTargetMarkerDegree,
                toDegree,
                animationLength
            )
        )
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

    private fun radiansToDegrees(rad: Double): Double {
        return rad * 180.0 / Math.PI
    }

}