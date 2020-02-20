package pl.wawra.compass.di

import dagger.Component
import pl.wawra.compass.LatitudeDialogViewModelTestSuite
import pl.wawra.compass.di.components.ApplicationComponent
import pl.wawra.compass.di.modules.DatabaseTestModule

@Component(modules = [DatabaseTestModule::class])
interface AppTestComponent : ApplicationComponent {

    companion object {
        val applicationComponent: AppTestComponent = DaggerAppTestComponent.builder()
            .databaseTestModule(DatabaseTestModule)
            .build()
    }

    fun inject(o: Any) {
        when (o) {
            is LatitudeDialogViewModelTestSuite -> inject(o)
        }
    }

    fun inject(latitudeDialogViewModelTestSuite: LatitudeDialogViewModelTestSuite)

}