package com.pinyougou.shop.service;

import com.pinyougou.pojo.TbSeller;
import com.pinyougou.sellergoods.service.SellerService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.List;


public class UserServiceImpl implements UserDetailsService {
    private SellerService sellerService;

    public void setSellerService(SellerService sellerService) {
        this.sellerService = sellerService;
    }

    @Override
    public UserDetails loadUserByUsername(String sellerId) throws UsernameNotFoundException {
        //根据sellerId得到商家对象
        TbSeller seller = sellerService.selectByPrimaryKey(sellerId);
        //判断
        if(seller != null){
            if(seller.getStatus().equals("1")){ //如果申核通过
                //为用户指定角色列表
                List<GrantedAuthority> authorities = new ArrayList<>();
                authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
                return new User(sellerId,seller.getPassword(),authorities);
            }
            return null;
        }
        return null;
    }
}
