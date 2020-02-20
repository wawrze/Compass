package pl.wawra.compass.di.modules

import dagger.Module
import dagger.Provides
import org.mockito.Mockito.mock
import pl.wawra.compass.database.daos.LocationDao

@Module
object DatabaseTestModule {

    @Provides
    fun provideLocationDao(): LocationDao = mock(LocationDao::class.java)

}