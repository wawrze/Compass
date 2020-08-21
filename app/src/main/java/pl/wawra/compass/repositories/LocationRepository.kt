package pl.wawra.compass.repositories

import pl.wawra.compass.database.daos.LocationDao
import pl.wawra.compass.database.entities.Latitude
import pl.wawra.compass.database.entities.Longitude
import javax.inject.Inject

class LocationRepository @Inject constructor(var locationDao: LocationDao) {

    fun changeLocation(newLatitude: Latitude, newLongitude: Longitude) =
        locationDao.changeLatitudesToInactive()
            .flatMap { locationDao.insertLatitude(newLatitude) }
            .flatMap { if (it < 1) throw Exception("Insert longitude error!") else locationDao.changeLongitudesToInactive() }
            .flatMap { locationDao.insertLongitude(newLongitude) }
            .map { if (it < 1) throw Exception("Insert latitude error!") else it }

    fun getLatitudes() = locationDao.getLatitudes()

    fun getLongitudes() = locationDao.getLongitudes()

    fun getTargetLocation() = locationDao.getTargetLocation()

}