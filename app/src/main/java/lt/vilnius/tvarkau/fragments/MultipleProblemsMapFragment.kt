package lt.vilnius.tvarkau.fragments

import android.location.Location
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import kotlinx.android.synthetic.main.fragment_map_fragment.*
import kotlinx.android.synthetic.main.loading_indicator.*
import lt.vilnius.tvarkau.R
import lt.vilnius.tvarkau.activity.ActivityConstants
import lt.vilnius.tvarkau.dagger.component.ActivityComponent
import lt.vilnius.tvarkau.entity.Problem
import lt.vilnius.tvarkau.extensions.gone
import lt.vilnius.tvarkau.extensions.visible
import lt.vilnius.tvarkau.fragments.interactors.MultipleReportsMapInteractor
import lt.vilnius.tvarkau.fragments.presenters.MultipleReportsMapPresenter
import lt.vilnius.tvarkau.fragments.presenters.MultipleReportsMapPresenterImpl
import lt.vilnius.tvarkau.fragments.views.MultipleProblemsMapView
import javax.inject.Inject

@Screen(titleRes = R.string.title_problems_map,
        trackingScreenName = ActivityConstants.SCREEN_ALL_REPORTS_MAP)
class MultipleProblemsMapFragment : BaseMapFragment(),
        MultipleProblemsMapView,
        GoogleMap.OnInfoWindowClickListener,
        GoogleMap.OnInfoWindowCloseListener {

    @Inject
    internal lateinit var interactor: MultipleReportsMapInteractor

    private val presenter: MultipleReportsMapPresenter by lazy {
        MultipleReportsMapPresenterImpl(
                interactor,
                uiScheduler,
                connectivityProvider,
                this
        )
    }

    private var zoomedToMyLocation = false
    private var toast: Snackbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        savedInstanceState?.let {
            zoomedToMyLocation = it.getBoolean(EXTRA_ZOOMED_TO_MY_LOCATION)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onInject(component: ActivityComponent) {
        component.inject(this)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.report_filter, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_action_filter -> {
                navigationManager.navigateToReportsMapFilter()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun addMarkers(reports: List<Problem>) {
        populateMarkers(reports)
    }

    override fun showError() {
        Toast.makeText(context, R.string.error_no_problems_in_list, Toast.LENGTH_SHORT).show()
    }

    override fun showNetworkError() {
        toast = Snackbar.make(map_container, R.string.no_connection, Snackbar.LENGTH_INDEFINITE)
                .setActionTextColor(ContextCompat.getColor(context!!, R.color.snackbar_action_text))
                .setAction(R.string.try_again) {
                    presenter.onAttach()
                }.apply { show() }

    }

    override fun showProgress() {
        loading_indicator.visible()
        map_container.gone()
    }

    override fun hideProgress() {
        loading_indicator.gone()
        map_container.visible()
    }

    override fun onInfoWindowClick(marker: Marker) {
        navigationManager.navigateToProblemDetailActivity(marker.tag as Problem)
    }

    override fun onInfoWindowClose(marker: Marker) {
        val problem = marker.tag as Problem
        activity!!.setTitle(R.string.title_problems_map)
        marker.setIcon(getMarkerIcon(problem))
    }

    override fun onMapLoaded() {
        super.onMapLoaded()

        googleMap?.setOnInfoWindowClickListener(this)
        googleMap?.setOnInfoWindowCloseListener(this)

        presenter.onAttach()
    }

    override fun onLocationInsideCity(location: Location) {
        if (!zoomedToMyLocation) {
            zoomedToMyLocation = true
            zoomToMyLocation(googleMap!!, location)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(EXTRA_ZOOMED_TO_MY_LOCATION, zoomedToMyLocation)
    }

    override fun onDestroyView() {
        toast?.dismiss()
        presenter.onDetach()
        googleMap?.setOnInfoWindowCloseListener(null)
        googleMap?.setOnInfoWindowClickListener(null)
        super.onDestroyView()
    }

    companion object {
        private const val EXTRA_ZOOMED_TO_MY_LOCATION = "zoomed_to_my_location"

        fun newInstance() = MultipleProblemsMapFragment()
    }
}
