package pl.wawra.compass.di.modules

import android.content.Context
import android.location.Geocoder
import dagger.Module
import dagger.Provides
import java.util.*

@Module
class GeocoderModule(private val context: Context) {

    private lateinit var geocoder: Geocoder

    @Provides
    fun provideGeocoder(): Geocoder = if (!::geocoder.isInitialized) {
        geocoder = Geocoder(context, Locale.getDefault())
        geocoder
    } else {
        geocoder
    }

}