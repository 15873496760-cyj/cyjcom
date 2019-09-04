package com.pinyougou.user.service;

import com.pinyougou.pojo.TbUser;

/**
 * Created by WF on 2019/9/3 10:28
 */
public interface UserService {
    void sendValidCode(String phone);

    boolean checkValidCode(String phone, String validCode);

    void add(TbUser user);
}
