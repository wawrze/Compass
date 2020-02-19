package pl.wawra.compass.base

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.InsetDrawable
import android.os.Bundle
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import pl.wawra.compass.R

abstract class BaseDialog : DialogFragment() {

    fun show(fragmentManager: FragmentManager?) {
        fragmentManager?.let { super.show(it, "") }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
        val margin = resources.getDimension(R.dimen.base_dialog_margin).toInt()
        dialog.window?.setBackgroundDrawable(
            InsetDrawable(ColorDrawable(Color.TRANSPARENT), margin)
        )
        dialog.setCancelable(true)
        dialog.show()

        return dialog
    }

    override fun onResume() {
        super.onResume()
        val params = dialog?.window?.attributes
        params?.width = ViewGroup.LayoutParams.MATCH_PARENT
        params?.height = ViewGroup.LayoutParams.WRAP_CONTENT
        dialog?.window?.attributes = params as android.view.WindowManager.LayoutParams
    }

}