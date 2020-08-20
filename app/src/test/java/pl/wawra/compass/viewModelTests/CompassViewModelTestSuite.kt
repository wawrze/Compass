package pl.wawra.compass.viewModelTests

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorManager
import android.location.Address
import io.mockk.*
import io.reactivex.Observable
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.anyFloat
import pl.wawra.compass.BaseTestSuite
import pl.wawra.compass.models.Location
import pl.wawra.compass.models.RotationModel
import pl.wawra.compass.presentation.compass.CompassViewModel


class CompassViewModelTestSuite : BaseTestSuite() {

    private lateinit var objectUnderTest: CompassViewModel

    @Before
    fun prepare() {
        objectUnderTest = CompassViewModel(mockk(), mockk(), mockk())
    }

    @Test
    fun shouldUpdateTargetLocation() {
        // given
        val location = Location(0.1234, 5.6789)
        every {
            objectUnderTest.locationRepository.getTargetLocation()
        } returns Observable.just(location)
        every {
            objectUnderTest.geocoder.getFromLocation(0.1234, 5.6789, 1)
        } returns ArrayList()
        // when
        objectUnderTest.updateTargetLocation()
        val result = objectUnderTest.targetLocationString.value
        // then
        assertEquals("0.1234, 5.6789", result)
    }

    @Test
    fun shouldNotUpdateTargetLocation() {
        // given
        every {
            objectUnderTest.locationRepository.getTargetLocation()
        } returns Observable.never()
        // when
        objectUnderTest.updateTargetLocation()
        val result = objectUnderTest.targetLocationString.value
        // then
        assertEquals("", result)
    }

    @Test
    fun shouldUpdateTargetAddress() {
        // given
        val location = Location(0.1234, 5.6789)
        val address = mockk<Address>()
        every { address.maxAddressLineIndex } returns 2
        every { address.getAddressLine(0) } returns "address line 0"
        every { address.getAddressLine(1) } returns "address line 1"
        every { address.getAddressLine(2) } returns "address line 2"
        every {
            objectUnderTest.locationRepository.getTargetLocation()
        } returns Observable.just(location)
        every {
            objectUnderTest.geocoder.getFromLocation(0.1234, 5.6789, 1)
        } returns listOf(address)
        // when
        objectUnderTest.updateTargetLocation()
        val result = objectUnderTest.targetAddressString.value
        // then
        assertEquals("address line 0\naddress line 1\naddress line 2", result)
    }

    @Test
    fun shouldHandleMagneticFieldSensorEvent() {
        // given
        val values = floatArrayOf(0F, 1F, 2F)
        val event = getFakeEvent(values, Sensor.TYPE_MAGNETIC_FIELD)
        mockkStatic(SensorManager::class)
        val magneticSensorValues = slot<FloatArray>()
        every {
            SensorManager.getRotationMatrix(any(), any(), any(), capture(magneticSensorValues))
        } returns false
        // when
        objectUnderTest.handleSensorEvent(event)
        // then
        verify(exactly = 1) { SensorManager.getRotationMatrix(any(), any(), any(), any()) }
        assertEquals(0F, magneticSensorValues.captured[0])
        assertEquals(0.5F, magneticSensorValues.captured[1])
        assertEquals(1.0F, magneticSensorValues.captured[2])
    }

    @Test
    fun shouldHandleAccelerometerSensorEvent() {
        // given
        val values = floatArrayOf(0F, 1F, 2F)
        val event = getFakeEvent(values, Sensor.TYPE_ACCELEROMETER)
        mockkStatic(SensorManager::class)
        val accelerometerValues = slot<FloatArray>()
        every {
            SensorManager.getRotationMatrix(any(), any(), capture(accelerometerValues), any())
        } returns false
        // when
        objectUnderTest.handleSensorEvent(event)
        // then
        verify(exactly = 1) { SensorManager.getRotationMatrix(any(), any(), any(), any()) }
        assertEquals(0F, accelerometerValues.captured[0])
        assertEquals(0.5F, accelerometerValues.captured[1])
        assertEquals(1.0F, accelerometerValues.captured[2])
    }

    @Test
    fun shouldHandleOtherSensorEvent() {
        // given
        val values = FloatArray(0)
        val event = getFakeEvent(values, 0)
        mockkStatic(SensorManager::class)
        every {
            SensorManager.getRotationMatrix(any(), any(), any(), any())
        } returns false
        // when
        objectUnderTest.handleSensorEvent(event)
        // then
        verify(exactly = 0) { SensorManager.getRotationMatrix(any(), any(), any(), any()) }
    }

    @Test
    fun shouldUpdateRotations() {
        // given
        val magneticEvent = getFakeEvent(floatArrayOf(0F, 1F, 2F), Sensor.TYPE_MAGNETIC_FIELD)
        val accelerometerEvent = getFakeEvent(floatArrayOf(3F, 4F, 5F), Sensor.TYPE_ACCELEROMETER)
        mockkStatic(SensorManager::class)
        every {
            SensorManager.getOrientation(any(), any())
        } returns FloatArray(0)
        every {
            SensorManager.getRotationMatrix(any(), any(), any(), any())
        } returns true
        every {
            objectUnderTest.rotationCalculator.calculateRotations(anyFloat(), any(), any())
        } returns Pair(
            RotationModel(7F, 8F, 10L),
            RotationModel(9F, 0.1F, 11L)
        )
        // when
        objectUnderTest.handleSensorEvent(accelerometerEvent)
        objectUnderTest.handleSensorEvent(magneticEvent)
        val compassRotation = objectUnderTest.compassRotation.value
        val targetMarkerRotation = objectUnderTest.targetMarkerRotation.value
        // then
        verify(exactly = 2) { SensorManager.getRotationMatrix(any(), any(), any(), any()) }
        assertEquals(7F, compassRotation?.fromDegree)
        assertEquals(8F, compassRotation?.toDegree)
        assertEquals(10L, compassRotation?.animationLength)
        assertEquals(9F, targetMarkerRotation?.fromDegree)
        assertEquals(0.1F, targetMarkerRotation?.toDegree)
        assertEquals(11L, targetMarkerRotation?.animationLength)
    }

    @Test
    fun shouldUpdateLocation() {
        // given
        val lastLocationField = objectUnderTest::class.java.getDeclaredField("lastLocation").apply {
            isAccessible = true
        }
        // when
        objectUnderTest.updateLocation(0.1234, 5.6789)
        val result = lastLocationField.get(objectUnderTest) as Location
        // then
        assertEquals(0.1234, result.lat, 0.0)
        assertEquals(5.6789, result.lon, 0.0)
    }

    @Test
    fun shouldNotUpdateLocation() {
        // given
        val lastLocationField = objectUnderTest::class.java.getDeclaredField("lastLocation").apply {
            isAccessible = true
        }
        // when
        objectUnderTest.updateLocation(0.1234, 5.6789)
        objectUnderTest.updateLocation(0.0, 0.0)
        val result = lastLocationField.get(objectUnderTest) as Location
        // then
        assertEquals(0.1234, result.lat, 0.0)
        assertEquals(5.6789, result.lon, 0.0)
    }

    private fun getFakeEvent(values: FloatArray, type: Int): SensorEvent {
        val sensorEvent = mockk<SensorEvent>()
        SensorEvent::class.java.getField("sensor").apply {
            isAccessible = true
            val sensor = mockk<Sensor>()
            every { sensor.type } returns type
            set(sensorEvent, sensor)
        }
        SensorEvent::class.java.getField("values").apply {
            isAccessible = true
            set(sensorEvent, values)
        }
        return sensorEvent
    }

}