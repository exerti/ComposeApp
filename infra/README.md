# Infra 网络模块使用文档

本模块封装了 Retrofit 网络请求库，提供统一的结果处理和便捷的 API 调用方式。

## 模块结构

```
infra/src/main/java/com/example/infra/
├── api/
│   └── ExampleApi.kt          # 示例 API 接口定义
├── network/
│   ├── ApiResult.kt            # 网络请求结果封装类
│   ├── RetrofitClient.kt       # Retrofit 单例管理
│   └── NetworkExtensions.kt    # 网络请求扩展函数
```

---

## 快速开始

### 1. 在 app 模块中已经配置好了依赖

```kotlin
// app/build.gradle.kts
dependencies {
    implementation(project(":infra"))
}
```

### 2. 定义你的 API 接口

```kotlin
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface MyApi {
    @GET("users/{id}")
    suspend fun getUser(@Path("id") userId: String): Response<UserResponse>
}
```

### 3. 创建 API 服务类

```kotlin
class MyApiService(private val baseUrl: String) {

    private val api: MyApi by lazy {
        RetrofitClient.createApi<MyApi>(baseUrl)
    }

    suspend fun getUser(userId: String): ApiResult<UserResponse> {
        return apiCall { api.getUser(userId) }
    }
}
```

### 4. 在 ViewModel 或其他地方调用

```kotlin
class MyViewModel : ViewModel() {

    private val apiService = MyApiService("https://api.example.com/")

    fun loadUser(userId: String) {
        viewModelScope.launch {
            val result = apiService.getUser(userId)

            result.onSuccess { user ->
                // 请求成功，处理数据
                println("用户名: ${user.name}")
            }.onError { error ->
                // 请求失败，处理错误
                println("请求失败: ${error.message}")
            }
        }
    }
}
```

---

## 详细使用说明

### ApiResult 结果封装

`ApiResult` 是一个密封类，有三种状态：

```kotlin
sealed class ApiResult<out T> {
    data class Success<T>(val data: T) : ApiResult<T>()    // 成功
    data class Error(val code: Int, val message: String)   // HTTP 错误
    data class Exception(val exception: Exception)         // 异常
}
```

#### 常用方法

```kotlin
val result: ApiResult<User> = apiService.getUser("123")

// 判断是否成功
if (result.isSuccess) { ... }

// 获取数据（失败返回 null）
val user = result.getOrNull()

// 获取数据（失败返回默认值）
val user = result.getOrDefault(User())

// 链式调用
result
    .onSuccess { user -> println(user.name) }
    .onError { error -> println(error.message) }

// 使用 when 表达式处理
when (result) {
    is ApiResult.Success -> println("成功: ${result.data}")
    is ApiResult.Error -> println("错误: ${result.message}")
    is ApiResult.Exception -> println("异常: ${result.exception.message}")
}
```

---

### RetrofitClient 单例

```kotlin
// 方式一：获取 Retrofit 实例
val retrofit = RetrofitClient.getRetrofit("https://api.example.com/")
val api = retrofit.create(MyApi::class.java)

// 方式二：直接创建 API 实例（推荐）
val api = RetrofitClient.createApi<MyApi>("https://api.example.com/")

// 清除缓存
RetrofitClient.clearCache()
```

---

### apiCall 扩展函数

自动将 Retrofit Response 转换为 ApiResult：

```kotlin
suspend fun getUser(userId: String): ApiResult<UserResponse> {
    return apiCall { api.getUser(userId) }
}
```

---

## 完整示例

### API 接口定义

```kotlin
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface UserApi {
    @GET("users/{id}")
    suspend fun getUser(@Path("id") userId: String): Response<UserResponse>

    @GET("users")
    suspend fun getUsers(@Query("page") page: Int): Response<List<UserResponse>>

    @POST("users")
    suspend fun createUser(@Body user: UserRequest): Response<UserResponse>
}
```

### 数据模型

```kotlin
data class UserResponse(
    val id: String,
    val name: String,
    val email: String
)

data class UserRequest(
    val name: String,
    val email: String
)
```

### API 服务类

```kotlin
import com.example.infra.network.ApiResult
import com.example.infra.network.RetrofitClient
import com.example.infra.network.apiCall

class UserApiService(private val baseUrl: String) {

    private val api: UserApi by lazy {
        RetrofitClient.createApi<UserApi>(baseUrl)
    }

    suspend fun getUser(userId: String): ApiResult<UserResponse> {
        return apiCall { api.getUser(userId) }
    }

    suspend fun getUsers(page: Int = 1): ApiResult<List<UserResponse>> {
        return apiCall { api.getUsers(page) }
    }

    suspend fun createUser(user: UserRequest): ApiResult<UserResponse> {
        return apiCall { api.createUser(user) }
    }
}
```

### 在 Compose 中使用

```kotlin
@Composable
fun UserScreen(userId: String) {
    var userState by remember { mutableStateOf<UserResponse?>(null) }
    var errorState by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(userId) {
        val service = UserApiService("https://api.example.com/")
        val result = service.getUser(userId)

        result.onSuccess { user ->
            userState = user
        }.onError { error ->
            errorState = error.message
        }
    }

    if (userState != null) {
        Text("用户名: ${userState!!.name}")
    } else if (errorState != null) {
        Text("错误: $errorState")
    } else {
        CircularProgressIndicator()
    }
}
```

---

## 常用 Retrofit 注解

| 注解 | 说明 | 示例 |
|------|------|------|
| `@GET` | GET 请求 | `@GET("users")` |
| `@POST` | POST 请求 | `@POST("users")` |
| `@PUT` | PUT 请求 | `@PUT("users/{id}")` |
| `@DELETE` | DELETE 请求 | `@DELETE("users/{id}")` |
| `@Path` | 路径参数 | `@Path("id") id: String` |
| `@Query` | 查询参数 | `@Query("page") page: Int` |
| `@Body` | 请求体 | `@Body user: User` |
| `@Header` | 请求头 | `@Header("Authorization") token: String` |
| `@Field` | 表单字段 | `@Field("name") name: String` |

---

## 配置说明

### 修改超时时间

在 `RetrofitClient.kt` 中修改：

```kotlin
private const val DEFAULT_CONNECT_TIMEOUT = 30L  // 连接超时（秒）
private const val DEFAULT_READ_TIMEOUT = 30L     // 读取超时（秒）
private const val DEFAULT_WRITE_TIMEOUT = 30L    // 写入超时（秒）
```

### 添加通用请求头

```kotlin
val builder = OkHttpClient.Builder()

builder.addInterceptor { chain ->
    val original = chain.request()
    val request = original.newBuilder()
        .header("Authorization", "Bearer $token")
        .header("Accept", "application/json")
        .build()
    chain.proceed(request)
}
```

### 关闭日志（生产环境）

```kotlin
val loggingInterceptor = HttpLoggingInterceptor().apply {
    level = if (BuildConfig.DEBUG) {
        HttpLoggingInterceptor.Level.BODY
    } else {
        HttpLoggingInterceptor.Level.NONE
    }
}
```
