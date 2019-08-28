app.controller("searchController",function ($scope,searchService) {

    //关键字查询
    $scope.findByTerm=()=>{
        searchService.findByTerm($scope.searchMap).success(response=>{
            $scope.itemList = response;
        })
    }
})