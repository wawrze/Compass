package pl.wawra.compass.database

import androidx.room.Database
import androidx.room.RoomDatabase
import pl.wawra.compass.database.daos.LocationDao
import pl.wawra.compass.database.entities.Location

@Database(
    entities = [Location::class],
    version = 1,
    exportSchema = false
)
abstract class Database : RoomDatabase() {

    abstract fun locationDao(): LocationDao

}