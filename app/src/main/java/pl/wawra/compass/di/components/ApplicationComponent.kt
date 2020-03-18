package pl.wawra.compass.di.components

import dagger.Component
import pl.wawra.compass.App
import pl.wawra.compass.di.modules.DatabaseModule
import pl.wawra.compass.di.modules.GeocoderModule
import pl.wawra.compass.presentation.MainActivity
import pl.wawra.compass.presentation.compass.CompassViewModel
import pl.wawra.compass.presentation.targetDialog.TargetDialogViewModel

@Component(modules = [DatabaseModule::class, GeocoderModule::class])
interface ApplicationComponent {

    fun inject(mainActivity: MainActivity)
    fun inject(app: App)
    fun inject(compassViewModel: CompassViewModel)
    fun inject(targetDialogViewModel: TargetDialogViewModel)

}