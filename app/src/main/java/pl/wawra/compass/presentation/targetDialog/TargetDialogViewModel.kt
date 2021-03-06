package pl.wawra.compass.presentation.targetDialog

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import pl.wawra.compass.R
import pl.wawra.compass.base.BaseViewModel
import pl.wawra.compass.database.entities.Latitude
import pl.wawra.compass.database.entities.Longitude
import pl.wawra.compass.repositories.LocationRepository
import javax.inject.Inject

class TargetDialogViewModel @Inject constructor(var locationRepository: LocationRepository) :
    BaseViewModel() {

    fun insertNewTarget(latitude: String, longitude: String): LiveData<Boolean> {
        val result = MutableLiveData<Boolean>()

        val latitudeToInsert = createLatitude(latitude) ?: return result.apply { value = false }
        val longitudeToInsert = createLongitude(longitude) ?: return result.apply { value = false }

        locationRepository.changeLocation(latitudeToInsert, longitudeToInsert)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(
                { result.postValue(true) },
                {
                    it.printStackTrace()
                    result.postValue(false)
                }
            )
            .addToDisposables()

        return result
    }

    private fun createLatitude(latitude: String): Latitude? {
        val latitudeDouble: Double
        try {
            latitudeDouble = latitude.toDouble()
        } catch (e: Throwable) {
            return null
        }
        return Latitude().apply {
            value = latitudeDouble
            isActive = true
        }
    }

    private fun createLongitude(longitude: String): Longitude? {
        val longitudeDouble: Double
        try {
            longitudeDouble = longitude.toDouble()
        } catch (e: Throwable) {
            return null
        }
        return Longitude().apply {
            value = longitudeDouble
            isActive = true
        }
    }

    fun verifyTarget(latitude: String, longitude: String): LiveData<Pair<Int, Int>> {
        val result = MutableLiveData<Pair<Int, Int>>()

        val latitudeVerifyResult = verifyLatitude(latitude)
        val longitudeVerifyResult = verifyLongitude(longitude)
        result.postValue(Pair(latitudeVerifyResult, longitudeVerifyResult))

        return result
    }

    private fun verifyLongitude(longitude: String) = try {
        val longitudeNumeric = longitude.toDouble()
        when {
            longitudeNumeric < -180 -> R.string.longitude_to_small
            longitudeNumeric > 180 -> R.string.longitude_to_big
            else -> 0
        }
    } catch (e: Throwable) {
        R.string.incorrect_longitude
    }

    private fun verifyLatitude(latitude: String) = try {
        val latitudeNumeric = latitude.toDouble()
        when {
            latitudeNumeric < -90 -> R.string.latitude_to_small
            latitudeNumeric > 90 -> R.string.latitude_to_big
            else -> 0
        }
    } catch (e: Throwable) {
        R.string.incorrect_latitude
    }

    fun getPreviousLongitudes(): LiveData<List<String>> {
        val result = MutableLiveData<List<String>>()

        locationRepository.getLongitudes()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(
                {
                    result.postValue(it.map { value -> value.toString() })
                },
                {
                    it.printStackTrace()
                }
            ).addToDisposables()

        return result
    }

    fun getPreviousLatitudes(): LiveData<List<String>> {
        val result = MutableLiveData<List<String>>()

        locationRepository.getLatitudes()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(
                {
                    result.postValue(it.map { value -> value.toString() })
                },
                {
                    it.printStackTrace()
                }
            ).addToDisposables()

        return result
    }

}