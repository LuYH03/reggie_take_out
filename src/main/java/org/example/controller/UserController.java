package org.example.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.example.common.R;
import org.example.entity.User;
import org.example.service.UserService;
import org.example.utils.SMSUtils;
import org.example.utils.ValidateCodeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

  /*  @Autowired
    private RedisTemplate redisTemplate;*/
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 发送手机短信验证码
     * @param user
     * @param sesstion
     * @return
     */
    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession sesstion){
        // 获取手机号
        String phone = user.getPhone();

        if (StringUtils.isNotEmpty(phone)){
            // 生成随机验证码
            String code = ValidateCodeUtils.generateValidateCode(4).toString();
            log.info("code={}",code);
            // 调用阿里云短信服务api
            /*SMSUtils.sendMessage("阿里云短信测试","SMS_154950909",phone,code);*/
            // 将生成的验证码保存到Sesstion
            // sesstion.setAttribute(phone,code);

            // 将生成的验证码缓存到redis中,并设置过期时间为五分钟
            stringRedisTemplate.opsForValue().set(phone,code,5, TimeUnit.MINUTES);

            return R.success("手机验证码短信发送成功");
        }

        return R.error("短信发送失败");
    }


    /**
     * 移动端用户登录
     * @param map
     * @param session
     * @return
     */
    @PostMapping("/login")
    public R<User> login(@RequestBody Map map, HttpSession session){
        log.info(map.toString());
        // 获取手机号
        String phone = map.get("phone").toString();
        // 获取验证码
        String code = map.get("code").toString();
        // 从Session中获取保存的验证码
        //Object codeInsession = session.getAttribute(phone);
        // 从缓存中获取保存的验证码
        Object codeInsession = stringRedisTemplate.opsForValue().get(phone);
        // 进行验证码的比对
        if (codeInsession != null && codeInsession.equals(code)){
            // 如果能够比对成功，说明登录成功

            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getPhone,phone);
            User user = userService.getOne(queryWrapper);
            if (user == null){
                // 判断当前手机号对应的用户是否为新用户，如果是新用户就自动完成注册
                user = new User();
                user.setPhone(phone);
                user.setStatus(1);
                userService.save(user);
            }
            session.setAttribute("user",user.getId());

            stringRedisTemplate.delete(phone);
            return R.success(user);
        }

        return R.error("登录失败");
    }


}
