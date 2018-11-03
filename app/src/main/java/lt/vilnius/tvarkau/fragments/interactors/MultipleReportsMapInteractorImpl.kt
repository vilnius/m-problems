package lt.vilnius.tvarkau.fragments.interactors

import com.vinted.preferx.IntPreference
import io.reactivex.Scheduler
import io.reactivex.Single
import lt.vilnius.tvarkau.backend.GetProblemsParams
import lt.vilnius.tvarkau.backend.LegacyApiService
import lt.vilnius.tvarkau.backend.requests.GetReportListRequest
import lt.vilnius.tvarkau.entity.Problem
import lt.vilnius.tvarkau.events_listeners.RefreshReportFilterEvent
import lt.vilnius.tvarkau.rx.RxBus

class MultipleReportsMapInteractorImpl(
    private val api: LegacyApiService,
    private val ioScheduler: Scheduler,
    private val reportType: IntPreference,
    private val reportStatus: IntPreference,
    private val allReportTypes: String
) : MultipleReportsMapInteractor {

    private val cachedReports = mutableListOf<Problem>()

    init {
        RxBus.observable
                .filter { it is RefreshReportFilterEvent }
                .subscribe({
                    cachedReports.clear()
                })
    }

    override fun getReports(): Single<List<Problem>> {
        return Single.concat(
                Single.just(cachedReports),
                newRequest()
        )
                .filter { it.isNotEmpty() }
                .firstOrError()
                .subscribeOn(ioScheduler)
    }

    private fun newRequest(): Single<List<Problem>> {
//        val mappedStatus = reportStatus.get().emptyToNull()

//        val mappedType = when (reportType.get()) {
//            allReportTypes -> null
//            else -> reportType.get().emptyToNull()
//        }

        val params = GetProblemsParams.Builder()
                .setStart(0)
                .setLimit(PROBLEM_COUNT_LIMIT_IN_MAP)
//                .setStatusFilter(mappedStatus)
//                .setTypeFilter(mappedType)
                .create()

        return api.getProblems(GetReportListRequest(params))
                .map { it.result }
                .doOnSuccess { reports ->
                    if (reports.isEmpty()) {
//                        val msg = "Empty report list returned for status: $mappedStatus type: $mappedType"
//                        throw NoMapReportsError(msg)
                    }
                }
                .doOnSuccess {
                    cachedReports.clear()
                    cachedReports.addAll(it)
                }
    }

    companion object {
        private const val PROBLEM_COUNT_LIMIT_IN_MAP = 200
    }

    inner class NoMapReportsError(message: String) : RuntimeException(message)
}
