package pl.wawra.compass.repositoryTests

import androidx.room.EmptyResultSetException
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import org.junit.Assert.*
import org.junit.Test
import pl.wawra.compass.BaseTestSuite
import pl.wawra.compass.database.entities.Latitude
import pl.wawra.compass.database.entities.Longitude
import pl.wawra.compass.models.Location
import pl.wawra.compass.repositories.LocationRepository

class LocationRepositoryTestSuite : BaseTestSuite() {

    private val objectUnderTest = LocationRepository(mockk())

    @Test
    fun shouldChangeLocation() {
        // given
        val latitude = Latitude(value = 0.12)
        val longitude = Longitude(value = 0.12)
        every { objectUnderTest.locationDao.changeLatitudesToInactive() } returns Maybe.just(1)
        every { objectUnderTest.locationDao.insertLatitude(latitude) } returns Maybe.just(1)
        every { objectUnderTest.locationDao.changeLongitudesToInactive() } returns Maybe.just(1)
        every { objectUnderTest.locationDao.insertLongitude(longitude) } returns Maybe.just(1)
        // when
        val result = objectUnderTest.changeLocation(latitude, longitude).blockingGet()
        // then
        verify { objectUnderTest.locationDao.changeLatitudesToInactive() }
        verify { objectUnderTest.locationDao.insertLatitude(latitude) }
        verify { objectUnderTest.locationDao.changeLongitudesToInactive() }
        verify { objectUnderTest.locationDao.insertLongitude(longitude) }
        assertEquals(1, result)
    }

    @Test
    fun shouldNotChangeLocationLatitudeNotInserted() {
        // given
        val latitude = Latitude(value = 0.12)
        val longitude = Longitude(value = 0.12)
        every { objectUnderTest.locationDao.changeLatitudesToInactive() } returns Maybe.just(1)
        every { objectUnderTest.locationDao.insertLatitude(latitude) } returns Maybe.just(0)
        every { objectUnderTest.locationDao.changeLongitudesToInactive() } returns Maybe.just(1)
        every { objectUnderTest.locationDao.insertLongitude(longitude) } returns Maybe.just(1)
        var result: Long? = null
        var error: Exception? = null
        // when
        try {
            result = objectUnderTest.changeLocation(latitude, longitude).blockingGet()
        } catch (e: Exception) {
            error = e
        }
        // then
        assertNull(result)
        assertNotNull(error)
    }

    @Test
    fun shouldNotChangeLocationLongitudeNotInserted() {
        // given
        val latitude = Latitude(value = 0.12)
        val longitude = Longitude(value = 0.12)
        every { objectUnderTest.locationDao.changeLatitudesToInactive() } returns Maybe.just(1)
        every { objectUnderTest.locationDao.insertLatitude(latitude) } returns Maybe.just(1)
        every { objectUnderTest.locationDao.changeLongitudesToInactive() } returns Maybe.just(1)
        every { objectUnderTest.locationDao.insertLongitude(longitude) } returns Maybe.just(0)
        var result: Long? = null
        var error: Exception? = null
        // when
        try {
            result = objectUnderTest.changeLocation(latitude, longitude).blockingGet()
        } catch (e: Exception) {
            error = e
        }
        // then
        assertNull(result)
        assertNotNull(error)
    }

    @Test
    fun shouldGetLatitudes() {
        // given
        val latitudes = listOf(
            0.12,
            3.45,
            6.78
        )
        every { objectUnderTest.locationDao.getLatitudes() } returns Observable.just(latitudes)
        // when
        val result = objectUnderTest.getLatitudes().blockingFirst()
        // then
        assertEquals(3, result.size)
        assertEquals(0.12, result[0], 0.0)
        assertEquals(3.45, result[1], 0.0)
        assertEquals(6.78, result[2], 0.0)
    }

    @Test
    fun shouldGetLatitudesEmptyList() {
        // given
        every { objectUnderTest.locationDao.getLatitudes() } returns Observable.just(listOf())
        // when
        val result = objectUnderTest.getLatitudes().blockingFirst()
        // then
        assertEquals(0, result.size)
    }

    @Test
    fun shouldGetLongitudes() {
        // given
        val longitudes = listOf(
            0.12,
            3.45,
            6.78
        )
        every { objectUnderTest.locationDao.getLongitudes() } returns Observable.just(longitudes)
        // when
        val result = objectUnderTest.getLongitudes().blockingFirst()
        // then
        assertEquals(3, result.size)
        assertEquals(0.12, result[0], 0.0)
        assertEquals(3.45, result[1], 0.0)
        assertEquals(6.78, result[2], 0.0)
    }

    @Test
    fun shouldGetLongitudesEmptyList() {
        // given
        every { objectUnderTest.locationDao.getLongitudes() } returns Observable.just(listOf())
        // when
        val result = objectUnderTest.getLongitudes().blockingFirst()
        // then
        assertEquals(0, result.size)
    }

    @Test
    fun shouldGetTargetLocation() {
        // given
        val location = Location(0.12, 3.14)
        every { objectUnderTest.locationDao.getTargetLocation() } returns Single.just(location)
        // when
        val result = objectUnderTest.getTargetLocation().blockingGet()
        // then
        assertEquals(0.12, result?.lat)
        assertEquals(3.14, result?.lon)
    }

    @Test
    fun shouldNotGetTargetLocation() {
        // given
        every {
            objectUnderTest.locationDao.getTargetLocation()
        } returns Single.error(EmptyResultSetException(""))
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
        assertNotNull(error)
    }

}