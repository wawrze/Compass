package pl.wawra.compass.di.presentation.compass

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation.RELATIVE_TO_SELF
import android.view.animation.RotateAnimation
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.fragment_compass.*
import pl.wawra.compass.R

class CompassFragment : Fragment() {

    private lateinit var viewModel: CompassViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this).get(CompassViewModel::class.java)
        return inflater.inflate(R.layout.fragment_compass, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()
        setupClickListeners()
        viewModel.updateDegree()
    }

    private fun setupObservers() {
        viewModel.degreeChange.observe(
            viewLifecycleOwner,
            Observer { rotateImage(it.first, it.second) }
        )
    }

    private fun setupClickListeners() {
        fragment_compass_compass_image.setOnClickListener {
            viewModel.updateDegree((System.currentTimeMillis() % 360).toDouble())
        }
    }

    private fun rotateImage(previousDegree: Float, degree: Float) {
        fragment_compass_compass_image.startAnimation(
            RotateAnimation(
                previousDegree,
                degree,
                RELATIVE_TO_SELF,
                0.5f,
                RELATIVE_TO_SELF,
                0.5f
            ).apply {
                duration = 2000
                fillAfter = true
            }
        )
    }

}