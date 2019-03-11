
## 简单的混合模式

1. 支持 `client` 模式和 `password` 模式的 oauth

1. 在同一个程序中提供认证服务和资源服务

1. 认证服务器

    1. 用户信息（用户名和密码）存储在程序的内存中
    
    1. client 信息存储在程序的内存中
    
    1. token 存储在程序的内存中

1. 资源服务器

    1. 资源服务器不对用户名和密码进行校验，所以不需要关心用户信息
    
    1. 资源服务器验证 token 时，跟 client 相关的验证，是比较资源服务器自己支持的 client id 和 token 中存储的 token 的 client id 是否一致，所以不需要读取认证服务器存储的 client 信息
    
        此结论可通过查看和调试 `OAuth2AuthenticationManager` 类的 `authenticate(Authentication authentication)` 函数实现得到验证。
    
    1. 没定义 token 存储，所以默认直接使用在同一个程序中的认证服务器中在内存中的 token 存储
    
        当资源服务器和认证服务器部署在同一程序中时，资源服务器会优先使用认证服务器的 token 存储。
        
        此结论已通过调试得到证实：此时资源服务器和认证服务器使用的是同一个 token 存储实例。
        
        调试验证过程：
        
        1. 查看认证服务器的 token 存储实例
        
            在访问 `/oauth/token` 接口时，断点在 `TokenEndpoint` 类的 `postAccessToken(Principal principal, @RequestParam Map<String, String> parameters)`，查看 `this.tokenGranter.this$0.tokenStore` 对应的实例 ID
        
        1. 查看资源服务器的 token 存储实例
        
            在访问 `/api/sqr/order/nut` 接口时，断点在 `DefaultTokenServices` 类的 `loadAuthentication(String accessTokenValue)`，查看 `this.tokenStore` 对应的实例 ID
        
            会发现此 ID 值和认证服务器中看到的 ID 值相同，说明资源服务器和认证服务器使用的是同一个 token 存储实例。
        
        如果要从源码中找答案，可以从类 `ResourceServerConfiguration` 中的以下代码中发现一些端倪。
         
        ```
            ... ...
        	@Autowired(required = false)
        	private AuthorizationServerEndpointsConfiguration endpoints;
            ... ...
        	protected void configure(HttpSecurity http) throws Exception {
        		ResourceServerSecurityConfigurer resources = new ResourceServerSecurityConfigurer();
        		ResourceServerTokenServices services = resolveTokenServices();
        		if (services != null) {
        			resources.tokenServices(services);
        		}
        		else {
        			if (tokenStore != null) {
        				resources.tokenStore(tokenStore);
        			}
        			else if (endpoints != null) {
        				resources.tokenStore(endpoints.getEndpointsConfigurer().getTokenStore());
        			}
        		}
        ```
        
        中的
        
        ```
            ... ...
        	@Autowired(required = false)
        	private AuthorizationServerEndpointsConfiguration endpoints;
        ```
        
        和
        
        ```
            ... ...
        			else if (endpoints != null) {
        				resources.tokenStore(endpoints.getEndpointsConfigurer().getTokenStore());
        			}
        ```

### 启动程序

直接运行 `SimpleHybridApplication` 的 `main` 函数即可。

### 直接访问

访问 [http://localhost:8080/api/sqr/product/nut](http://localhost:8080/api/sqr/product/nut) 能正常显示。

访问 [http://localhost:8080/api/sqr/order/nut](http://localhost:8080/api/sqr/order/nut) 将显示没有权限。

### 以 `client` 模式访问

1. 获取 access token

    即访问 [http://localhost:8080/oauth/token?grant_type=client_credentials&scope=read&client_id=client1&client_secret=123456](http://localhost:8080/oauth/token?grant_type=client_credentials&scope=read&client_id=client1&client_secret=123456)，将返回类似以下内容：
    
    ```
    {"access_token":"9002bd98-3c2e-4a3d-b76a-32cb7556d502","token_type":"bearer","expires_in":43197,"scope":"read"}
    ```

1. 使用此 token 访问 order 接口

    即访问 [http://localhost:8080/api/sqr/order/nut?access_token=9002bd98-3c2e-4a3d-b76a-32cb7556d502](http://localhost:8080/api/sqr/order/nut?access_token=9002bd98-3c2e-4a3d-b76a-32cb7556d502)，将会返回以下内容：
    
    ```
    order id : nut
    ```

### 以 `password` 模式访问

1. 获取 access token

    即访问 [http://localhost:8080/oauth/token?grant_type=password&username=admin&password=123456&scope=read&client_id=client2&client_secret=123456](http://localhost:8080/oauth/token?grant_type=password&username=admin&password=123456&scope=read&client_id=client2&client_secret=123456)，将返回类似以下内容：
    
    ```
    {"access_token":"20da014f-029e-473c-8380-f4b26f161d8e","token_type":"bearer","refresh_token":"15256d25-ce84-4a58-897c-c6d09c9bf377","expires_in":43199,"scope":"read"}
    ```

1. 使用此 token 访问 order 接口

    即访问 [http://localhost:8080/api/sqr/order/nut?access_token=20da014f-029e-473c-8380-f4b26f161d8e](http://localhost:8080/api/sqr/order/nut?access_token=20da014f-029e-473c-8380-f4b26f161d8e)，将会返回以下内容：
    
    ```
    order id : nut
    ```

