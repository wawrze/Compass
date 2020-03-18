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

class TargetDialogViewModel : BaseViewModel() {

    @Inject
    lateinit var locationDao: LocationDao

    var callBack: TargetDialogCallback? = null

    fun insertNewTarget(latitude: String, longitude: String): MutableLiveData<Boolean> {
        val result = MutableLiveData<Boolean>()

        val latitudeDouble: Double
        try {
            latitudeDouble = latitude.toDouble()
        } catch (e: Throwable) {
            result.apply { value = false }
            return result
        }
        val longitudeDouble: Double
        try {
            longitudeDouble = longitude.toDouble()
        } catch (e: Throwable) {
            result.apply { value = false }
            return result
        }

        val latitudeToInsert = Latitude().apply {
            value = latitudeDouble
            isActive = true
        }
        val longitudeToInsert = Longitude().apply {
            value = longitudeDouble
            isActive = true
        }

        locationDao.changeLatitudesToInactive()
            .flatMap { locationDao.insertLatitude(latitudeToInsert) }
            .flatMap { locationDao.changeLongitudesToInactive() }
            .flatMap { locationDao.insertLongitude(longitudeToInsert) }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe { result.postValue(true) }
            .addToDisposables()

        return result
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