package pl.wawra.compass.di

import dagger.Component
import pl.wawra.compass.di.components.ApplicationComponent
import pl.wawra.compass.di.modules.DatabaseTestModule
import pl.wawra.compass.di.modules.GeocoderTestModule
import pl.wawra.compass.viewModelTests.LatitudeDialogViewModelTestSuite
import pl.wawra.compass.viewModelTests.TargetDialogViewModelTestSuite

@Component(modules = [DatabaseTestModule::class, GeocoderTestModule::class])
interface AppTestComponent : ApplicationComponent {

    companion object {
        val applicationComponent: AppTestComponent = DaggerAppTestComponent.builder()
            .geocoderTestModule(GeocoderTestModule)
            .databaseTestModule(DatabaseTestModule)
            .build()
    }

    fun inject(o: Any) {
        when (o) {
            is LatitudeDialogViewModelTestSuite -> inject(o)
            is TargetDialogViewModelTestSuite -> inject(o)
        }
    }

    fun inject(latitudeDialogViewModelTestSuite: LatitudeDialogViewModelTestSuite)
    fun inject(longitudeDialogViewModelTestSuite: TargetDialogViewModelTestSuite)

}