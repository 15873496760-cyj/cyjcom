app.controller("baseController",function($scope){
    //分页控件配置
    $scope.paginationConf = {
        currentPage: 1,
        totalItems: 10,
        itemsPerPage: 10,
        perPageOptions: [10, 20, 30, 40, 50],
        onChange: function(){
            $scope.search($scope.paginationConf.currentPage,$scope.paginationConf.itemsPerPage);
        }
    };
    //定义选择的对象的id数组
    $scope.selectIds=[];
    //定义方法：实现选择某个id时，向数组中放id，取消选择时从数组中删除此id
    $scope.updateSelection=(event,id)=>{
        //event.target:代表我们当前正在选择的哪个复选框对象
        if(event.target.checked){       //如果此复选框被复选，就将其放到$scope.selectIds这个数组中
            $scope.selectIds.push(id);
        }else{                          //否则，就从数组中删除它
            //得到我们要删除的id在数组中的下标索引
            var index = $scope.selectIds.indexOf(id);
            //参数1：代表要删除的元素在数组中下标索引  参数2：代表要删除的元素个数
            $scope.selectIds.splice(index,1);
        }
    }
})