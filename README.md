# spring-security-demo
参考：  
[spring security学习（SpringBoot2.1.5版本）](https://gitee.com/blueses/spring-boot-security)  
[SpringBootSecurity学习（13）前后端分离版之JWT](https://www.jianshu.com/p/4eda6471ae51)  
[重拾后端之Spring Boot（四）：使用JWT和Spring Security保护REST API](https://www.jianshu.com/p/6307c89fe3fa)  
一个spring-security采用jwt认证机制的demo。  
以下代码仅为说明代码作用，有的并不完整，如若要参考请git clone整个项目代码查看

**前言：本来是想尽量简单简单点的写一个demo的，但是spring-security实在是内容有点多，写着写着看起来就没那么简单了，想入门spring-security的话还是需要下些功夫的，这远没有Mybatis、JPA之类的容易入门**
## spring-security
config.securityConfig是springSecurity的安全配置类，在这个类中配置需要验证的接口、需要放行的接口，配置登录成功失败的处理器

### 1.最简单的用户角色权限控制demo  
最简单是demo是直接在securityConfig中配置存在内存中的用户对象，可以采用一下代码配置用户角色：  

```java
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication().passwordEncoder(new BCryptPasswordEncoder())
                .withUser("user").password({noop}123).roles("USER")
                .and()
                .withUser("admin").password({noop}123).roles("ADMIN")
                .and()
                .withUser("one").password({noop}123).roles("ONE")
                .and()
                .withUser("two").password({noop}123).roles("TWO");
    }
```
然后在securityConfig加注解开启接口的preAuth注解支持
```java
@Configuration
@EnableGlobalMethodSecurity(securedEnabled = true,prePostEnabled = true,jsr250Enabled = true)
public class securityConfig extends WebSecurityConfigurerAdapter {
```
然后可以直接在Controller的接口上加注解
```java
    /* 只有角色ONE才能访问 */
    @PreAuthorize("hasRole('ONE')")
    @GetMapping("/hello")
    public String hello(){
        return "hello Spring Security";
    }
```
然后访问localhost:8080/two，发现会跳转到login登录页面，此时以one登录进去可以正常访问，但是以其它角色访问均会出错。至此，最简单的demo已完成。

### 2.修改用户为数据库用户  
上面的用户是存在内存中的，接下来需要将其改为从数据库中获取用户信息并验证。  
首先需要在securityConfig中配置spring-security加载用户时使用的类，spring-security通过我们提供的这个类得到一个用户信息，该用户信息中一般包含用户名、密码、角色，spring-security得到这些信息后完成后续操作。  
提供该类给securityConfig

```
    @Qualifier("userDetailServiceImpl")
    @Autowired
    private UserDetailsService userDetailsService;
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService) // 提供给spring-security的类
                .passwordEncoder( new BCryptPasswordEncoder() );  // 这是密码加密的类，可以理解为将明文密码加密成hash值，可以先忽略照写
    }
```
然后需要实现这个类

```java
@Component
public class UserDetailServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserPasswordRepository userPasswordRepository;
    @Autowired
    private UserRoleRepository userRoleRepository;

    /**
     * 我的数据库表分为User表、UserInfo用户详细信息表、UserPassword密码表、UserRole用户角色表
     * spring-security会给这个方法提供一个用户名，然后我们实现根据用户名得到这个用户的UserDetail信息（类似于包含用户名、密码、角色的实体类，下一步重写它）
     * 然后返回的就是这个UserDetail，spring-security可以使用该类完成其它的操作
     * @param username
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findFirstByUsername(username);
        Integer id = user.getId();
        if (Objects.nonNull(user) && username.trim().length() <= 0) {
            throw new UsernameNotFoundException("用户名错误");
        }
        // 填充所有角色信息
        List<GrantedAuthorityImpl> grantedAuthorities = new ArrayList<>();
        List<UserRole> roles = userRoleRepository.findByCreator_Id(id);
        for (UserRole role : roles) {
            grantedAuthorities.add(new GrantedAuthorityImpl("ROLE_" + role.getRole()));
        }
        return new UserDetailImpl(
                    username,
                    userPasswordRepository.findByCreator_Id(id).getPassword(),
                    grantedAuthorities
                );
    }
}
```
实现UserDetail，这个类就像是一个实体类，但是实现了UserDetails接口，遵循spring-security的规范以让spring-security能使用它

```java
@NoArgsConstructor
@ToString
public class UserDetailImpl implements UserDetails {

    private String username;

    @JsonIgnore
    private String password;

    private List<GrantedAuthorityImpl> authorities;

    @JsonIgnore
    private boolean accountNonExpired;
    @JsonIgnore
    private boolean accountNonLocked;
    @JsonIgnore
    private boolean credentialsNonExpired;
    @JsonIgnore
    private boolean enabled;

    public UserDetailImpl(String username, String password, List<GrantedAuthorityImpl> authorities) {
        this.username = username;
        this.password = password;
        this.authorities = authorities;
        this.accountNonExpired = true;
        this.accountNonLocked = true;
        this.credentialsNonExpired = true;
        this.enabled = true;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}

```
最好再实现一下GrantedAuthority，这个是角色信息的规范接口

```
@NoArgsConstructor
public class GrantedAuthorityImpl implements GrantedAuthority {
    private String authority;

    public GrantedAuthorityImpl(String authority) {
        this.authority = authority;
    }

    @Override
    public String getAuthority() {
        return authority;
    }
}
```
上述这些就完成了spring-security用户表转移到数据库的操作了。

### 3.引入jwt
上述过程中，spring-security默认使用session-cookie的方法保存一个连接中的用户信息，然后拿这些用户信息到数据库查询。接下来可以改造成为jwt保存用户信息，jwt其实就是平时经常看到的token保存用户信息，其机制是直接将用户信息写在token中，然后就这个token进行签名后颁发给用户，用户发起请求时可以携带token，服务器就可以直接给用户认证信息了。  
首先我们先来构造jwt token。  
首先是jwt的工具类，该类提供信息HMACSHA256加密、信息签名、测试token是否合法
```java
public class JWTUtils {
    public static final String DEFAULT_HEADER = "\"alg\":\"HS256\",\"typ\":\"JWT\"";

    public static final String SECRET = "woshizengchunmiao";

    public static final long EXPIRE_TIME = 1000 * 60 * 60 * 24;

    public static final String HEADER_TOKEN_NAME = "Authrization";

    public static String encode(String input) {
        return Base64.getEncoder().encodeToString(input.getBytes());
    }

    public static String decode(String input) {
        return new String(Base64.getDecoder().decode(input));
    }

    public static String HMACSHA256(String data, String secret) throws Exception {
        Mac hmacSHA256 = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKeySpec = new SecretKeySpec(secret.getBytes("UTF-8"), "HmacSHA256");
        hmacSHA256.init(secretKeySpec);
        byte[] bytes = hmacSHA256.doFinal(data.getBytes("UTF-8"));
        StringBuilder sb = new StringBuilder();
        for (byte aByte : bytes) {
            sb.append(Integer.toHexString((aByte & 0xFF) | 0x100), 1, 3);
        }
        return sb.toString().toUpperCase();
    }

    public static String getSignature(String payload) throws Exception {
        return HMACSHA256(encode(DEFAULT_HEADER) + encode(payload), SECRET);
    }

    public static String testJwt(String jwt) {
        String[] split = jwt.split("\\.");
        try {
            if (!(HMACSHA256(split[0] + split[1], SECRET).equals(split[2]))) {
                return null;
            }
            if (!decode(split[0]).equals(DEFAULT_HEADER)) {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return decode(split[1]);
    }
}
```
然后提供一个JWT类，构造该类时，只需要将想放在token上的信息传入构造函数，即可得到一个想要的JWT，调用toString方法就得到了token

```java
public class JWT {
    private String header;

    private String payload;

    private String signature;

    public JWT(String payload) throws Exception {
        this.payload = JWTUtils.encode(payload);
        this.header = JWTUtils.encode(JWTUtils.DEFAULT_HEADER);
        this.signature = JWTUtils.getSignature(payload);
    }

    @Override
    public String toString() {
        return header + "." + payload + "." + signature;
    }
}
```
### 4.jwt设置到spring-security
以上两个类就完成了token的构造，然后我们需要用它来代替spring-security中的session-cookie机制。首先需要将spring-security的session关闭，实质上我的理解是，token是一个虚拟的session，每次建立连接时，spring-security将它解析出来把它作为认证信息放到Holder里。
关闭session,在securityConfig

```java
@Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                // 设置无session
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
    }
```
然后需要写一个Filter，在spring-security进行用户名-密码验证前抢先发生，对token进行验证，若token合法就放入认证信息，就完成了安全认证；若token不合法直接失败。  
先配置这个Filter到config中
```java
@Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                // 拦截登录请求
                .addFilterBefore(new JwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
    }
```
然后实现这个Filter

```java
/**
 * 验证token是否正确，并从token中还原"session"信息
 */
public class JwtAuthenticationFilter extends GenericFilter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String token = request.getHeader(JWTUtils.HEADER_TOKEN_NAME);   // 从请求头中拿到token
        if (Objects.nonNull(token) && token.trim().length() > 0) {
            String payload = JWTUtils.testJwt(token);   // 从token中拿到payload
            if (Objects.nonNull(payload) && payload.trim().length() > 0) {
                ObjectMapper objectMapper = new ObjectMapper();
                // 我这个项目的payload是UserDetailImp的序列化后的Json，这里将其还原为UserDetailImpl对象
                UserDetailImpl user = objectMapper.readValue(payload, UserDetailImpl.class);   
                // 将还原得到的认证信息交给spring-security管理（用户信息,认证,用户角色表）
                SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(user,null,user.getAuthorities()));
            }
        }
        filterChain.doFilter(servletRequest,servletResponse);
    }
}
```
以上，就完成了spring-security使用JWT的全部过程。可以测试使用了

为了方便测试，我还提供了SuccessHandle、FailureHandle、AccessDeniedHandlerImpl用于spring-security登录成功、登录失败、没有认证信息的处理器，其中，SuccessHandle在登录成功后返回当前认证信息的token，拿这个token放到请求头访问接口时，即可自动完成认证。

## 测试
提供了一个hello的Controller层接口
```
@RestController
public class hello {
    // 拥有ADMIN角色才可以访问
    @PreAuthorize("hasAnyRole('ADMIN')")
    @RequestMapping("/hello")
    String test() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getPrincipal() instanceof UserDetailImpl) {
            UserDetailImpl user = (UserDetailImpl) authentication.getPrincipal();
            System.out.println(user.getUsername());
            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
            for (GrantedAuthority authority : authorities) {
                System.out.println(authority.getAuthority());
            }
        }
        return "hello";
    }
}
```
直接访问该接口，由于没有认证，会跳转到/login接口下
![image](/media/Snipaste_2020-10-16_15-19-30.png)
以admin-123登录后跳转成功页面并得到token
![image](/media/Snipaste_2020-10-16_15-21-06.png)
复制token，放到postman的header里，然后再次请求/hello
![image](/media/Snipaste_2020-10-16_15-24-05.png)
发现成功得到响应
![image](/media/Snipaste_2020-10-16_15-25-38.png)
到控制台看看，得到用户名和角色名
![image](/media/Snipaste_2020-10-16_15-27-08.png)

换user-123角色的token登录看看
![image](/media/Snipaste_2020-10-16_15-28-25.png)
![image](/media/Snipaste_2020-10-16_15-29-05.png)

还可以更换root角色，不再赘述。






