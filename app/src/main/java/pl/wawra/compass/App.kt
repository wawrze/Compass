package pl.wawra.compass

import android.app.Application
import android.content.Context
import pl.wawra.compass.base.BaseViewModel
import pl.wawra.compass.di.components.ApplicationComponent
import pl.wawra.compass.di.components.DaggerApplicationComponent
import pl.wawra.compass.di.modules.DatabaseModule

open class App : Application() {

    var appComponent: ApplicationComponent? = null

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerApplicationComponent.builder()
            .databaseModule(DatabaseModule(this))
            .build()
        appComponent?.inject(this)
        BaseViewModel.setAppComponent(appComponent)
    }

    operator fun get(context: Context): App = context.applicationContext as App

}