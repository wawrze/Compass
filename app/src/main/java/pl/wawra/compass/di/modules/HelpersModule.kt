package pl.wawra.compass.di.modules

import dagger.Module
import dagger.Provides
import pl.wawra.compass.helpers.RotationCalculator

@Module
class HelpersModule {

    private lateinit var calculator: RotationCalculator

    @Provides
    fun provideRotationCalculator(): RotationCalculator = if (!::calculator.isInitialized) {
        calculator = RotationCalculator()
        calculator
    } else {
        calculator
    }

}