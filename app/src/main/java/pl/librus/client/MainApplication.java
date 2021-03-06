package pl.librus.client;

import android.content.Context;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.multidex.MultiDexApplication;

import com.google.common.base.Optional;

import javax.inject.Inject;

import io.reactivex.exceptions.UndeliverableException;
import io.reactivex.plugins.RxJavaPlugins;
import pl.librus.client.analytics.IAnalytics;
import pl.librus.client.data.server.HttpException;
import pl.librus.client.ui.MainActivity;
import pl.librus.client.util.LibrusUtils;


public class MainApplication extends MultiDexApplication {

    protected static ApplicationComponent applicationComponent;
    private static UserComponent userComponent;
    private static MainActivityComponent mainActivityComponent;

    @Inject
    Optional<IAnalytics> analytics;

    @Override
    public void onCreate() {
        super.onCreate();

        applicationComponent = buildApplicationComponent();
        applicationComponent.inject(this);

        StrictMode.enableDefaults();
        if (analytics.isPresent()) {
            analytics.get().init();
        }

        RxJavaPlugins.setErrorHandler(throwable -> {
            if (throwable instanceof UndeliverableException && throwable.getCause() instanceof HttpException) {
                //If there are many requests sent at once, first error is handler normally, the rest lands here
                LibrusUtils.log("plugin handle");
                LibrusUtils.log(throwable);
            } else {
                Thread currentThread = Thread.currentThread();
                Thread.UncaughtExceptionHandler handler = currentThread.getUncaughtExceptionHandler();
                handler.uncaughtException(currentThread, throwable);
            }
        });
    }

    protected ApplicationComponent buildApplicationComponent() {
        return DaggerApplicationComponent
                .builder()
                .applicationModule(new ApplicationModule(this))
                .build();
    }

    public static Optional<UserComponent> getOrCreateUserComponent(Context context) {
        if (userComponent == null) {
            String login = PreferenceManager.getDefaultSharedPreferences(context)
                    .getString("login", null);
            if (login == null) {
                return Optional.absent();
            }
            userComponent = component().plus(new UserModule(login));
        }

        return Optional.of(userComponent);
    }

    public static MainActivityComponent createMainActivityComponent(MainActivity mainActivity) {
        mainActivityComponent = getOrCreateUserComponent(mainActivity)
                .get() // Assuming user is logged in
                .plus(new MainActivityModule(mainActivity));
        return mainActivityComponent;
    }

    public static Optional<MainActivityComponent> getMainActivityComponent() {
        return Optional.fromNullable(mainActivityComponent);
    }

    public static void releaseMainActivityComponent() {
        mainActivityComponent = null;
    }

    public static void releaseUserComponent() {
        userComponent = null;
    }

    public static ApplicationComponent component() {
        return applicationComponent;
    }

}
