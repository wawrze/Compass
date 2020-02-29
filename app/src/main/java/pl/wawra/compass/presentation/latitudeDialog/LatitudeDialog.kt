package pl.wawra.compass.presentation.latitudeDialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.dialog_latitude.*
import pl.wawra.compass.R
import pl.wawra.compass.base.BaseDialog

class LatitudeDialog(private val callBack: (() -> Unit)?) : BaseDialog() {

    private lateinit var viewModel: LatitudeDialogViewModel
    private lateinit var adapter: ArrayAdapter<String>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this).get(LatitudeDialogViewModel::class.java)
        viewModel.callBack = callBack
        context?.let { adapter = ArrayAdapter(it, R.layout.item_dropdown, ArrayList<String>()) }
        return inflater.inflate(R.layout.dialog_latitude, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClickListeners()
        viewModel.getPreviousLatitudes().observe { adapter.addAll(it) }
        setupInput()
    }

    private fun setupInput() {
        dialog_latitude_input.setAdapter(adapter)
        dialog_latitude_input.threshold = 1
        dialog_latitude_input.onFocusChangeListener = View.OnFocusChangeListener { _, b ->
            if (b) dialog_latitude_input.showDropDown()
        }
    }

    private fun setupClickListeners() {
        dialog_latitude_cancel_button.setOnClickListener { dismissAllowingStateLoss() }
        dialog_latitude_confirm_button.setOnClickListener {
            val latitude = dialog_latitude_input.text.toString()
            viewModel.verifyLatitude(latitude).observe {
                if (it == 0) {
                    dialog_latitude_input_error_message.visibility = View.GONE
                    onCorrectLatitude(latitude)
                } else {
                    dialog_latitude_input_error_message.text = getString(it)
                    dialog_latitude_input_error_message.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun onCorrectLatitude(latitude: String) {
        viewModel.insertNewLatitude(latitude).observe {
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
                    getString(R.string.unknown_error),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    companion object {
        fun createAndShow(fragmentManager: FragmentManager?, callBack: (() -> Unit)?) {
            val dialog = LatitudeDialog(callBack)
            fragmentManager?.let { dialog.show(it) }
        }
    }

}