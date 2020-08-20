package pl.wawra.compass.di.components

import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import pl.wawra.compass.App
import pl.wawra.compass.di.modules.*
import pl.wawra.compass.di.scopes.AppScoped

@AppScoped
@Component(
    modules = [
        AndroidSupportInjectionModule::class,
        DatabaseModule::class,
        FragmentBuilderModule::class,
        HelpersModule::class,
        RepositoryModule::class,
        ViewModelFactoryModule::class
    ]
)
interface ApplicationComponent : AndroidInjector<App> {

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun application(application: App): Builder

        fun build(): ApplicationComponent

    }

}