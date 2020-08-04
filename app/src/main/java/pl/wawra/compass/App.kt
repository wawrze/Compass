package pl.wawra.compass

import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import pl.wawra.compass.di.components.DaggerApplicationComponent

open class App : DaggerApplication() {

    override fun applicationInjector(): AndroidInjector<out DaggerApplication>? {
        return DaggerApplicationComponent.builder().application(this).build()
    }

}