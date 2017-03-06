package lt.vilnius.tvarkau.fragments

import android.os.Bundle
import android.view.*
import kotlinx.android.synthetic.main.fragment_all_reports_list.*
import kotlinx.android.synthetic.main.include_report_list_recycler_view.*
import lt.vilnius.tvarkau.R
import lt.vilnius.tvarkau.dagger.component.ApplicationComponent
import lt.vilnius.tvarkau.entity.Problem
import lt.vilnius.tvarkau.extensions.gone
import lt.vilnius.tvarkau.extensions.visible
import lt.vilnius.tvarkau.fragments.interactors.AllReportListInteractor
import lt.vilnius.tvarkau.fragments.presenters.AllReportsListPresenterImpl
import lt.vilnius.tvarkau.fragments.presenters.ProblemListPresenter
import lt.vilnius.tvarkau.fragments.views.ReportListView
import lt.vilnius.tvarkau.prefs.Preferences
import lt.vilnius.tvarkau.prefs.StringPreference
import lt.vilnius.tvarkau.widgets.EndlessScrollListener
import javax.inject.Inject
import javax.inject.Named

/**
 * @author Martynas Jurkus
 */
class AllReportsListFragment : BaseReportListFragment(), ReportListView {

    @field:[Inject Named(Preferences.LIST_SELECTED_FILTER_REPORT_STATUS)]
    lateinit var reportStatus: StringPreference
    @field:[Inject Named(Preferences.LIST_SELECTED_FILTER_REPORT_TYPE)]
    lateinit var reportType: StringPreference

    private lateinit var scrollListener: EndlessScrollListener

    override val presenter: ProblemListPresenter by lazy {
        AllReportsListPresenterImpl(
                AllReportListInteractor(
                        legacyApiService,
                        ioScheduler,
                        reportType,
                        reportStatus,
                        getString(R.string.report_filter_all_report_types)
                ),
                uiScheduler,
                this,
                connectivityProvider
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_all_reports_list, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        scrollListener = EndlessScrollListener({ getReports() })
        report_list.addOnScrollListener(scrollListener)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        baseActivity?.setTitle(R.string.home_list_of_reports)
        baseActivity?.supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back)
    }

    override fun onInject(component: ApplicationComponent) {
        component.inject(this)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.main_toolbar_menu, menu)
    }

    override fun getReports() {
        if (scrollListener.isLoading) return
        scrollListener.isLoading = true
        super.getReports()
    }

    override fun reloadData() {
        super.reloadData()
        scrollListener.isLoading = false
    }

    override fun onReportsLoaded(reports: List<Problem>) {
        super.onReportsLoaded(reports)
        swipe_container.isRefreshing = false
    }

    override fun showProgress() {
        super.showProgress()
        scrollListener.isLoading = true
    }

    override fun hideProgress() {
        super.hideProgress()
        scrollListener.isLoading = false
    }

    override fun showEmptyState() {
        all_reports_empty_state.visible()
    }

    override fun hideEmptyState() {
        all_reports_empty_state.gone()
    }

    companion object {
        fun newInstance() = AllReportsListFragment()
    }
}