package pl.wawra.compass.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import pl.wawra.compass.database.entities.Latitude
import pl.wawra.compass.database.entities.Longitude
import pl.wawra.compass.models.Location

@Dao
interface LocationDao {

    @Insert
    fun insertLatitude(latitude: Latitude): Maybe<Long>

    @Insert
    fun insertLongitude(longitude: Longitude): Maybe<Long>

    @Query("UPDATE Latitude SET isActive = 0")
    fun changeLatitudesToInactive(): Maybe<Int>

    @Query("UPDATE Longitude SET isActive = 0")
    fun changeLongitudesToInactive(): Maybe<Int>

    @Query(
        """
        SELECT DISTINCT value
        FROM 
            (SELECT value, COUNT(value) AS c FROM Latitude GROUP BY value ORDER BY c DESC)
        """
    )
    fun getLatitudes(): Observable<List<Double>>

    @Query(
        """
        SELECT DISTINCT value
        FROM 
            (SELECT value, COUNT(value) AS c FROM Longitude GROUP BY value ORDER BY c DESC)
        """
    )
    fun getLongitudes(): Observable<List<Double>>

    @Query(
        """
        SELECT 
            lat.value AS lat,
            lon.value AS lon
        FROM latitude lat, longitude lon
        WHERE lat.isActive = 1
            AND lon.isActive = 1
        LIMIT 1
    """
    )
    fun getTargetLocation(): Single<Location>

}