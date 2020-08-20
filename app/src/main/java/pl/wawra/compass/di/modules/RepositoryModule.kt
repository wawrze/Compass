package pl.wawra.compass.di.modules

import dagger.Module
import dagger.Provides
import pl.wawra.compass.database.daos.LocationDao
import pl.wawra.compass.di.scopes.AppScoped
import pl.wawra.compass.repositories.LocationRepository

@Module
class RepositoryModule {

    @AppScoped
    @Provides
    fun provideLocationRepository(locationDao: LocationDao): LocationRepository =
        LocationRepository(locationDao)

}