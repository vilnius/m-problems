package lt.vilnius.tvarkau.dagger.component

import android.support.v7.app.AppCompatActivity
import dagger.Subcomponent
import lt.vilnius.tvarkau.TestActivity
import lt.vilnius.tvarkau.dagger.module.TestActivityModule
import lt.vilnius.tvarkau.fragments.NewReportFragmentTest

@Subcomponent(
    modules = [
        TestActivityModule::class
    ]
)
interface TestActivityComponent : ActivityComponent {

    companion object {
        fun init(
            applicationComponent: TestApplicationComponent,
            activity: AppCompatActivity
        ): TestActivityComponent {
            return applicationComponent.activityComponent(TestActivityModule(activity))
        }
    }

    fun inject(activity: TestActivity)

    fun inject(test: NewReportFragmentTest)
}
