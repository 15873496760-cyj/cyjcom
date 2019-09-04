app.controller("userController",function ($scope,$controller,userService) {
    $controller('baseController',{$scope:$scope});//继承


    $scope.getValidCode=()=>{
        userService.getValidCode($scope.entity.phone).success(response=>{
            if (response.success) {
                alert(response.message);
            } else {
                alert(response.message);
            }
        })
    }

    $scope.register=()=>{
        if($scope.entity.password != $scope.repassword){
            alert("两次密码不一致！");
            return;
        }
        userService.add($scope.entity,$scope.validCode).success(response=>{
            if(response.success){
                $scope.entity={};
            }else{
                alert(response.message);
            }
        })
    }
})