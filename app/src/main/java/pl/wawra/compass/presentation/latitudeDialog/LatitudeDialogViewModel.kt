package pl.wawra.compass.presentation.latitudeDialog

import pl.wawra.compass.base.BaseViewModel
import pl.wawra.compass.database.daos.LocationDao
import javax.inject.Inject

class LatitudeDialogViewModel : BaseViewModel() {

    @Inject
    lateinit var locationDao: LocationDao

}