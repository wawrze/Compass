package pl.wawra.compass.di.presentation.compass

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import pl.wawra.compass.database.daos.LocationDao
import javax.inject.Inject

class CompassViewModel : ViewModel() {

    @Inject
    lateinit var locationDao: LocationDao

    val degreeChange = MutableLiveData<Pair<Float, Float>>()

    private var lastDegree: Float = 0.0F

    fun updateDegree(degree: Float? = null) {
        if (degree == null) {
            degreeChange.postValue(Pair(0.0F, lastDegree))
        } else {
            val calculatedDegree = (Math.toDegrees(degree.toDouble()) + 360).toFloat() % 360
            degreeChange.postValue(Pair(lastDegree, calculatedDegree))
            lastDegree = -calculatedDegree
        }
    }

}