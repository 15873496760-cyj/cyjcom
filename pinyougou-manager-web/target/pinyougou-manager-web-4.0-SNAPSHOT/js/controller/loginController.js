 //控制层
app.controller('loginController' ,function($scope,$controller,loginService){
	
	$controller('baseController',{$scope:$scope});//继承


	//到后台获取当前登录的用户名
	$scope.loginName=()=>{
		loginService.loginName().success(response=>{
			$scope.name=response.name;
		})
	}
});	
