package com.example.infra.network

import retrofit2.Response

/**
 * 将 Retrofit Response 转换为 ApiResult
 * @return ApiResult<T>
 */
suspend fun <T> apiCall(call: suspend () -> Response<T>): ApiResult<T> {
    return try {
        val response = call()
        if (response.isSuccessful) {
            val body = response.body()
            if (body != null) {
                ApiResult.Success(body)
            } else {
                ApiResult.Error(response.code(), "Response body is null")
            }
        } else {
            ApiResult.Error(response.code(), response.message() ?: "Unknown error")
        }
    } catch (e: Exception) {
        ApiResult.Exception(e)
    }
}

/**
 * 执行网络请求并返回 ApiResult
 * 自动处理 try-catch，将异常转换为 ApiResult.Exception
 * @return ApiResult<T>
 */
suspend fun <T> safeApiCall(call: suspend () -> T): ApiResult<T> {
    return try {
        val result = call()
        ApiResult.Success(result)
    } catch (e: Exception) {
        ApiResult.Exception(e)
    }
}
