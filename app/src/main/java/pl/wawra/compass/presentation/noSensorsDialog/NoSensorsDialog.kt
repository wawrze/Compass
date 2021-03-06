package pl.wawra.compass.presentation.noSensorsDialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.dialog_no_sensors.*
import pl.wawra.compass.R
import pl.wawra.compass.base.BaseDialog

class NoSensorsDialog : BaseDialog() {

    init {
        isCancelable = false
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.dialog_no_sensors, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog_no_sensors_close_button.setOnClickListener {
            dismissAllowingStateLoss()
            activity?.finishAffinity()
        }
    }

}