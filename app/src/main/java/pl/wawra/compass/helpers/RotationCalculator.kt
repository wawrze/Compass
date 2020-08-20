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

    fun calculateRotations(
        newDegree: Float,
        targetLocation: Location?,
        lastLocation: Location
    ): Pair<RotationModel?, RotationModel?> {
        if (!isAnimationFinished()) return Pair(null, null)

        val calculatedDegree = (-Math.toDegrees(newDegree.toDouble()) + 360).toFloat() % 360

        val change = abs(calculatedDegree - lastCompassDegree)
        if (change < 10.0) return Pair(null, null)

        val toDegree = calculateToDegree(calculatedDegree, change)
        val fromDegree = calculateFromDegree(calculatedDegree, change)
        val animationLength = calculateAnimationLength(fromDegree, toDegree)

        val compassRotation = calculateCompassRotation(
            fromDegree,
            toDegree,
            calculatedDegree,
            animationLength
        )

        val targetMarkerRotation = calculateTargetMarkerRotation(
            targetLocation,
            lastLocation,
            calculatedDegree,
            animationLength
        )

        return Pair(compassRotation, targetMarkerRotation)
    }

    private fun calculateToDegree(calculatedDegree: Float, change: Float): Float =
        if (change > 180 && lastCompassDegree > calculatedDegree) {
            calculatedDegree + 360
        } else {
            calculatedDegree
        }

    private fun calculateFromDegree(calculatedDegree: Float, change: Float): Float =
        if (change > 180 && lastCompassDegree <= calculatedDegree) {
            lastCompassDegree + 360
        } else {
            lastCompassDegree
        }

    private fun calculateAnimationLength(fromDegree: Float, toDegree: Float): Long {
        var animationLength = abs(toDegree - fromDegree).toLong() * 10
        if (animationLength < 100) animationLength = 100
        return animationLength
    }

    private fun calculateTargetMarkerRotation(
        targetLocation: Location?,
        lastLocation: Location,
        calculatedDegree: Float,
        animationLength: Long
    ): RotationModel? {
        targetLocation ?: return null

        val targetDegreeFromNorth = calculateTargetDegree(
            targetLocation.lat,
            targetLocation.lon,
            lastLocation.lat,
            lastLocation.lon
        )
        val toDegree = (calculatedDegree + targetDegreeFromNorth) % 360
        lastTargetMarkerDegree = toDegree % 360

        return RotationModel(
            lastTargetMarkerDegree,
            toDegree,
            animationLength
        )
    }

    private fun calculateCompassRotation(
        fromDegree: Float,
        toDegree: Float,
        calculatedDegree: Float,
        animationLength: Long
    ): RotationModel {
        lastCompassDegree = calculatedDegree
        lastAnimationLength = animationLength

        return RotationModel(
            fromDegree,
            toDegree,
            animationLength
        )
    }

    private fun isAnimationFinished(): Boolean {
        val timestamp = System.currentTimeMillis()
        return if (lastCompassUpdate != 0L && timestamp - lastCompassUpdate < lastAnimationLength) {
            false
        } else {
            lastCompassUpdate = timestamp
            true
        }
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