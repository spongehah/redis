package com.hmdp.interceptor;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.hmdp.dto.UserDTO;
import com.hmdp.utils.RedisConstants;
import com.hmdp.utils.UserHolder;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class LoginInterceptor implements HandlerInterceptor {

    private StringRedisTemplate stringRedisTemplate;
    /*
    自写的类，并没有被spring管理，不能注入
    在MvcConfig中使用构造注入
     */

    public LoginInterceptor(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // /*// 1.获取session
        // HttpSession session = request.getSession();
        // // 2.获取session中的用户
        // Object user = session.getAttribute("user");*/
        //
        // // TODO 1 获取请求头中的token
        // String token = request.getHeader("authorization");
        // if (StrUtil.isBlank(token)){
        //     // 不存在，拦截，返回401状态码
        //     response.setStatus(401);
        //     return false;
        // }
        // // TODO 2 基于token获取redis中的用户信息
        // String tokenKey = RedisConstants.LOGIN_USER_KEY + token;
        // Map<Object, Object> userMap = stringRedisTemplate.opsForHash().entries(tokenKey);
        //
        //
        // // 3.判断用户是否存在
        // if (userMap.isEmpty()) {
        //     // 4.不存在，拦截，返回401状态码
        //     response.setStatus(401);
        //     return false;
        // }
        // /*// 5.存在，保存用户信息到Threadlocal
        // UserHolder.saveUser((UserDTO) user);*/
        //
        // // TODO 5 将查询到的对象转换为UserDTO对象
        // UserDTO userDTO = BeanUtil.fillBeanWithMap(userMap, new UserDTO(), false);
        // // 6.存在，保存用户信息到Threadlocal
        // UserHolder.saveUser(userDTO);
        //
        // // TODO 7 刷新token有效期
        // stringRedisTemplate.expire(tokenKey,RedisConstants.LOGIN_USER_TTL, TimeUnit.MINUTES);
        //
        // // 放行
        // return true;

        /**
         * 因为该拦截器拦截的都是需要访问验证的请求，但是登陆后所有的请求都应该刷新token
         * 所以将上面的逻辑移到RefreshTokenInterceptor中
         * 此拦截器只需要进行拦截即可
         */
        // 1.判断是否需要拦截（ThreadLocal中是否有用户）
        if (UserHolder.getUser() == null) {
            // 没有，需要拦截，设置状态码
            response.setStatus(401);
            // 拦截
            return false;
        }
        // 有用户，则放行
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 移除用户
        UserHolder.removeUser();
    }
}
