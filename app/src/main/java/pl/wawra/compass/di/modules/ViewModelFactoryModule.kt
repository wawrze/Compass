package pl.wawra.compass.di.modules

import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import pl.wawra.compass.base.ViewModelProviderFactory

@Module
abstract class ViewModelFactoryModule {

    @Binds
    abstract fun bindViewModelFactory(viewModelFactory: ViewModelProviderFactory): ViewModelProvider.Factory

}