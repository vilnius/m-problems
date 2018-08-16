package lt.vilnius.tvarkau.dagger.module

import android.app.Application
import ca.mimic.oauth2library.OAuth2Client
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import io.reactivex.Scheduler
import lt.vilnius.tvarkau.R
import lt.vilnius.tvarkau.api.ApiEndpoint
import lt.vilnius.tvarkau.dagger.Api
import lt.vilnius.tvarkau.dagger.IoScheduler
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
class RestAdapterModule {

    @Provides
    @Named(HOST)
    fun provideHost(): String {
        return "https://api.tvarkaumiesta.lt"
    }

    @Provides
    @ApiOAuth
    fun provideOAuthEndpoint(
            application: Application,
            @Named(HOST) host: String
    ): ApiEndpoint {
        return ApiEndpoint(application.getString(R.string.api_root_oauth).format(host))
    }

    @Provides
    @Api
    fun provideApiV2Endpoint(
            @Named(HOST) host: String
    ): ApiEndpoint {
        return ApiEndpoint(host.plus("/"))
    }

    @Provides
    @GuestToken
    fun provideGuestTokenOAuthBuilder(
            @ApiOAuth endpoint: ApiEndpoint,
            @RawOkHttpClient client: OkHttpClient
    ): OAuth2Client.Builder {
        return OAuth2Client.Builder(
                OAUTH_CLIENT_ID,
                "",
                endpoint.url + OAUTH_TOKEN_ENDPOINT
        )
                .grantType("client_credentials")
                .scope("user")
                .okHttpClient(client)
    }

    @Provides
    @RefreshToken
    fun provideRefreshTokenOAuthBuilder(
            @ApiOAuth endpoint: ApiEndpoint,
            @RawOkHttpClient client: OkHttpClient
    ): OAuth2Client.Builder {
        return OAuth2Client.Builder(
                OAUTH_CLIENT_ID,
                "",
                endpoint.url + OAUTH_TOKEN_ENDPOINT
        )
                .grantType("refresh_token")
                .okHttpClient(client)
    }

    @Provides
    @Api
    @Singleton
    fun provideApi2Retrofit(
            @Api endpoint: ApiEndpoint,
            @IoScheduler ioScheduler: Scheduler,
            @Api client: OkHttpClient,
            gson: Gson
    ): Retrofit {
        return Retrofit.Builder()
                .client(client)
                .baseUrl(endpoint.url)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(ioScheduler))
                .build()
    }

    companion object {
        const val HOST = "host"
        const val OAUTH_CLIENT_ID = "android"
        const val OAUTH_TOKEN_ENDPOINT = "token"
    }
}

@Qualifier
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
annotation class ApiOAuth

@Qualifier
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
annotation class GuestToken

@Qualifier
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
annotation class RefreshToken
