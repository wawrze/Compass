package pl.wawra.compass.testsDI.modules

import dagger.Module
import dagger.Provides
import org.mockito.Mockito.mock
import pl.wawra.compass.helpers.RotationCalculator

@Module
object HelpersTestModule {

    @Provides
    fun provideRotationCalculator(): RotationCalculator = mock(RotationCalculator::class.java)

}