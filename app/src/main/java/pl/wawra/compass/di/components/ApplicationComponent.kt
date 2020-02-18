package pl.wawra.compass.di.components

import dagger.Component
import pl.wawra.compass.di.modules.DatabaseModule
import pl.wawra.compass.di.presentation.MainActivity
import pl.wawra.compass.di.presentation.compass.CompassViewModel

@Component(modules = [DatabaseModule::class])
interface ApplicationComponent {

    fun inject(mainActivity: MainActivity)
    fun inject(compassViewModel: CompassViewModel)

}