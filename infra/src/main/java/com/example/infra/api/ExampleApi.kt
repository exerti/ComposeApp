package com.example.infra.api

import com.example.infra.network.ApiResult
import com.example.infra.network.RetrofitClient
import com.example.infra.network.apiCall
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * 示例 API 接口
 * 参考此接口定义你自己的 API
 */
interface ExampleApi {

    @GET("users/{id}")
    suspend fun getUser(@Path("id") userId: String): Response<UserResponse>

    @GET("users")
    suspend fun getUsers(@Query("page") page: Int = 1): Response<List<UserResponse>>

    @POST("users")
    suspend fun createUser(@Body user: UserRequest): Response<UserResponse>
}

/**
 * 示例 API 服务类
 * 提供返回 ApiResult 的便捷方法
 */
class ExampleApiService(private val baseUrl: String) {

    private val api: ExampleApi by lazy {
        RetrofitClient.createApi<ExampleApi>(baseUrl)
    }

    /**
     * 获取用户信息
     * @param userId 用户ID
     * @return ApiResult<UserResponse>
     */
    suspend fun getUser(userId: String): ApiResult<UserResponse> {
        return apiCall { api.getUser(userId) }
    }

    /**
     * 获取用户列表
     * @param page 页码
     * @return ApiResult<List<UserResponse>>
     */
    suspend fun getUsers(page: Int = 1): ApiResult<List<UserResponse>> {
        return apiCall { api.getUsers(page) }
    }

    /**
     * 创建用户
     * @param user 用户信息
     * @return ApiResult<UserResponse>
     */
    suspend fun createUser(user: UserRequest): ApiResult<UserResponse> {
        return apiCall { api.createUser(user) }
    }
}

// ============ 示例数据模型 ============

/**
 * 用户响应数据
 */
data class UserResponse(
    val id: String,
    val name: String,
    val email: String,
    val createdAt: String
)

/**
 * 创建用户请求数据
 */
data class UserRequest(
    val name: String,
    val email: String
)
