package pl.wawra.compass.di.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import pl.wawra.compass.R
import pl.wawra.compass.di.components.Injector
import pl.wawra.compass.di.modules.DatabaseModule

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Injector.injector
            .databaseModule(DatabaseModule(this))
        setContentView(R.layout.activity_main)
    }

}