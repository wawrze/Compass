package pl.wawra.compass.di.modules

import dagger.Module
import dagger.android.ContributesAndroidInjector
import pl.wawra.compass.di.scopes.FragmentScoped
import pl.wawra.compass.presentation.compass.CompassFragment
import pl.wawra.compass.presentation.targetDialog.TargetDialog

@Module
abstract class FragmentBuilderModule {

    @FragmentScoped
    @ContributesAndroidInjector(modules = [ViewModelModule::class])
    abstract fun contributeCompassFragment(): CompassFragment?

    @FragmentScoped
    @ContributesAndroidInjector(modules = [ViewModelModule::class])
    abstract fun contributeTargetDialog(): TargetDialog?

}