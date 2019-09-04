package com.pinyougou.user.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.pinoyougou.util.PhoneFormatCheckUtils;
import com.pinyougou.pojo.Result;
import com.pinyougou.pojo.TbUser;
import com.pinyougou.user.service.UserService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/user")
@RestController
public class UserController {

    @Reference
    private UserService userService;

    @RequestMapping("/getValidCode")
    public Result getValidCode(String phone) {
        try {
            if (PhoneFormatCheckUtils.isPhoneLegal(phone)) {
                userService.sendValidCode(phone);
                return new Result(true, "短信发送成功！");
            }
            return new Result(false, "手机号不合法");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "短信发送失败！");
        }
    }

    @RequestMapping("add")
    /**
     * 添加用户
     */
    public Result add(@RequestBody TbUser user, String validCode){
        try {
            //检查用户输入的验证码是否正确
            boolean b = userService.checkValidCode(user.getPhone(),validCode);
            //添加用户
            userService.add(user);
            return new Result(true,"注册成功！");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"注册失败！");
        }
    }

}
