package pl.wawra.compass.viewModelTests

import io.reactivex.Maybe
import io.reactivex.Observable
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.Mockito
import pl.wawra.compass.BaseTestSuite
import pl.wawra.compass.R
import pl.wawra.compass.presentation.longitudeDialog.LongitudeDialogViewModel

class LongitudeDialogViewModelTestSuite : BaseTestSuite() {

    private val objectUnderTest = LongitudeDialogViewModel()

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
        Mockito.`when`(objectUnderTest.locationDao.getLongitudes())
            .thenReturn(Observable.just(longitudes))
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
        Mockito.`when`(objectUnderTest.locationDao.getLongitudes())
            .thenReturn(Observable.just(longitudes))
        // when
        val resultMutable = objectUnderTest.getPreviousLongitudes()
        val resultList = resultMutable.value
        // then
        assertEquals(0, resultList?.size)
    }

    @Test
    fun shouldVerifyCorrectLongitude() {
        // given
        val longitude = "0.0"
        // when
        val resultMutable = objectUnderTest.verifyLongitude(longitude)
        val result = resultMutable.value
        // then
        assertEquals(0, result)
    }

    @Test
    fun shouldVerifyIncorrectLongitudeFormat() {
        // given
        val longitude = "abc"
        // when
        val resultMutable = objectUnderTest.verifyLongitude(longitude)
        val result = resultMutable.value
        // then
        assertEquals(R.string.incorrect_longitude, result)
    }

    @Test
    fun shouldVerifyToBigLongitude() {
        // given
        val longitude = "200.0"
        // when
        val resultMutable = objectUnderTest.verifyLongitude(longitude)
        val result = resultMutable.value
        // then
        assertEquals(R.string.longitude_to_big, result)
    }

    @Test
    fun shouldVerifyToSmallLongitude() {
        // given
        val longitude = "-200.0"
        // when
        val resultMutable = objectUnderTest.verifyLongitude(longitude)
        val result = resultMutable.value
        // then
        assertEquals(R.string.longitude_to_small, result)
    }

    @Test
    fun shouldInsertNewLongitude() {
        // given
        val newLongitude = "0.0"
        Mockito.`when`(objectUnderTest.locationDao.changeLongitudesToInactive())
            .thenReturn(Maybe.just(0))
        Mockito.`when`(objectUnderTest.locationDao.insertLongitude(any()))
            .thenReturn(Maybe.just(1L))
        // when
        val resultMutable = objectUnderTest.insertNewLongitude(newLongitude)
        val result = resultMutable.value
        // then
        assertEquals(true, result)
    }

    @Test
    fun shouldNotInsertNewLongitudeWrongFormat() {
        // given
        val newLongitude = "abc"
        Mockito.`when`(objectUnderTest.locationDao.changeLongitudesToInactive())
            .thenReturn(Maybe.just(0))
        Mockito.`when`(objectUnderTest.locationDao.insertLongitude(any()))
            .thenReturn(Maybe.just(1L))
        // when
        val resultMutable = objectUnderTest.insertNewLongitude(newLongitude)
        val result = resultMutable.value
        // then
        assertEquals(false, result)
    }

    @Test
    fun shouldNotInsertNewLongitude() {
        // given
        val newLongitude = "0.0"
        Mockito.`when`(objectUnderTest.locationDao.changeLongitudesToInactive())
            .thenReturn(Maybe.just(0))
        Mockito.`when`(objectUnderTest.locationDao.insertLongitude(any()))
            .thenReturn(Maybe.just(0L))
        // when
        val resultMutable = objectUnderTest.insertNewLongitude(newLongitude)
        val result = resultMutable.value
        // then
        assertEquals(false, result)
    }

}