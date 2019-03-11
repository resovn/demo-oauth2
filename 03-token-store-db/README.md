
## 开始使用数据库：用于存储 Token

1. 支持 `client` 模式和 `password` 模式的 oauth

1. 在不同的程序中提供认证服务和资源服务

1. 认证服务器

    1. 用户信息（用户名和密码）存储在程序的内存中
    
    1. client 信息存储在程序的内存中
    
    1. resource id 数组值可有可无，可在初始化时指定，默认为空数组
    
        如果 resource id 数组为空数组，资源服务器验证 token 时将忽略 resource id 的检查，否则会检查资源服务器的 resource id 值是否在 token 的 resource id 数组中。
    
    1. token 存储在数据库中
    
        从 `V1__create_tables.sql` 的内容可以看出，相应的数据库是 `oauth_access_token` 和 `oauth_refresh_token`。
    
        通过在资源服务器验证 token 时调试断点在 `JdbcTokenStore.readAuthentication(String token)` 并查看相关实现可知，access token 的 resource id 的值是存储在 `oauth_access_token` 表的 `authentication` 字段值中。
    
1. 资源服务器

    1. 资源服务器不对用户名和密码进行校验，所以不需要关心用户信息
    
    1. 不关心 client 相应信息，所以不需要关心认证服务器存储的 client 信息
    
    1. 记录此资源服务器所支持的 resource id，在初始化时已经指定
    
        如果资源服务器在初始化时不指定 resource id 值，将默认是 `oauth2-resource`。
    
    1. 验证 token 时是依据数据库的数据进行，所读取的相应表和认证服务器相同

### 部署步骤

1. 创建 MySQL 数据库

    部署 MySQL 数据库，并创建一个名为 `oauth2-token-store` 的数据库。
    
    具体操作方法有很多种，比如一种比较方便的方法是用 docker-compose 部署，不过这里不再详细说明，以后有需要的话再补充。

1. 启动认证服务器

    即运行 `AuthorizationServerTokenStoreDbApplication` 的 `main` 函数。
    
    因为用到了 `Flyway` 类库，所以在启动时会自动根据配置的内容初始化数据库。

1. 启动资源服务器

    即运行 `ResourceServerTokenStoreDbApplication` 的 `main` 函数。

### 直接访问

访问 [http://localhost:8081/api/sqr/product/nut](http://localhost:8081/api/sqr/product/nut) 能正常显示。

访问 [http://localhost:8081/api/sqr/order/nut](http://localhost:8081/api/sqr/order/nut) 将显示没有权限。

### 以 `client` 模式访问

1. 获取 access token

    即访问 [http://localhost:8080/oauth/token?grant_type=client_credentials&scope=read&client_id=client1&client_secret=123456](http://localhost:8080/oauth/token?grant_type=client_credentials&scope=read&client_id=client1&client_secret=123456)，将返回类似以下内容：
    
    ```
    {"access_token":"143ebb87-223a-4a40-8921-6679a1b1a9f9","token_type":"bearer","expires_in":42707,"scope":"read"}
    ```

1. 使用此 token 访问 order 接口

    即访问 [http://localhost:8081/api/sqr/order/nut?access_token=143ebb87-223a-4a40-8921-6679a1b1a9f9](http://localhost:8081/api/sqr/order/nut?access_token=143ebb87-223a-4a40-8921-6679a1b1a9f9)，将会返回以下内容：
    
    ```
    order id : nut
    ```

### 以 `password` 模式访问

1. 获取 access token

    即访问 [http://localhost:8080/oauth/token?grant_type=password&username=admin&password=123456&scope=read&client_id=client2&client_secret=123456](http://localhost:8080/oauth/token?grant_type=password&username=admin&password=123456&scope=read&client_id=client2&client_secret=123456)，将返回类似以下内容：
    
    ```
    {"access_token":"43100a3c-1ade-4298-b700-8a3f814931f3","token_type":"bearer","refresh_token":"8302fdb1-eaed-4905-b006-87da309c6d2e","expires_in":43198,"scope":"read"}
    ```

1. 使用此 token 访问 order 接口

    即访问 [http://localhost:8081/api/sqr/order/nut?access_token=43100a3c-1ade-4298-b700-8a3f814931f3](http://localhost:8081/api/sqr/order/nut?access_token=43100a3c-1ade-4298-b700-8a3f814931f3)，将会返回以下内容：
    
    ```
    order id : nut
    ```

