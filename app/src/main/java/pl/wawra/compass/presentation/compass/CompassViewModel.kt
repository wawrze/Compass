package pl.wawra.compass.presentation.compass

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorManager
import android.location.Address
import android.location.Geocoder
import androidx.lifecycle.MutableLiveData
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import pl.wawra.compass.base.BaseViewModel
import pl.wawra.compass.database.daos.LocationDao
import pl.wawra.compass.helpers.RotationCalculator
import pl.wawra.compass.models.Location
import pl.wawra.compass.models.RotationModel
import pl.wawra.compass.repositories.LocationRepository
import javax.inject.Inject


class CompassViewModel @Inject constructor(
    var locationRepository: LocationRepository,
    var geocoder: Geocoder,
    var rotationCalculator: RotationCalculator
) : BaseViewModel() {

    val compassRotation = MutableLiveData<RotationModel>()
    val targetMarkerRotation = MutableLiveData<RotationModel>()
    val targetLocationString = MutableLiveData<String>()
    val targetAddressString = MutableLiveData<String>()

    private var targetLocation: Location? = null
    private val lastLocation = Location(0.0, 0.0)
    private var lastLocationUpdate: Long = 0L

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

        locationRepository.getTargetLocation()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe {
                it?.let {
                    targetLocation = it
                    targetLocationString.postValue("${it.lat}, ${it.lon}")
                    getTargetAddress(it.lat, it.lon)
                } ?: targetLocationString.postValue("")
            }.addToDisposables()
    }

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
        if (accelerometerValues != null && magneticFieldValues != null) updateRotations()
    }

    private fun updateRotations() {
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
            calculateRotations(orientation[0])
            accelerometerValues = null
            magneticFieldValues = null
        }
    }

    private fun calculateRotations(newDegree: Float) {
        Observable.fromCallable {
            rotationCalculator.calculateRotations(newDegree, targetLocation, lastLocation)
        }.observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(
                { rotations ->
                    rotations.first?.let { compassRotation.postValue(it) }
                    rotations.second?.let { targetMarkerRotation.postValue(it) }
                },
                {}
            )
            .addToDisposables()
    }

    fun updateLocation(latitude: Double, longitude: Double) {
        val timestamp = System.currentTimeMillis()
        if (lastLocationUpdate != 0L && timestamp - lastLocationUpdate < 5000) return
        lastLocationUpdate = timestamp
        lastLocation.lat = latitude
        lastLocation.lon = longitude
    }

    private fun getTargetAddress(lat: Double, lon: Double) {
        Observable.fromCallable { geocoder.getFromLocation(lat, lon, 1) }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(
                {
                    if (it.isNotEmpty()) {
                        updateTargetAddress(it)
                    } else {
                        targetAddressString.postValue("")
                    }
                },
                {}
            ).addToDisposables()
    }

    private fun updateTargetAddress(addresses: List<Address>) {
        val address = addresses[0]
        val lines = address.maxAddressLineIndex
        val addressStringBuilder = StringBuilder(address.getAddressLine(0))
        if (lines > 1) {
            for (i: Int in 1..lines) {
                addressStringBuilder.append("\n")
                addressStringBuilder.append(address.getAddressLine(i))
            }
        }
        targetAddressString.postValue(addressStringBuilder.toString())
    }

}