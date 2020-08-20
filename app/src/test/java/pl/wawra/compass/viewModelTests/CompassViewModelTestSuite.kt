package pl.wawra.compass.viewModelTests

import android.location.Geocoder
import org.junit.Assert
import org.junit.Test
import org.mockito.Mockito.mock
import pl.wawra.compass.BaseTestSuite
import pl.wawra.compass.helpers.RotationCalculator
import pl.wawra.compass.presentation.compass.CompassViewModel
import pl.wawra.compass.repositories.LocationRepository

class CompassViewModelTestSuite : BaseTestSuite() {

    // TODO: CompassViewModel tests

    private val objectUnderTest = CompassViewModel(
        mock(LocationRepository::class.java),
        mock(Geocoder::class.java),
        mock(RotationCalculator::class.java)
    )

    @Test
    fun shouldTestNothing() {
        // given

        // when

        // then
        Assert.assertEquals(0, 0)
    }

}