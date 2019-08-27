app.controller("contentController",function ($scope,$controller,contentService) {
    $controller('baseController',{$scope:$scope});//继承

    //定义存放广告列表的数组
    $scope.contentList = [];
    //1.根据分类id查询广告列表
    $scope.findContentByCategoryId=(categoryId)=>{
        contentService.findContentByCategoryId(categoryId).success(response=>{
           for(var i = 0,len = response.length;i < len;i++){
               $scope.contentList[categoryId] = response;
           }
        })
    }

})