
## 简单的多服务器模式

1. 支持 `client` 模式和 `password` 模式的 oauth

1. 在不同的程序中提供认证服务和资源服务

1. 认证服务器

    1. 用户信息（用户名和密码）存储在程序的内存中
    
    1. client 信息存储在程序的内存中
    
    1. 因为 token 是用 jwt 构造的，所以不需要 token 存储
    
        即使用的 token 存储类的是 `JwtTokenStore`，名称中虽然带有 `store` 字样，但实际并没相应的存储，此特性在文章 [Managing Tokens](https://projects.spring.io/spring-security-oauth/docs/oauth2.html#managing-tokens) 中有说明。

1. 资源服务器

    1. 资源服务器不对用户名和密码进行校验，所以不需要关心用户信息
    
    1. 只记录此资源服务器所支持的 client id，在初始化时已经指定，且不需要关心认证服务器存储的 client 信息
    
        因为 token 是用 jwt 构造的，token 对应的 client 信息在 token 本身就能提取到，所以不需要关心认证服务器存储的 client 信息。
    
    1. 同理，token 本身的值已经包含了所有需要的信息，所以不需要另外存储 token

### 启动程序

1. 启动认证服务器

    即运行 `AuthorizationServerJwtApplication` 的 `main` 函数。

1. 启动资源服务器

    即运行 `ResourceServerJwtApplication` 的 `main` 函数。

### 直接访问

访问 [http://localhost:8081/api/sqr/product/nut](http://localhost:8081/api/sqr/product/nut) 能正常显示。

访问 [http://localhost:8081/api/sqr/order/nut](http://localhost:8081/api/sqr/order/nut) 将显示没有权限。

### 以 `client` 模式访问

1. 获取 access token

    即访问 [http://localhost:8080/oauth/token?grant_type=client_credentials&scope=read&client_id=client1&client_secret=123456](http://localhost:8080/oauth/token?grant_type=client_credentials&scope=read&client_id=client1&client_secret=123456)，将返回类似以下内容：
    
    ```
    {"access_token":"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJhdWQiOlsibXVsdGktc2VydmVyLXdpdGgtand0Il0sInNjb3BlIjpbInJlYWQiXSwiZXhwIjoxNTUyMDEwMjIxLCJhdXRob3JpdGllcyI6WyJVU0VSIl0sImp0aSI6IjJjZTE3Mjg1LWViMzMtNDI2OS04MzkwLTRmOTZkYzczYWNjZiIsImNsaWVudF9pZCI6ImNsaWVudDEifQ.7uT_g4sYs3GNaycS_MOjHlXmBhALwYHqPszqmkx1grE","token_type":"bearer","expires_in":43199,"scope":"read","jti":"2ce17285-eb33-4269-8390-4f96dc73accf"}
    ```

1. 使用此 token 访问 order 接口

    即访问 [http://localhost:8081/api/sqr/order/nut?access_token=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJhdWQiOlsibXVsdGktc2VydmVyLXdpdGgtand0Il0sInNjb3BlIjpbInJlYWQiXSwiZXhwIjoxNTUyMDEwMjIxLCJhdXRob3JpdGllcyI6WyJVU0VSIl0sImp0aSI6IjJjZTE3Mjg1LWViMzMtNDI2OS04MzkwLTRmOTZkYzczYWNjZiIsImNsaWVudF9pZCI6ImNsaWVudDEifQ.7uT_g4sYs3GNaycS_MOjHlXmBhALwYHqPszqmkx1grE](http://localhost:8081/api/sqr/order/nut?access_token=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJhdWQiOlsibXVsdGktc2VydmVyLXdpdGgtand0Il0sInNjb3BlIjpbInJlYWQiXSwiZXhwIjoxNTUyMDEwMjIxLCJhdXRob3JpdGllcyI6WyJVU0VSIl0sImp0aSI6IjJjZTE3Mjg1LWViMzMtNDI2OS04MzkwLTRmOTZkYzczYWNjZiIsImNsaWVudF9pZCI6ImNsaWVudDEifQ.7uT_g4sYs3GNaycS_MOjHlXmBhALwYHqPszqmkx1grE)，将会返回以下内容：
    
    ```
    order id : nut
    ```

### 以 `password` 模式访问

1. 获取 access token

    即访问 [http://localhost:8080/oauth/token?grant_type=password&username=admin&password=123456&scope=read&client_id=client2&client_secret=123456](http://localhost:8080/oauth/token?grant_type=password&username=admin&password=123456&scope=read&client_id=client2&client_secret=123456)，将返回类似以下内容：
    
    ```
    {"access_token":"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJhdWQiOlsibXVsdGktc2VydmVyLXdpdGgtand0Il0sInVzZXJfbmFtZSI6ImFkbWluIiwic2NvcGUiOlsicmVhZCJdLCJleHAiOjE1NTIwMTAyMDEsImF1dGhvcml0aWVzIjpbIlVTRVIiXSwianRpIjoiOTZlNzAzYTktYzY3Zi00ODE5LWE2YmEtMjA0NDQwZDE3YzI3IiwiY2xpZW50X2lkIjoiY2xpZW50MiJ9.jCNYLdURrmTG8xSbBj3E8Rd8BnB2Wte_8-jgSFdt70k","token_type":"bearer","refresh_token":"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJhdWQiOlsibXVsdGktc2VydmVyLXdpdGgtand0Il0sInVzZXJfbmFtZSI6ImFkbWluIiwic2NvcGUiOlsicmVhZCJdLCJhdGkiOiI5NmU3MDNhOS1jNjdmLTQ4MTktYTZiYS0yMDQ0NDBkMTdjMjciLCJleHAiOjE1NTQ1NTkwMDEsImF1dGhvcml0aWVzIjpbIlVTRVIiXSwianRpIjoiMjM4ODExMDUtMWQwNi00YmJmLWI1ZGEtZmJhMjZiMDM3MDdlIiwiY2xpZW50X2lkIjoiY2xpZW50MiJ9.zHe0LY8q5e_7WesOftyCr4_Z_TMCmnBjsxYZYzjtI4Q","expires_in":43199,"scope":"read","jti":"96e703a9-c67f-4819-a6ba-204440d17c27"}
    ```

1. 使用此 token 访问 order 接口

    即访问 [http://localhost:8081/api/sqr/order/nut?access_token=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJhdWQiOlsibXVsdGktc2VydmVyLXdpdGgtand0Il0sInVzZXJfbmFtZSI6ImFkbWluIiwic2NvcGUiOlsicmVhZCJdLCJleHAiOjE1NTIwMTAyMDEsImF1dGhvcml0aWVzIjpbIlVTRVIiXSwianRpIjoiOTZlNzAzYTktYzY3Zi00ODE5LWE2YmEtMjA0NDQwZDE3YzI3IiwiY2xpZW50X2lkIjoiY2xpZW50MiJ9.jCNYLdURrmTG8xSbBj3E8Rd8BnB2Wte_8-jgSFdt70k](http://localhost:8081/api/sqr/order/nut?access_token=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJhdWQiOlsibXVsdGktc2VydmVyLXdpdGgtand0Il0sInVzZXJfbmFtZSI6ImFkbWluIiwic2NvcGUiOlsicmVhZCJdLCJleHAiOjE1NTIwMTAyMDEsImF1dGhvcml0aWVzIjpbIlVTRVIiXSwianRpIjoiOTZlNzAzYTktYzY3Zi00ODE5LWE2YmEtMjA0NDQwZDE3YzI3IiwiY2xpZW50X2lkIjoiY2xpZW50MiJ9.jCNYLdURrmTG8xSbBj3E8Rd8BnB2Wte_8-jgSFdt70k)，将会返回以下内容：
    
    ```
    order id : nut
    ```

