package pl.wawra.compass.di.presentation.compass

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import pl.wawra.compass.database.daos.LocationDao
import javax.inject.Inject

class CompassViewModel : ViewModel() {

    @Inject
    lateinit var locationDao: LocationDao

    private var lastDegree: Float = 0.0F
    val degreeChange = MutableLiveData<Pair<Float, Float>>()

    fun updateDegree(degree: Double? = null) {
        if (degree == null) {
            degreeChange.postValue(Pair(0.0F, lastDegree))
        } else {
            val calculatedDegree = (Math.toDegrees(degree) + 360).toFloat() % 360
            degreeChange.postValue(Pair(lastDegree, calculatedDegree))
            lastDegree = calculatedDegree
        }
    }

}