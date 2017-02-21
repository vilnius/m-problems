package lt.vilnius.tvarkau;

import android.app.Application;

import com.squareup.leakcanary.RefWatcher;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import lt.vilnius.tvarkau.dagger.module.IoScheduler;
import lt.vilnius.tvarkau.dagger.module.UiScheduler;
import rx.Scheduler;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

@Module
public class AppModule {

    TvarkauApplication application;

    public AppModule(TvarkauApplication application) {
        this.application = application;
    }

    @Provides
    @Singleton
    Application providesApplication() {
        return application;
    }

    @Provides
    @Singleton
    @IoScheduler
    Scheduler provideIoScheduler() {
        return Schedulers.io();
    }

    @Provides
    @Singleton
    @UiScheduler
    Scheduler provideUiScheduler() {
        return AndroidSchedulers.mainThread();
    }

    @Provides
    @Singleton
    RefWatcher providesRefWatcher() {
        return application.getRefWatcher();
    }
}