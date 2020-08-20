package pl.wawra.compass.di.modules

import android.location.Geocoder
import dagger.Module
import dagger.Provides
import pl.wawra.compass.App
import pl.wawra.compass.di.scopes.AppScoped
import pl.wawra.compass.helpers.RotationCalculator
import java.util.*

@Module
class HelpersModule {

    @AppScoped
    @Provides
    fun provideRotationCalculator(): RotationCalculator = RotationCalculator()

    @AppScoped
    @Provides
    fun provideGeocoder(app: App): Geocoder = Geocoder(app, Locale.getDefault())

}