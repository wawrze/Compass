package pl.wawra.compass.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Location(
    val lat: Double,
    val lon: Double,
    @PrimaryKey(autoGenerate = true) var id: Long = 0L
)