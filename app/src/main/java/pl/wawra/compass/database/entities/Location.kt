package pl.wawra.compass.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Location(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val lon: Double,
    val lat: Double
)