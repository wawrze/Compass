package pl.wawra.compass.di.modules

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import pl.wawra.compass.di.scopes.ViewModelKey
import pl.wawra.compass.presentation.compass.CompassViewModel
import pl.wawra.compass.presentation.targetDialog.TargetDialogViewModel

@Module
abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(CompassViewModel::class)
    abstract fun bindCompassViewModel(compassViewModel: CompassViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(TargetDialogViewModel::class)
    abstract fun bindTargetDialogViewModel(targetDialogViewModel: TargetDialogViewModel): ViewModel

}