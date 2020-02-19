package pl.wawra.compass.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Longitude(
    @PrimaryKey(autoGenerate = true) var longitudeId: Long = 0L,
    var value: Double = 0.0,
    var isActive: Boolean = false
)