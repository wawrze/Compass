package pl.wawra.compass.base

import android.content.Context
import android.widget.Toast
import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.navigation.NavController
import dagger.android.support.DaggerFragment

abstract class BaseFragment : DaggerFragment() {

    protected var navigate: NavController? = null

    override fun onResume() {
        super.onResume()
        navigate = (activity as? Navigation)?.getNavigationController()
    }

    override fun onPause() {
        super.onPause()
        navigate = null
    }

    @MainThread
    protected fun <T> LiveData<T>.observe(action: (T) -> Unit) {
        this.observe(
            this@BaseFragment.viewLifecycleOwner,
            { action.invoke(it) }
        )
    }

    protected fun Context.showToast(res: Int) {
        Toast.makeText(this, res, Toast.LENGTH_LONG).show()
    }

}