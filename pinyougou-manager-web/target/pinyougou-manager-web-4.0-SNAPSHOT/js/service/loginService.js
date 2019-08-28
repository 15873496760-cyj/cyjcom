//服务层
app.service('loginService',function($http){
	 //1.获取登录的用户名
	this.loginName=()=>{
		return $http.get("../login.do");
	}
});
