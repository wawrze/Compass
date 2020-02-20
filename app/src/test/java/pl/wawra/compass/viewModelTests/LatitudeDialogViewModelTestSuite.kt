package pl.wawra.compass.viewModelTests

import io.reactivex.Maybe
import io.reactivex.Observable
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.Mockito
import pl.wawra.compass.BaseTestSuite
import pl.wawra.compass.R
import pl.wawra.compass.presentation.latitudeDialog.LatitudeDialogViewModel

class LatitudeDialogViewModelTestSuite : BaseTestSuite() {

    private val objectUnderTest = LatitudeDialogViewModel()

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
        Mockito.`when`(objectUnderTest.locationDao.getLatitudes())
            .thenReturn(Observable.just(latitudes))
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
        Mockito.`when`(objectUnderTest.locationDao.getLatitudes())
            .thenReturn(Observable.just(latitudes))
        // when
        val resultMutable = objectUnderTest.getPreviousLatitudes()
        val resultList = resultMutable.value
        // then
        assertEquals(0, resultList?.size)
    }

    @Test
    fun shouldVerifyCorrectLatitude() {
        // given
        val latitude = "0.0"
        // when
        val resultMutable = objectUnderTest.verifyLatitude(latitude)
        val result = resultMutable.value
        // then
        assertEquals(0, result)
    }

    @Test
    fun shouldVerifyIncorrectLatitudeFormat() {
        // given
        val latitude = "abc"
        // when
        val resultMutable = objectUnderTest.verifyLatitude(latitude)
        val result = resultMutable.value
        // then
        assertEquals(R.string.incorrect_latitude, result)
    }

    @Test
    fun shouldVerifyToBigLatitude() {
        // given
        val latitude = "100.0"
        // when
        val resultMutable = objectUnderTest.verifyLatitude(latitude)
        val result = resultMutable.value
        // then
        assertEquals(R.string.latitude_to_big, result)
    }

    @Test
    fun shouldVerifyToSmallLatitude() {
        // given
        val latitude = "-100.0"
        // when
        val resultMutable = objectUnderTest.verifyLatitude(latitude)
        val result = resultMutable.value
        // then
        assertEquals(R.string.latitude_to_small, result)
    }

    @Test
    fun shouldInsertNewLatitude() {
        // given
        val newLatitude = "0.0"
        Mockito.`when`(objectUnderTest.locationDao.changeLatitudesToInactive())
            .thenReturn(Maybe.just(0))
        Mockito.`when`(objectUnderTest.locationDao.insertLatitude(any()))
            .thenReturn(Maybe.just(1L))
        // when
        val resultMutable = objectUnderTest.insertNewLatitude(newLatitude)
        val result = resultMutable.value
        // then
        assertEquals(true, result)
    }

    @Test
    fun shouldNotInsertNewLatitudeWrongFormat() {
        // given
        val newLatitude = "abc"
        Mockito.`when`(objectUnderTest.locationDao.changeLatitudesToInactive())
            .thenReturn(Maybe.just(0))
        Mockito.`when`(objectUnderTest.locationDao.insertLatitude(any()))
            .thenReturn(Maybe.just(1L))
        // when
        val resultMutable = objectUnderTest.insertNewLatitude(newLatitude)
        val result = resultMutable.value
        // then
        assertEquals(false, result)
    }

    @Test
    fun shouldNotInsertNewLatitude() {
        // given
        val newLatitude = "0.0"
        Mockito.`when`(objectUnderTest.locationDao.changeLatitudesToInactive())
            .thenReturn(Maybe.just(0))
        Mockito.`when`(objectUnderTest.locationDao.insertLatitude(any()))
            .thenReturn(Maybe.just(0L))
        // when
        val resultMutable = objectUnderTest.insertNewLatitude(newLatitude)
        val result = resultMutable.value
        // then
        assertEquals(false, result)
    }

}