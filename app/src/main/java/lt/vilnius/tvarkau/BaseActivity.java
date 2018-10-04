package lt.vilnius.tvarkau;

import android.arch.lifecycle.ViewModelProvider;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.VisibleForTesting;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import lt.vilnius.tvarkau.analytics.Analytics;
import lt.vilnius.tvarkau.backend.LegacyApiService;
import lt.vilnius.tvarkau.dagger.component.ActivityComponent;
import lt.vilnius.tvarkau.navigation.NavigationManager;

import javax.inject.Inject;
import javax.inject.Named;

import static lt.vilnius.tvarkau.prefs.Preferences.MY_PROBLEMS_PREFERENCES;

public abstract class BaseActivity extends AppCompatActivity {

    @Inject
    LegacyApiService legacyApiService;
    @Inject
    @Named(MY_PROBLEMS_PREFERENCES)
    SharedPreferences myProblemsPreferences;
    @Inject
    Analytics analytics;
    @Inject
    NavigationManager navigationManager;
    @Inject
    protected ViewModelProvider.Factory viewModelFactory;


    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    protected ActivityComponent component;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        component = buildComponent((TvarkauApplication) getApplication());
        onInject(component);
    }

    @Override
    protected void onDestroy() {
        analytics.flush();
        super.onDestroy();
    }

    protected void onInject(ActivityComponent component) {
        component.inject(this);
    }

    protected ActivityComponent buildComponent(TvarkauApplication application) {
        return ActivityComponent.Companion.init(application.getComponent(), this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (navigationManager.onBackPressed()) {
            return;
        }

        super.onBackPressed();
    }
}
