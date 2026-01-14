package com.example.infra.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Retrofit 单例类
 * 负责创建和管理 Retrofit 实例
 */
object RetrofitClient {

    private const val DEFAULT_CONNECT_TIMEOUT = 30L
    private const val DEFAULT_READ_TIMEOUT = 30L
    private const val DEFAULT_WRITE_TIMEOUT = 30L

    /**
     * OkHttp 客户端配置
     */
    private val okHttpClient: OkHttpClient by lazy {
        val builder = OkHttpClient.Builder()

        // 设置超时时间
        builder.connectTimeout(DEFAULT_CONNECT_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(DEFAULT_READ_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(DEFAULT_WRITE_TIMEOUT, TimeUnit.SECONDS)

        // 添加日志拦截器（Debug 模式下打印请求日志）
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        builder.addInterceptor(loggingInterceptor)

        builder.build()
    }

    /**
     * Retrofit 实例缓存
     * Key: baseUrl, Value: Retrofit 实例
     */
    private val retrofitCache = mutableMapOf<String, Retrofit>()

    /**
     * 获取 Retrofit 实例
     * @param baseUrl 基础 URL（如：https://api.example.com/）
     * @return Retrofit 实例
     */
    fun getRetrofit(baseUrl: String): Retrofit {
        return retrofitCache.getOrPut(baseUrl) {
            Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
    }

    /**
     * 创建 API 服务实例
     * @param baseUrl 基础 URL
     * @param service API 服务接口的 Class 对象
     * @return API 服务实例
     */
    fun <T> createApi(baseUrl: String, service: Class<T>): T {
        return getRetrofit(baseUrl).create(service)
    }

    /**
     * 创建 API 服务实例（使用 Kotlin 泛型）
     * @param baseUrl 基础 URL
     * @return API 服务实例
     */
    inline fun <reified T> createApi(baseUrl: String): T {
        return createApi(baseUrl, T::class.java)
    }

    /**
     * 清除 Retrofit 缓存
     */
    fun clearCache() {
        retrofitCache.clear()
    }
}
