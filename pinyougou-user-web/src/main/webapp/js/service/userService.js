app.service("userService",function ($http) {
    //点击获取验证码
    this.getValidCode=(phone)=>{
        return $http.get("./user/getValidCode.do?phone="+phone)
    }

    //注册
    this.add=(user,validCode)=>{
        return $http.post("./user/add.do?validCode="+validCode,user);
    }
})