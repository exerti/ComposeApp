package com.example.infra.network

/**
 * 网络请求结果封装类
 * 用于统一处理网络请求的成功、失败状态
 */
sealed class ApiResult<out T> {
    /**
     * 请求成功
     * @param data 返回的数据
     */
    data class Success<T>(val data: T) : ApiResult<T>()

    /**
     * 请求失败
     * @param code 错误码
     * @param message 错误信息
     */
    data class Error(val code: Int, val message: String) : ApiResult<Nothing>()

    /**
     * 请求异常（网络错误、解析错误等）
     * @param exception 异常对象
     */
    data class Exception(val exception: kotlin.Exception) : ApiResult<Nothing>()

    /**
     * 是否成功
     */
    val isSuccess: Boolean
        get() = this is Success

    /**
     * 是否失败
     */
    val isError: Boolean
        get() = this is Error || this is Exception

    /**
     * 获取数据，如果失败则返回 null
     */
    fun getOrNull(): T? = when (this) {
        is Success -> data
        else -> null
    }

    /**
     * 获取数据，如果失败则返回默认值
     */
    fun getOrDefault(defaultValue: @UnsafeVariance T): T = when (this) {
        is Success -> data
        else -> defaultValue
    }

    /**
     * 成功时执行操作
     */
    inline fun onSuccess(action: (T) -> Unit): ApiResult<T> {
        if (this is Success) {
            action(data)
        }
        return this
    }

    /**
     * 失败时执行操作
     */
    inline fun onError(action: (Throwable) -> Unit): ApiResult<T> {
        if (this is Error) {
            action(RuntimeException("Code: $code, Message: $message"))
        } else if (this is Exception) {
            action(exception)
        }
        return this
    }
}
