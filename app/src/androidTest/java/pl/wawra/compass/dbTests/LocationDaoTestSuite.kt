package pl.wawra.compass.dbTests

import androidx.room.EmptyResultSetException
import org.junit.Assert.*
import org.junit.Test
import pl.wawra.compass.BaseDaoTestSuite
import pl.wawra.compass.database.entities.Latitude
import pl.wawra.compass.database.entities.Longitude
import pl.wawra.compass.models.Location

class LocationDaoTestSuite : BaseDaoTestSuite() {

    private val objectUnderTest = db.locationDao()

    @Test
    fun shouldInsertLatitude() {
        // given
        val latitude = Latitude(value = 1.23)
        // when
        val result = objectUnderTest.insertLatitude(latitude).blockingGet()
        val latitudes = objectUnderTest.getLatitudes().blockingFirst()
        // then
        assertEquals(1L, result)
        assertEquals(1, latitudes.size)
        assertEquals(1.23, latitudes[0], 0.0)
    }

    @Test
    fun shouldInsertLongitude() {
        // given
        val longitude = Longitude(value = 1.23)
        // when
        val result = objectUnderTest.insertLongitude(longitude).blockingGet()
        val longitudes = objectUnderTest.getLongitudes().blockingFirst()
        // then
        assertEquals(1L, result)
        assertEquals(1, longitudes.size)
        assertEquals(1.23, longitudes[0], 0.0)
    }

    @Test
    fun shouldChangeLatitudesToInactive() {
        // given
        objectUnderTest.insertLatitude(Latitude(value = 1.0, isActive = true)).blockingGet()
        objectUnderTest.insertLatitude(Latitude(value = 2.0)).blockingGet()
        objectUnderTest.insertLatitude(Latitude(value = 3.0)).blockingGet()
        // when
        val result = objectUnderTest.changeLatitudesToInactive().blockingGet()
        // then
        assertEquals(3, result)
    }

    @Test
    fun shouldChangeLatitudesToInactiveNoActiveLatitudes() {
        // given
        objectUnderTest.insertLatitude(Latitude(value = 1.0)).blockingGet()
        objectUnderTest.insertLatitude(Latitude(value = 2.0)).blockingGet()
        objectUnderTest.insertLatitude(Latitude(value = 3.0)).blockingGet()
        // when
        val result = objectUnderTest.changeLatitudesToInactive().blockingGet()
        // then
        assertEquals(3, result)
    }

    @Test
    fun shouldNotChangeLatitudesToInactiveNoLatitudes() {
        // when
        val result = objectUnderTest.changeLatitudesToInactive().blockingGet()
        // then
        assertEquals(0, result)
    }

    @Test
    fun shouldChangeLongitudesToInactive() {
        // given
        objectUnderTest.insertLongitude(Longitude(value = 1.0, isActive = true)).blockingGet()
        objectUnderTest.insertLongitude(Longitude(value = 2.0)).blockingGet()
        objectUnderTest.insertLongitude(Longitude(value = 3.0)).blockingGet()
        // when
        val result = objectUnderTest.changeLongitudesToInactive().blockingGet()
        // then
        assertEquals(3, result)
    }

    @Test
    fun shouldChangeLongitudesToInactiveNoActiveLongitudes() {
        // given
        objectUnderTest.insertLongitude(Longitude(value = 1.0)).blockingGet()
        objectUnderTest.insertLongitude(Longitude(value = 2.0)).blockingGet()
        objectUnderTest.insertLongitude(Longitude(value = 3.0)).blockingGet()
        // when
        val result = objectUnderTest.changeLongitudesToInactive().blockingGet()
        // then
        assertEquals(3, result)
    }

    @Test
    fun shouldNotChangeLongitudesToInactiveNoLongitudes() {
        // when
        val result = objectUnderTest.changeLongitudesToInactive().blockingGet()
        // then
        assertEquals(0, result)
    }

    @Test
    fun shouldGetLatitudes() {
        // given
        objectUnderTest.insertLatitude(Latitude(value = 1.0)).blockingGet()
        objectUnderTest.insertLatitude(Latitude(value = 2.0)).blockingGet()
        objectUnderTest.insertLatitude(Latitude(value = 2.0)).blockingGet()
        objectUnderTest.insertLatitude(Latitude(value = 3.0)).blockingGet()
        objectUnderTest.insertLatitude(Latitude(value = 3.0)).blockingGet()
        objectUnderTest.insertLatitude(Latitude(value = 3.0)).blockingGet()
        // when
        val result = objectUnderTest.getLatitudes().blockingFirst()
        // then
        assertEquals(3, result.size)
        assertEquals(3.0, result[0], 0.0)
        assertEquals(2.0, result[1], 0.0)
        assertEquals(1.0, result[2], 0.0)
    }

    @Test
    fun shouldGetLatitudesEmptyList() {
        // when
        val result = objectUnderTest.getLatitudes().blockingFirst()
        // then
        assertEquals(0, result.size)
    }

    @Test
    fun shouldGetLongitudes() {
        // given
        objectUnderTest.insertLongitude(Longitude(value = 1.0)).blockingGet()
        objectUnderTest.insertLongitude(Longitude(value = 2.0)).blockingGet()
        objectUnderTest.insertLongitude(Longitude(value = 2.0)).blockingGet()
        objectUnderTest.insertLongitude(Longitude(value = 3.0)).blockingGet()
        objectUnderTest.insertLongitude(Longitude(value = 3.0)).blockingGet()
        objectUnderTest.insertLongitude(Longitude(value = 3.0)).blockingGet()
        // when
        val result = objectUnderTest.getLongitudes().blockingFirst()
        // then
        assertEquals(3, result.size)
        assertEquals(3.0, result[0], 0.0)
        assertEquals(2.0, result[1], 0.0)
        assertEquals(1.0, result[2], 0.0)
    }

    @Test
    fun shouldGetLongitudesEmptyList() {
        // when
        val result = objectUnderTest.getLongitudes().blockingFirst()
        // then
        assertEquals(0, result.size)
    }

    @Test
    fun shouldGetTargetLocation() {
        // given
        objectUnderTest.insertLatitude(Latitude(value = 1.0)).blockingGet()
        objectUnderTest.insertLatitude(Latitude(value = 2.0, isActive = true)).blockingGet()
        objectUnderTest.insertLongitude(Longitude(value = 3.0)).blockingGet()
        objectUnderTest.insertLongitude(Longitude(value = 4.0, isActive = true)).blockingGet()
        // when
        val result = objectUnderTest.getTargetLocation().blockingGet()
        // then
        assertEquals(2.0, result?.lat ?: 0.0, 0.0)
        assertEquals(4.0, result?.lon ?: 0.0, 0.0)
    }

    @Test
    fun shouldNotGetTargetLocationNoActiveLatitude() {
        // given
        objectUnderTest.insertLatitude(Latitude(value = 1.0)).blockingGet()
        objectUnderTest.insertLatitude(Latitude(value = 2.0)).blockingGet()
        objectUnderTest.insertLongitude(Longitude(value = 3.0)).blockingGet()
        objectUnderTest.insertLongitude(Longitude(value = 4.0, isActive = true)).blockingGet()
        var result: Location? = null
        var error: Exception? = null
        // when
        try {
            result = objectUnderTest.getTargetLocation().blockingGet()
        } catch (e: Exception) {
            error = e
        }
        // then
        assertNull(result)
        assertTrue(error is EmptyResultSetException)
    }

    @Test
    fun shouldNotGetTargetLocationNoActiveLongitude() {
        // given
        objectUnderTest.insertLatitude(Latitude(value = 1.0)).blockingGet()
        objectUnderTest.insertLatitude(Latitude(value = 2.0, isActive = true)).blockingGet()
        objectUnderTest.insertLongitude(Longitude(value = 3.0)).blockingGet()
        objectUnderTest.insertLongitude(Longitude(value = 4.0)).blockingGet()
        var result: Location? = null
        var error: Exception? = null
        // when
        try {
            result = objectUnderTest.getTargetLocation().blockingGet()
        } catch (e: Exception) {
            error = e
        }
        // then
        assertNull(result)
        assertTrue(error is EmptyResultSetException)
    }

    @Test
    fun shouldNotGetTargetLocationNoActiveLatitudeAndNoActiveLongitude() {
        // given
        objectUnderTest.insertLatitude(Latitude(value = 1.0)).blockingGet()
        objectUnderTest.insertLatitude(Latitude(value = 2.0)).blockingGet()
        objectUnderTest.insertLongitude(Longitude(value = 3.0)).blockingGet()
        objectUnderTest.insertLongitude(Longitude(value = 4.0)).blockingGet()
        var result: Location? = null
        var error: Exception? = null
        // when
        try {
            result = objectUnderTest.getTargetLocation().blockingGet()
        } catch (e: Exception) {
            error = e
        }
        // then
        assertNull(result)
        assertTrue(error is EmptyResultSetException)
    }

}