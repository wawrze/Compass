package pl.wawra.compass.base

import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import pl.wawra.compass.di.components.Injector

abstract class BaseViewModel : ViewModel() {

    private val compositeDisposable = CompositeDisposable()

    init {
        @Suppress("LeakingThis")
        Injector.inject(this)
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }

    fun Disposable.addToDisposables() {
        compositeDisposable.add(this)
    }

}