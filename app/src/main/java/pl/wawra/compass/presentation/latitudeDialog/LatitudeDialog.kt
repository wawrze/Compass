package pl.wawra.compass.presentation.latitudeDialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.dialog_latitude.*
import pl.wawra.compass.R
import pl.wawra.compass.base.BaseDialog

class LatitudeDialog(private val callBack: (() -> Unit)?) : BaseDialog() {

    private lateinit var viewModel: LatitudeDialogViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this).get(LatitudeDialogViewModel::class.java)
        viewModel.callBack = callBack
        return inflater.inflate(R.layout.dialog_latitude, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClickListeners()
    }

    private fun setupClickListeners() {
        dialog_latitude_cancel_button.setOnClickListener { dismissAllowingStateLoss() }
        dialog_latitude_confirm_button.setOnClickListener {
            val latitude = dialog_latitude_input.text.toString()
            viewModel.insertNewLatitude(latitude).observe(
                viewLifecycleOwner,
                Observer {
                    if (it) {
                        Toast.makeText(
                            context,
                            getString(R.string.new_latitude_set),
                            Toast.LENGTH_LONG
                        ).show()
                        viewModel.callBack?.invoke()
                        dismissAllowingStateLoss()
                    } else {
                        Toast.makeText(
                            context,
                            getString(R.string.new_latitude_error),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            )
        }
    }

    companion object {
        fun createAndShow(fragmentManager: FragmentManager?, callBack: (() -> Unit)?) {
            val dialog = LatitudeDialog(callBack)
            fragmentManager?.let { dialog.show(it) }
        }
    }

}