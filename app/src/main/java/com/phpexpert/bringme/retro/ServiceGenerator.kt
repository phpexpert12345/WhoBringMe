package com.phpexpert.bringme.retro

import android.annotation.SuppressLint
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.phpexpert.bringme.utilities.CONSTANTS
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

class ServiceGenerator {

    companion object {
        private val builder: Retrofit.Builder =
            Retrofit.Builder().baseUrl(CONSTANTS.URLS.baseUrl)
        private var gson: Gson = GsonBuilder()
            .setLenient()
            .create()

        fun <S> createService(serviceClass: Class<S>?): S {
            //        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
//        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
//        httpClient.interceptors().add(interceptor);
//        httpClient.setConnectTimeout(10, TimeUnit.MINUTES);
//        httpClient.setReadTimeout(10, TimeUnit.MINUTES);
//        httpClient.setWriteTimeout(10, TimeUnit.MINUTES);
            val retrofit = builder
                .client(unsafeOkHttpClient.build())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
            return retrofit.create(serviceClass)
        }

        private val unsafeOkHttpClient:
                OkHttpClient.Builder
            get() = try { // Create a trust manager that does not validate certificate chains
                val trustAllCerts =
                    arrayOf<TrustManager>(
                        object : X509TrustManager {
                            @SuppressLint("TrustAllX509TrustManager")
                            @Throws(CertificateException::class)
                            override fun checkClientTrusted(
                                chain: Array<X509Certificate>,
                                authType: String
                            ) {
                            }

                            @SuppressLint("TrustAllX509TrustManager")
                            @Throws(CertificateException::class)
                            override fun checkServerTrusted(
                                chain: Array<X509Certificate>,
                                authType: String
                            ) {
                            }

                            override fun getAcceptedIssuers(): Array<X509Certificate> {
                                return arrayOf()
                            }
                        }
                    )
                // Install the all-trusting trust manager
                val sslContext =
                    SSLContext.getInstance("SSL")
                sslContext.init(null, trustAllCerts, SecureRandom())
                // Create an ssl socket factory with our all-trusting manager
                val sslSocketFactory = sslContext.socketFactory
                val builder = OkHttpClient.Builder()
                builder.connectTimeout(30, TimeUnit.MINUTES)
                builder.readTimeout(30, TimeUnit.MINUTES)
                builder.writeTimeout(30, TimeUnit.MINUTES)
                builder.addInterceptor { chain ->
                    val original = chain.request()
                    val requestBuilder = original.newBuilder()
                        .addHeader("Accept", "application/json")
                        .addHeader("Content-Type", "application/json")
                    val request = requestBuilder.build()
                    chain.proceed(request)
                }
                val interceptor = HttpLoggingInterceptor()
                interceptor.level = HttpLoggingInterceptor.Level.BODY
                builder.addInterceptor(interceptor)
                builder.sslSocketFactory(
                    sslSocketFactory,
                    trustAllCerts[0] as X509TrustManager
                )
                builder.hostnameVerifier { _, _ -> true }
                builder
            } catch (e: Exception) {
                throw RuntimeException(e)
            }
    }
}