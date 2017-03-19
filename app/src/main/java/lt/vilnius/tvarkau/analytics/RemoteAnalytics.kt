package lt.vilnius.tvarkau.analytics

import android.app.Activity
import android.content.Context
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.mixpanel.android.mpmetrics.MixpanelAPI
import lt.vilnius.tvarkau.entity.Problem
import org.json.JSONObject

class RemoteAnalytics(appContext: Context, private val mixpanelAPI: MixpanelAPI) : Analytics {

    override fun trackApplyReportFilter(status: String, category: String, target: String) {
        val params = Bundle().apply {
            putString(PARAM_PROBLEM_STATUS, status)
            putString(PARAM_REPORT_CATEGORY, category)
            putString(PARAM_REPORT_FILTER_TARGET, target)
        }

        logEvent(EVENT_APPLY_REPORT_FILTER, params)
    }

    private val analytics: FirebaseAnalytics by lazy {
        FirebaseAnalytics.getInstance(appContext)
    }

    override fun trackOpenFragment(activity: Activity, name: String) {
        val eventName = "open_$name"

        timeEvent(eventName)
        analytics.setCurrentScreen(activity, eventName, null)
    }

    override fun trackCloseFragment(name: String) = finishTimeEvent("open_$name")

    override fun trackViewProblem(problem: Problem) = Bundle().run {
        putString(FirebaseAnalytics.Param.ITEM_ID, problem.problemId)
        putString(FirebaseAnalytics.Param.ITEM_NAME, problem.id)
        putString(FirebaseAnalytics.Param.ITEM_CATEGORY, problem.getType())
        putString(PARAM_PROBLEM_STATUS, problem.status)

        logEvent(FirebaseAnalytics.Event.VIEW_ITEM, this)
    }

    override fun trackReportRegistration(reportType: String, photoCount: Int) {
        val params = Bundle().apply {
            putString(FirebaseAnalytics.Param.CONTENT_TYPE, reportType)
            putInt(PARAM_PHOTO_COUNT, photoCount)
        }

        logEvent(EVENT_NEW_REPORT, params)
    }

    override fun trackReportValidation(validationError: String) {
        val params = Bundle().apply {
            putString(PARAM_VALIDATION_MESSAGE, validationError)
        }

        logEvent(EVENT_VALIDATION_ERROR, params)
    }

    override fun trackPersonalDataSharingEnabled(enabled: Boolean) {
        val params = Bundle().apply {
            putBoolean(PARAM_ENABLED, enabled)
        }

        setUserProperty(PROPERTY_SHARE_PERSONAL_DATA, enabled.toString())
        logEvent(EVENT_SHARE_PERSONAL_DATA, params)
    }

    override fun trackLogIn() =
            logEvent(FirebaseAnalytics.Event.LOGIN, Bundle.EMPTY)

    private fun logEvent(name: String, bundle: Bundle) {
        analytics.logEvent(name, bundle)

        val properties = bundle.keySet().map { key -> key to bundle[key] }.toMap()
        mixpanelAPI.trackMap(name, properties)
    }

    private fun setUserProperty(name: String, value: String) {
        val userProperties = listOf(name to value).toMap()

        mixpanelAPI.registerSuperProperties(JSONObject(userProperties))
    }

    private fun timeEvent(name: String) {
        mixpanelAPI.timeEvent(name)
    }

    private fun finishTimeEvent(name: String) {
        mixpanelAPI.track(name)
    }

    companion object {
        private const val PARAM_PROBLEM_STATUS = "problem_status"
        private const val PARAM_PHOTO_COUNT = "photo_count"
        private const val PARAM_VALIDATION_MESSAGE = "validation_message"
        private const val PARAM_ENABLED = "enabled"
        private const val PARAM_REPORT_CATEGORY = "report_category"
        private const val PARAM_REPORT_FILTER_TARGET = "report_filter_target"

        private const val EVENT_NEW_REPORT = "new_report"
        private const val EVENT_VALIDATION_ERROR = "validation_error"
        private const val EVENT_SHARE_PERSONAL_DATA = "personal_data"
        private const val EVENT_APPLY_REPORT_FILTER = "apply_report_filter"

        private const val PROPERTY_SHARE_PERSONAL_DATA = "share_personal_data"
    }
}