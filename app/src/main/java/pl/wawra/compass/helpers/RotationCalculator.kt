package pl.wawra.compass.helpers

import pl.wawra.compass.models.Location
import pl.wawra.compass.models.RotationModel
import kotlin.math.abs
import kotlin.math.atan

class RotationCalculator {

    private var lastCompassUpdate: Long = 0L
    private var lastAnimationLength: Long = 0L
    private var lastCompassDegree: Float = 0.0F
    private var lastTargetMarkerDegree: Float = 0.0F

    // TODO: try to split to few functions
    fun calculateRotations(
        newDegree: Float,
        targetLocation: Location?,
        lastLocation: Location
    ): Pair<RotationModel?, RotationModel?> {
        val timestamp = System.currentTimeMillis()
        if (lastCompassUpdate != 0L && timestamp - lastCompassUpdate < lastAnimationLength) return Pair(
            null,
            null
        )
        lastCompassUpdate = timestamp

        val calculatedDegree = (-Math.toDegrees(newDegree.toDouble()) + 360).toFloat() % 360

        val change = abs(calculatedDegree - lastCompassDegree)
        if (change < 10.0) return Pair(null, null)

        var toDegree = calculatedDegree
        var fromDegree = lastCompassDegree
        if (change > 180) {
            if (lastCompassDegree > calculatedDegree) {
                toDegree += 360
            } else {
                fromDegree += 360
            }
        }

        var animationLength = abs(toDegree - fromDegree).toLong() * 10
        if (animationLength < 100) animationLength = 100

        lastCompassDegree = calculatedDegree
        lastAnimationLength = animationLength
        val compassRotation = RotationModel(
            fromDegree,
            toDegree,
            animationLength
        )

        targetLocation ?: return Pair(compassRotation, null)
        val targetDegreeFromNorth = calculateTargetDegree(
            targetLocation.lat,
            targetLocation.lon,
            lastLocation.lat,
            lastLocation.lon
        )
        toDegree = (calculatedDegree + targetDegreeFromNorth) % 360
        lastTargetMarkerDegree = toDegree % 360
        val targetMarkerRotation = RotationModel(
            lastTargetMarkerDegree,
            toDegree,
            animationLength
        )

        return Pair(compassRotation, targetMarkerRotation)
    }

    private fun calculateTargetDegree(
        targetLat: Double,
        targetLon: Double,
        currentLat: Double,
        currentLon: Double
    ): Float {
        val targetY = ((targetLat - currentLat) * 10000000).toLong().toDouble()
        if (targetY == 0.0) return 0F
        val targetX = ((targetLon - currentLon) * 10000000).toLong().toDouble()

        var degrees = (radiansToDegrees(atan(targetX / targetY)) + 360) % 360
        if (targetY < 0) degrees += (if (targetX < 0) 180 else -180)

        return degrees.toFloat()
    }

    private fun radiansToDegrees(rad: Double): Double {
        return rad * 180.0 / Math.PI
    }

}