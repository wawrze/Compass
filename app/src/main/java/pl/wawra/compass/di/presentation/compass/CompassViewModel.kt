package pl.wawra.compass.di.presentation.compass

import androidx.lifecycle.ViewModel
import pl.wawra.compass.database.daos.LocationDao
import javax.inject.Inject

class CompassViewModel : ViewModel() {

    @Inject
    lateinit var locationDao: LocationDao

}