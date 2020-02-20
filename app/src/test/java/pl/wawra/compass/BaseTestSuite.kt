package pl.wawra.compass

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import io.reactivex.Scheduler
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.disposables.Disposable
import io.reactivex.internal.schedulers.ExecutorScheduler
import io.reactivex.plugins.RxJavaPlugins
import org.junit.Rule
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import pl.wawra.compass.base.BaseViewModel
import pl.wawra.compass.di.AppTestComponent
import java.util.concurrent.Executor
import java.util.concurrent.TimeUnit

@RunWith(JUnit4::class)
abstract class BaseTestSuite {

    init {
        val appComponent = AppTestComponent.applicationComponent
        BaseViewModel.setAppComponent(appComponent)

        @Suppress("LeakingThis")
        appComponent.inject(this)

        val immediate = object : Scheduler() {
            override fun scheduleDirect(run: Runnable, delay: Long, unit: TimeUnit): Disposable {
                return super.scheduleDirect(run, 0, unit)
            }

            override fun createWorker(): Worker {
                return ExecutorScheduler.ExecutorWorker(Executor { it.run() }, false)
            }
        }
        RxJavaPlugins.setInitIoSchedulerHandler { immediate }
        RxJavaPlugins.setInitComputationSchedulerHandler { immediate }
        RxJavaPlugins.setInitNewThreadSchedulerHandler { immediate }
        RxJavaPlugins.setInitSingleSchedulerHandler { immediate }
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { immediate }
    }

    @get:Rule
    val taskExecutorRule = InstantTaskExecutorRule()

}