package pl.wawra.compass.presentation.latitudeDialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.dialog_latitude.*
import pl.wawra.compass.R
import pl.wawra.compass.base.BaseDialog

class LatitudeDialog : BaseDialog() {

    private lateinit var viewModel: LatitudeDialogViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this).get(LatitudeDialogViewModel::class.java)
        return inflater.inflate(R.layout.dialog_latitude, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClickListeners()
    }

    private fun setupClickListeners() {
        dialog_latitude_cancel_button.setOnClickListener { dismissAllowingStateLoss() }
        dialog_latitude_confirm_button.setOnClickListener { dismissAllowingStateLoss() }
    }

    companion object {
        fun createAndShow(fragmentManager: FragmentManager?) {
            val dialog = LatitudeDialog()
            fragmentManager?.let { dialog.show(it) }
        }
    }

}