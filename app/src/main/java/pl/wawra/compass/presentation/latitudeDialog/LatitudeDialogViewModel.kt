package pl.wawra.compass.presentation.latitudeDialog

import androidx.lifecycle.MutableLiveData
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import pl.wawra.compass.R
import pl.wawra.compass.base.BaseViewModel
import pl.wawra.compass.database.daos.LocationDao
import pl.wawra.compass.database.entities.Latitude
import javax.inject.Inject

class LatitudeDialogViewModel : BaseViewModel() {

    @Inject
    lateinit var locationDao: LocationDao

    var callBack: (() -> Unit)? = null

    fun insertNewLatitude(latitude: String): MutableLiveData<Boolean> {
        val result = MutableLiveData<Boolean>()

        val latitudeDouble: Double
        try {
            latitudeDouble = latitude.toDouble()
        } catch (e: Throwable) {
            result.apply { value = false }
            return result
        }

        val latitudeToInsert = Latitude().apply {
            value = latitudeDouble
            isActive = true
        }
        locationDao.changeLatitudesToInactive()
            .flatMap { locationDao.insertLatitude(latitudeToInsert) }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe {
                result.postValue(it > 0)
            }.addToDisposables()

        return result
    }

    fun verifyLatitude(latitude: String): MutableLiveData<Int> {
        val result = MutableLiveData<Int>()
        try {
            val latitudeNumeric = latitude.toDouble()
            when {
                latitudeNumeric < -90 -> result.postValue(R.string.latitude_to_small)
                latitudeNumeric > 90 -> result.postValue(R.string.latitude_to_big)
                else -> result.postValue(0)
            }
        } catch (e: Throwable) {
            result.postValue(R.string.incorrect_latitude)
        }

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