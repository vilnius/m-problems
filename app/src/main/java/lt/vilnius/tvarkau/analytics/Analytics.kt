package lt.vilnius.tvarkau.analytics

import android.app.Activity
import android.support.v4.app.Fragment
import lt.vilnius.tvarkau.entity.Problem

/**
 * @author Martynas Jurkus
 */
interface Analytics {

    fun trackCurrentFragment(activity: Activity, fragment: Fragment)

    fun trackViewProblem(problem: Problem)

    fun trackReportRegistration(reportType: String, photoCount: Int)

    fun trackReportValidation(validationError: String)

    fun trackPersonalDataSharingEnabled(enabled: Boolean)

    fun trackLogIn()
}