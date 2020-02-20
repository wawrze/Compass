package pl.wawra.compass.di.modules

import android.location.Geocoder
import dagger.Module
import dagger.Provides
import org.mockito.Mockito.mock

@Module
object GeocoderTestModule {

    @Provides
    fun provideGeocoder(): Geocoder = mock(Geocoder::class.java)

}