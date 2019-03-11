
## Authorization Code 授权模式

1. 支持 `authorization code` 模式

1. 在不同的程序中提供认证服务和资源服务

1. 认证服务器

    1. 用户信息（用户名和密码）存储在程序的内存中
    
    1. client 信息存储在数据库中
    
    1. resource id 数组值可有可无，可在初始化时指定，默认为空数组
    
        如果 resource id 数组为空数组，资源服务器验证 token 时将忽略 resource id 的检查，否则会检查资源服务器的 resource id 值是否在 token 的 resource id 数组中。
    
    1. token 存储在数据库中
    
        从 `V1__create_tables.sql` 的内容可以看出，相应的数据库是 `oauth_client_details`、`oauth_access_token` 和 `oauth_refresh_token`。
    
        通过在资源服务器验证 token 时调试断点在 `JdbcTokenStore.readAuthentication(String token)` 并查看相关实现可知，access token 的 resource id 的值是存储在 `oauth_access_token` 表的 `authentication` 字段值中。
    
1. 资源服务器

    1. 资源服务器不对用户名和密码进行校验，所以不需要关心用户信息
    
    1. 自身的 client 信息包括 id 和密码存储在内存中
    
    1. 记录此资源服务器所支持的 resource id，在初始化时已经指定
    
        如果资源服务器在初始化时不指定 resource id 值，将默认是 `oauth2-resource`。
    
    1. 验证 token 时是调用认证服务器的接口 `/oauth/check_token` 验证
    
        当然也可以通过直接连数据库进行验证，但此模式的应用场景往往是要限制资源服务器对认证服务器认证信息的访问权限的，所以不建议允许资源服务器直接访问认证服务器的数据库。

### 部署步骤

1. 创建 MySQL 数据库

    部署 MySQL 数据库，并创建一个名为 `oauth2-authorization-code` 的数据库。
    
    具体操作方法有很多种，比如一种比较方便的方法是用 docker-compose 部署，不过这里不再详细说明，以后有需要的话再补充。

1. 启动认证服务器

    即运行 `AuthorizationCodeAuthorizationServerApplication` 的 `main` 函数。
    
    因为用到了 `Flyway` 类库，所以在启动时会自动根据配置的内容初始化数据库。

1. 启动资源服务器

    即运行 `AuthorizationCodeResourceServerApplication` 的 `main` 函数。

### 直接访问

访问 [http://localhost:8081/api/sqr/product/nut](http://localhost:8081/api/sqr/product/nut) 能正常显示。

访问 [http://localhost:8081/api/sqr/order/nut](http://localhost:8081/api/sqr/order/nut) 将显示没有权限。

### 以 `authorization code` 模式访问

1. 获取 access token

    即访问 [http://localhost:8080/oauth/authorize?client_id=client1&redirect_uri=http%3A%2F%2Flocalhost%3A8081%2Fcallback&response_type=code&scope=read&state=abc](http://localhost:8080/oauth/authorize?client_id=client1&redirect_uri=http%3A%2F%2Flocalhost%3A8081%2Fcallback&response_type=code&scope=read&state=abc)，将会被要求先进行身份认证。
    
    输入用户名 `admin` 和密码 `123456` 后将提示用户确认授权，确认后将重定向到 redirect_uri 指定的链接，比如以下链接：
    
    ```
    http://localhost:8081/callback?code=9y5lnD&state=abc
    ```

1. 使用此 code 请求 access token
    
    如果是直接使用命令行，可以执行以下脚本：
    
    ```
    curl -X POST --user client1:123456 http://localhost:8080/oauth/token -H "content-type: application/x-www-form-urlencoded" -d "code=9y5lnD&grant_type=authorization_code&redirect_uri=http%3A%2F%2Flocalhost%3A9000%2Fcallback&scope=read"
    ```
    
    如果是使用 Postman，可以按以下步骤操作：
    
    1. 单击 `+` 按钮创建请求
    
    1. 链接填写 `http://localhost:8080/oauth/token?code=9y5lnD&grant_type=authorization_code&scope=read`
    
    1. `Params` 中增加一行，key 为 `redirect_uri`，value 为 `http://localhost:8081/callback`
    
    1. `Authorization` 中 `TYPE` 选择 `Basic Auth`，然后在 `Username` 中填写 `client1`，在 `Password` 中填写 `123456`
    
    1. 单击 `Send` 按钮发起请求，服务器将返回以下信息
    
        ```
        {"access_token":"8cf9f3e9-1c88-43f4-b5fa-201f5cbf64c8","token_type":"bearer","expires_in":41278,"scope":"read"}
        ```

1. 使用此 token 访问 order 接口

    即访问 [http://localhost:8081/api/sqr/order/nut?access_token=8cf9f3e9-1c88-43f4-b5fa-201f5cbf64c8](http://localhost:8081/api/sqr/order/nut?access_token=8cf9f3e9-1c88-43f4-b5fa-201f5cbf64c8)，将会返回以下内容：
    
    ```
    order id : nut
    ```

