package pl.wawra.compass.testsDI

import dagger.Component
import pl.wawra.compass.di.components.ApplicationComponent
import pl.wawra.compass.testsDI.modules.DatabaseTestModule
import pl.wawra.compass.testsDI.modules.GeocoderTestModule
import pl.wawra.compass.testsDI.modules.HelpersTestModule
import pl.wawra.compass.viewModelTests.CompassViewModelTestSuite
import pl.wawra.compass.viewModelTests.TargetDialogViewModelTestSuite

@Component(modules = [DatabaseTestModule::class, GeocoderTestModule::class, HelpersTestModule::class])
interface AppTestComponent : ApplicationComponent {

    companion object {
        val applicationComponent: AppTestComponent? = DaggerAppTestComponent.builder()
            .geocoderTestModule(GeocoderTestModule)
            .databaseTestModule(DatabaseTestModule)
            .build()
    }

    fun inject(o: Any) {
        when (o) {
            is CompassViewModelTestSuite -> inject(o)
            is TargetDialogViewModelTestSuite -> inject(o)
        }
    }

    fun inject(compassViewModelTestSuite: CompassViewModelTestSuite)
    fun inject(longitudeDialogViewModelTestSuite: TargetDialogViewModelTestSuite)

}