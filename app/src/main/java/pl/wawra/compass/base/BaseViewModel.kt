package pl.wawra.compass.base

import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import pl.wawra.compass.di.components.ApplicationComponent
import pl.wawra.compass.presentation.compass.CompassViewModel
import pl.wawra.compass.presentation.latitudeDialog.LatitudeDialogViewModel
import pl.wawra.compass.presentation.longitudeDialog.LongitudeDialogViewModel

abstract class BaseViewModel : ViewModel() {

    companion object {
        private var appComponent: ApplicationComponent? = null

        fun setAppComponent(appComponent: ApplicationComponent?) {
            this.appComponent = appComponent
        }
    }

    private val compositeDisposable = CompositeDisposable()

    init {
        @Suppress("LeakingThis")
        when (this) {
            is CompassViewModel -> appComponent?.inject(this)
            is LatitudeDialogViewModel -> appComponent?.inject(this)
            is LongitudeDialogViewModel -> appComponent?.inject(this)
        }
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }

    fun Disposable.addToDisposables() {
        compositeDisposable.add(this)
    }

}