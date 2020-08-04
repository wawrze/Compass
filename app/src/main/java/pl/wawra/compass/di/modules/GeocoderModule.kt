package pl.wawra.compass.di.modules

import android.location.Geocoder
import dagger.Module
import dagger.Provides
import pl.wawra.compass.App
import java.util.*

@Module
class GeocoderModule {

    private lateinit var geocoder: Geocoder

    @Provides
    fun provideGeocoder(app: App): Geocoder = if (!::geocoder.isInitialized) {
        geocoder = Geocoder(app, Locale.getDefault())
        geocoder
    } else {
        geocoder
    }

}