package pl.wawra.compass.di.components

import pl.wawra.compass.di.presentation.MainActivity
import pl.wawra.compass.di.presentation.compass.CompassViewModel

object Injector {

    @Suppress("HasPlatformType")
    val injector = DaggerApplicationComponent.builder()

    fun inject(o: Any) {
        when (o) {
            is MainActivity -> injector.build().inject(o)
            is CompassViewModel -> injector.build().inject(o)
        }
    }

}