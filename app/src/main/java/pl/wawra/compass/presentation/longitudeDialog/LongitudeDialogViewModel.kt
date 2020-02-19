package pl.wawra.compass.presentation.longitudeDialog

import androidx.lifecycle.MutableLiveData
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import pl.wawra.compass.R
import pl.wawra.compass.base.BaseViewModel
import pl.wawra.compass.database.daos.LocationDao
import pl.wawra.compass.database.entities.Longitude
import javax.inject.Inject

class LongitudeDialogViewModel : BaseViewModel() {

    @Inject
    lateinit var locationDao: LocationDao

    var callBack: LongitudeDialogCallback? = null

    fun insertNewLongitude(longitude: String): MutableLiveData<Boolean> {
        val result = MutableLiveData<Boolean>()

        val longitudeDouble: Double
        try {
            longitudeDouble = longitude.toDouble()
        } catch (e: Throwable) {
            result.apply { value = false }
            return result
        }

        val longitudeToInsert = Longitude().apply {
            value = longitudeDouble
            isActive = true
        }
        locationDao.changeLongitudesToInactive()
            .flatMap { locationDao.insertLongitude(longitudeToInsert) }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe {
                result.postValue(it > 0)
            }.addToDisposables()

        return result
    }

    fun verifyLongitude(longitude: String): MutableLiveData<Int> {
        val result = MutableLiveData<Int>()
        try {
            val longitudeNumeric = longitude.toDouble()
            when {
                longitudeNumeric < -180 -> result.postValue(R.string.longitude_to_small)
                longitudeNumeric > 180 -> result.postValue(R.string.longitude_to_big)
                else -> result.postValue(0)
            }
        } catch (e: Throwable) {
            result.postValue(R.string.incorrect_longitude)
        }

        return result
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

}