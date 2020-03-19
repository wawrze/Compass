package pl.wawra.compass.base

import android.widget.Toast
import androidx.annotation.MainThread
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.navigation.NavController

abstract class BaseFragment : Fragment() {

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
    protected fun <T> MutableLiveData<T>.observe(action: (T) -> Unit) {
        this.observe(
            this@BaseFragment.viewLifecycleOwner,
            Observer { action.invoke(it) }
        )
    }

    fun showToast(res: Int) {
        Toast.makeText(context, res, Toast.LENGTH_LONG).show()
    }

}