package pl.wawra.compass.helpersTests

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import pl.wawra.compass.BaseTestSuite
import pl.wawra.compass.helpers.RotationCalculator
import pl.wawra.compass.models.Location

class RotationCalculatorTestSuite : BaseTestSuite() {

    private lateinit var objectUnderTest: RotationCalculator

    @Before
    fun prepare() {
        objectUnderTest = RotationCalculator()
    }

    @Test
    fun shouldCalculateRotations() {
        // given
        val newDegree = 1.23F
        val targetLocation = Location(0.1, 2.3)
        val lastLocation = Location(4.5, 6.7)
        // when
        val result = objectUnderTest.calculateRotations(newDegree, targetLocation, lastLocation)
        // then
        assertEquals(360.0F, result.first?.fromDegree)
        assertEquals(289.52618F, result.first?.toDegree)
        assertEquals(700L, result.first?.animationLength)
        assertEquals(154.52618F, result.second?.fromDegree)
        assertEquals(154.52618F, result.second?.toDegree)
        assertEquals(700L, result.second?.animationLength)
    }

    @Test
    fun shouldNotCalculateRotationsChangeToSmall() {
        // given
        val newDegree = 0F
        val targetLocation = Location(0.1, 2.3)
        val lastLocation = Location(4.5, 6.7)
        // when
        val result = objectUnderTest.calculateRotations(newDegree, targetLocation, lastLocation)
        // then
        assertNull(result.first)
        assertNull(result.second)
    }

    @Test
    fun shouldNotCalculateRotationsAnimationNotFinished() {
        // given
        val newDegree = 5F
        val targetLocation = Location(0.1, 2.3)
        val lastLocation = Location(4.5, 6.7)
        // when
        objectUnderTest.calculateRotations(1.23F, targetLocation, lastLocation)
        val result = objectUnderTest.calculateRotations(newDegree, targetLocation, lastLocation)
        // then
        assertNull(result.first)
        assertNull(result.second)
    }

    @Test
    fun shouldCalculateRotationsToOver360() {
        // given
        objectUnderTest::class.java.getDeclaredField("lastCompassDegree").apply {
            isAccessible = true
            set(objectUnderTest, 300F)
        }
        val newDegree = 6.0F
        val targetLocation = Location(0.1, 2.3)
        val lastLocation = Location(4.5, 6.7)
        // when
        val result = objectUnderTest.calculateRotations(newDegree, targetLocation, lastLocation)
        // then
        assertEquals(300.0F, result.first?.fromDegree)
        assertEquals(376.2253F, result.first?.toDegree)
        assertEquals(760L, result.first?.animationLength)
        assertEquals(241.22533F, result.second?.fromDegree)
        assertEquals(241.22533F, result.second?.toDegree)
        assertEquals(760L, result.second?.animationLength)
    }

    @Test
    fun shouldCalculateRotationsFromOver360() {
        // given
        val newDegree = 6.0F
        val targetLocation = Location(0.1, 2.3)
        val lastLocation = Location(4.5, 6.7)
        // when
        val result = objectUnderTest.calculateRotations(newDegree, targetLocation, lastLocation)
        // then
        assertEquals(0.0F, result.first?.fromDegree)
        assertEquals(16.225323F, result.first?.toDegree)
        assertEquals(160L, result.first?.animationLength)
        assertEquals(241.22533F, result.second?.fromDegree)
        assertEquals(241.22533F, result.second?.toDegree)
        assertEquals(160L, result.second?.animationLength)
    }

    @Test
    fun shouldCalculateRotationsWithShortAnimation() {
        // given
        val newDegree = 0.12F
        val targetLocation = Location(0.1, 2.3)
        val lastLocation = Location(4.5, 6.7)
        // when
        val result = objectUnderTest.calculateRotations(newDegree, targetLocation, lastLocation)
        // then
        assertEquals(360.0F, result.first?.fromDegree)
        assertEquals(353.1245F, result.first?.toDegree)
        assertEquals(100L, result.first?.animationLength)
        assertEquals(218.12451F, result.second?.fromDegree)
        assertEquals(218.12451F, result.second?.toDegree)
        assertEquals(100L, result.second?.animationLength)
    }

    @Test
    fun shouldCalculateOnlyCompassRotation() {
        // given
        val newDegree = 1.23F
        val targetLocation = null
        val lastLocation = Location(4.5, 6.7)
        // when
        val result = objectUnderTest.calculateRotations(newDegree, targetLocation, lastLocation)
        // then
        assertEquals(360.0F, result.first?.fromDegree)
        assertEquals(289.52618F, result.first?.toDegree)
        assertEquals(700L, result.first?.animationLength)
        assertNull(result.second)
    }

}