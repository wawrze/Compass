package pl.wawra.compass

import android.app.Application
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.runner.RunWith
import pl.wawra.compass.database.Database

@RunWith(AndroidJUnit4::class)
abstract class BaseDaoTestSuite {

    protected var db: Database = Room.inMemoryDatabaseBuilder(
        InstrumentationRegistry.getInstrumentation().targetContext.applicationContext as Application,
        Database::class.java
    ).allowMainThreadQueries().build()

}