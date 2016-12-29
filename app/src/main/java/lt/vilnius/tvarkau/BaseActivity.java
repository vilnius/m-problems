package lt.vilnius.tvarkau;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.VisibleForTesting;
import android.support.v7.app.AppCompatActivity;

import javax.inject.Inject;
import javax.inject.Named;

import icepick.Icepick;
import lt.vilnius.tvarkau.analytics.Analytics;
import lt.vilnius.tvarkau.backend.LegacyApiService;
import lt.vilnius.tvarkau.dagger.component.ApplicationComponent;

import static lt.vilnius.tvarkau.prefs.Preferences.MY_PROBLEMS_PREFERENCES;

public abstract class BaseActivity extends AppCompatActivity {

    @Inject
    LegacyApiService legacyApiService;
    @Inject
    @Named(MY_PROBLEMS_PREFERENCES)
    SharedPreferences myProblemsPreferences;
    @Inject
    Analytics analytics;

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    protected ApplicationComponent component;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Icepick.restoreInstanceState(this, savedInstanceState);

        component = buildComponent((TvarkauApplication) getApplication());
        onInject(component);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
    }

    protected void onInject(ApplicationComponent component) {
        component.inject(this);
    }

    protected ApplicationComponent buildComponent(TvarkauApplication application) {
        return application.getComponent();
    }
}
