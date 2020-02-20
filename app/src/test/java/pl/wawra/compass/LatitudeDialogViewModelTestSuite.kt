package pl.wawra.compass

import io.reactivex.Observable
import org.junit.Assert
import org.junit.Test
import org.mockito.Mockito
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
        Assert.assertEquals(5, resultList.size)
        Assert.assertEquals("11.111111", resultList[0])
        Assert.assertEquals("22.222222", resultList[1])
        Assert.assertEquals("33.333333", resultList[2])
        Assert.assertEquals("44.444444", resultList[3])
        Assert.assertEquals("55.555555", resultList[4])
    }

}