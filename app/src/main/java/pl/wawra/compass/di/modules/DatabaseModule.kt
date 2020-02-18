package pl.wawra.compass.di.modules

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import pl.wawra.compass.database.Database
import pl.wawra.compass.database.daos.LocationDao

@Module
class DatabaseModule(private val context: Context) {

    companion object {
        private const val DB_NAME = "compass.db"
    }

    private lateinit var dataBase: Database

    @Provides
    fun provideDataBase(): Database {
        if (!::dataBase.isInitialized) {
            dataBase = Room.databaseBuilder(context, Database::class.java, DB_NAME)
                .build()
        }
        return dataBase
    }

    @Provides
    fun provideLocationDao(database: Database): LocationDao = database.locationDao()

}