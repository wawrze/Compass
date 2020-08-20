package pl.wawra.compass.viewModelTests

import io.mockk.every
import io.mockk.mockk
import io.reactivex.Maybe
import io.reactivex.Observable
import org.junit.Assert.assertEquals
import org.junit.Test
import pl.wawra.compass.BaseTestSuite
import pl.wawra.compass.R
import pl.wawra.compass.presentation.targetDialog.TargetDialogViewModel

class TargetDialogViewModelTestSuite : BaseTestSuite() {

    private val objectUnderTest = TargetDialogViewModel(mockk())

    @Test
    fun shouldGetPreviousLatitudes() {
        // given
        val latitudes = ArrayList<Double>().apply {
            add(11.111111)
            add(22.222222)
            add(33.333333)
            add(44.444444)
            add(55.555555)
        }
        every {
            objectUnderTest.locationRepository.getLatitudes()
        } returns Observable.just(latitudes)
        // when
        val resultMutable = objectUnderTest.getPreviousLatitudes()
        val resultList = resultMutable.value ?: ArrayList()
        // then
        assertEquals(5, resultList.size)
        assertEquals("11.111111", resultList[0])
        assertEquals("22.222222", resultList[1])
        assertEquals("33.333333", resultList[2])
        assertEquals("44.444444", resultList[3])
        assertEquals("55.555555", resultList[4])
    }

    @Test
    fun shouldGetPreviousLatitudesEmptyList() {
        // given
        val latitudes = ArrayList<Double>()
        every {
            objectUnderTest.locationRepository.getLatitudes()
        } returns Observable.just(latitudes)
        // when
        val resultMutable = objectUnderTest.getPreviousLatitudes()
        val resultList = resultMutable.value
        // then
        assertEquals(0, resultList?.size)
    }

    @Test
    fun shouldGetPreviousLongitudes() {
        // given
        val longitudes = ArrayList<Double>().apply {
            add(11.111111)
            add(22.222222)
            add(33.333333)
            add(44.444444)
            add(55.555555)
        }
        every {
            objectUnderTest.locationRepository.getLongitudes()
        } returns Observable.just(longitudes)
        // when
        val resultMutable = objectUnderTest.getPreviousLongitudes()
        val resultList = resultMutable.value ?: ArrayList()
        // then
        assertEquals(5, resultList.size)
        assertEquals("11.111111", resultList[0])
        assertEquals("22.222222", resultList[1])
        assertEquals("33.333333", resultList[2])
        assertEquals("44.444444", resultList[3])
        assertEquals("55.555555", resultList[4])
    }

    @Test
    fun shouldGetPreviousLongitudesEmptyList() {
        // given
        val longitudes = ArrayList<Double>()
        every {
            objectUnderTest.locationRepository.getLongitudes()
        } returns Observable.just(longitudes)
        // when
        val resultMutable = objectUnderTest.getPreviousLongitudes()
        val resultList = resultMutable.value
        // then
        assertEquals(0, resultList?.size)
    }

    @Test
    fun shouldVerifyCorrectTarget() {
        // given
        val latitude = "0.0"
        val longitude = "0.0"
        // when
        val resultMutable = objectUnderTest.verifyTarget(latitude, longitude)
        val latitudeResult = resultMutable.value?.first
        val longitudeResult = resultMutable.value?.second
        // then
        assertEquals(0, latitudeResult)
        assertEquals(0, longitudeResult)
    }

    @Test
    fun shouldVerifyIncorrectLatitudeFormat() {
        // given
        val latitude = "abc"
        val longitude = "0"
        // when
        val resultMutable = objectUnderTest.verifyTarget(latitude, longitude)
        val latitudeResult = resultMutable.value?.first
        val longitudeResult = resultMutable.value?.second
        // then
        assertEquals(R.string.incorrect_latitude, latitudeResult)
        assertEquals(0, longitudeResult)
    }

    @Test
    fun shouldVerifyIncorrectLongitudeFormat() {
        // given
        val latitude = "0.0"
        val longitude = "abc"
        // when
        val resultMutable = objectUnderTest.verifyTarget(latitude, longitude)
        val latitudeResult = resultMutable.value?.first
        val longitudeResult = resultMutable.value?.second
        // then
        assertEquals(0, latitudeResult)
        assertEquals(R.string.incorrect_longitude, longitudeResult)
    }

    @Test
    fun shouldVerifyToBigLatitude() {
        // given
        val latitude = "100.0"
        val longitude = "0"
        // when
        val resultMutable = objectUnderTest.verifyTarget(latitude, longitude)
        val latitudeResult = resultMutable.value?.first
        val longitudeResult = resultMutable.value?.second
        // then
        assertEquals(R.string.latitude_to_big, latitudeResult)
        assertEquals(0, longitudeResult)
    }

    @Test
    fun shouldVerifyToSmallLatitude() {
        // given
        val latitude = "-100.0"
        val longitude = "0"
        // when
        val resultMutable = objectUnderTest.verifyTarget(latitude, longitude)
        val latitudeResult = resultMutable.value?.first
        val longitudeResult = resultMutable.value?.second
        // then
        assertEquals(R.string.latitude_to_small, latitudeResult)
        assertEquals(0, longitudeResult)
    }

    @Test
    fun shouldVerifyToBigLongitude() {
        // given
        val latitude = "0.0"
        val longitude = "200.0"
        // when
        val resultMutable = objectUnderTest.verifyTarget(latitude, longitude)
        val latitudeResult = resultMutable.value?.first
        val longitudeResult = resultMutable.value?.second
        // then
        assertEquals(0, latitudeResult)
        assertEquals(R.string.longitude_to_big, longitudeResult)
    }

    @Test
    fun shouldVerifyToSmallLongitude() {
        // given
        val latitude = "0.0"
        val longitude = "-200.0"
        // when
        val resultMutable = objectUnderTest.verifyTarget(latitude, longitude)
        val latitudeResult = resultMutable.value?.first
        val longitudeResult = resultMutable.value?.second
        // then
        assertEquals(0, latitudeResult)
        assertEquals(R.string.longitude_to_small, longitudeResult)
    }

    @Test
    fun shouldInsertNewLatitude() {
        // given
        val newLatitude = "0.0"
        val newLongitude = "0.0"
        every {
            objectUnderTest.locationRepository.changeLocation(any(), any())
        } returns Maybe.just(1L)
        // when
        val resultMutable = objectUnderTest.insertNewTarget(newLatitude, newLongitude)
        val result = resultMutable.value
        // then
        assertEquals(true, result)
    }

    @Test
    fun shouldInsertNewLongitude() {
        // given
        val newLatitude = "0.0"
        val newLongitude = "0.0"
        every {
            objectUnderTest.locationRepository.changeLocation(any(), any())
        } returns Maybe.just(1L)
        // when
        val resultMutable = objectUnderTest.insertNewTarget(newLatitude, newLongitude)
        val result = resultMutable.value
        // then
        assertEquals(true, result)
    }

    @Test
    fun shouldNotInsertNewLatitudeWrongFormat() {
        // given
        val newLatitude = "abc"
        val newLongitude = "0.0"
        every {
            objectUnderTest.locationRepository.changeLocation(any(), any())
        } returns Maybe.just(1L)
        // when
        val resultMutable = objectUnderTest.insertNewTarget(newLatitude, newLongitude)
        val result = resultMutable.value
        // then
        assertEquals(false, result)
    }

    @Test
    fun shouldNotInsertNewLongitudeWrongFormat() {
        // given
        val newLatitude = "0.0"
        val newLongitude = "abc"
        every {
            objectUnderTest.locationRepository.changeLocation(any(), any())
        } returns Maybe.just(1L)
        // when
        val resultMutable = objectUnderTest.insertNewTarget(newLatitude, newLongitude)
        val result = resultMutable.value
        // then
        assertEquals(false, result)
    }

    @Test
    fun shouldNotInsertNewLatitude() {
        // given
        val newLatitude = "0.0"
        val newLongitude = "0.0"
        every {
            objectUnderTest.locationRepository.changeLocation(any(), any())
        } returns Maybe.error(Exception())
        // when
        val resultMutable = objectUnderTest.insertNewTarget(newLatitude, newLongitude)
        val result = resultMutable.value
        // then
        assertEquals(false, result)
    }

    @Test
    fun shouldNotInsertNewLongitude() {
        // given
        val newLatitude = "0.0"
        val newLongitude = "0.0"
        every {
            objectUnderTest.locationRepository.changeLocation(any(), any())
        } returns Maybe.error(Exception())
        // when
        val resultMutable = objectUnderTest.insertNewTarget(newLatitude, newLongitude)
        val result = resultMutable.value
        // then
        assertEquals(false, result)
    }

}