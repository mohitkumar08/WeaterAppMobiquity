package com.example.weatherapp.core


import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.example.weatherapp.BuildConfig
import com.example.weatherapp.WeatherApplication
import com.example.weatherapp.repository.local.AppDatabase
import com.example.weatherapp.repository.service.WeatherNetworkService
import com.google.gson.*
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.reflect.Modifier
import java.lang.reflect.Type
import java.text.DateFormat
import java.util.*
import java.util.concurrent.TimeUnit


private const val READ_TIMEOUT = 10L
private const val WRITE_TIMEOUT = 10L
private const val CONNECTION_TIMEOUT = 10L
private const val CALL_TIMEOUT = 10L

class AppObjectController {

    companion object {

        @JvmStatic
        var INSTANCE: AppObjectController =
            AppObjectController()

        @JvmStatic
        lateinit var joshApplication: WeatherApplication
            private set

        @JvmStatic
        lateinit var appDatabase: AppDatabase
            private set

        @JvmStatic
        lateinit var retrofit: Retrofit
            private set

        @JvmStatic
        lateinit var weatherNetworkService: WeatherNetworkService
            private set

        @JvmStatic
        var uiHandler: Handler = Handler(Looper.getMainLooper())
            private set

        fun initLibrary(context: Context): AppObjectController {
            joshApplication = context as WeatherApplication
            appDatabase = AppDatabase.getDatabase(context)!!


            val builder = OkHttpClient().newBuilder()
                .connectTimeout(CONNECTION_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
                .callTimeout(CALL_TIMEOUT, TimeUnit.SECONDS)
                .addInterceptor(ApiKeyInterceptor())


            if (BuildConfig.DEBUG) {
                val logging =
                    HttpLoggingInterceptor { message -> Log.e("OkHttp", message) }.apply {
                        level = HttpLoggingInterceptor.Level.BODY

                    }
                builder.addInterceptor(logging)
            }
            val gsonMapper = GsonBuilder()
                .enableComplexMapKeySerialization()
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                .excludeFieldsWithModifiers(
                    Modifier.TRANSIENT
                )
                .setPrettyPrinting()
                .create()

            retrofit = Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_URL)
                .client(builder.build())
                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .addConverterFactory(GsonConverterFactory.create(gsonMapper))
                .build()
            weatherNetworkService = retrofit.create(WeatherNetworkService::class.java)
            return INSTANCE
        }
    }

    class ApiKeyInterceptor : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            var request: Request = chain.request()
            val url: HttpUrl = request.url.newBuilder()
                .addQueryParameter("appid", BuildConfig.WEATHER_API_KEY).build()
            request = request.newBuilder().url(url).build()
            return chain.proceed(request)
        }
    }


}
