package pl.wawra.compass.presentation.targetDialog

import androidx.lifecycle.MutableLiveData
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import pl.wawra.compass.R
import pl.wawra.compass.base.BaseViewModel
import pl.wawra.compass.database.daos.LocationDao
import pl.wawra.compass.database.entities.Latitude
import pl.wawra.compass.database.entities.Longitude
import javax.inject.Inject

class TargetDialogViewModel @Inject constructor(var locationDao: LocationDao) : BaseViewModel() {

    fun insertNewTarget(latitude: String, longitude: String): MutableLiveData<Boolean> {
        val result = MutableLiveData<Boolean>()

        val latitudeToInsert = createLatitude(latitude) ?: return result.apply { value = false }
        val longitudeToInsert = createLongitude(longitude) ?: return result.apply { value = false }

        locationDao.changeLatitudesToInactive()
            .flatMap { locationDao.insertLatitude(latitudeToInsert) }
            .flatMap { if (it < 1) throw Exception() else locationDao.changeLongitudesToInactive() }
            .flatMap { locationDao.insertLongitude(longitudeToInsert) }
            .map { if (it < 1) throw Exception() else it }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(
                { result.postValue(true) },
                { result.postValue(false) }
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

    fun verifyTarget(latitude: String, longitude: String): MutableLiveData<Pair<Int, Int>> {
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

    fun getPreviousLongitudes(): MutableLiveData<List<String>> {
        val result = MutableLiveData<List<String>>()

        locationDao.getLongitudes()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe {
                result.postValue(it.map { value -> value.toString() })
            }.addToDisposables()

        return result
    }

    fun getPreviousLatitudes(): MutableLiveData<List<String>> {
        val result = MutableLiveData<List<String>>()

        locationDao.getLatitudes()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe {
                result.postValue(it.map { value -> value.toString() })
            }.addToDisposables()

        return result
    }

}