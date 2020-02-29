package pl.wawra.compass.base

import androidx.annotation.MainThread
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer

abstract class BaseFragment : Fragment() {

    @MainThread
    protected fun <T> MutableLiveData<T>.observe(action: (T) -> Unit) {
        this.observe(
            this@BaseFragment.viewLifecycleOwner,
            Observer { action.invoke(it) }
        )
    }

}