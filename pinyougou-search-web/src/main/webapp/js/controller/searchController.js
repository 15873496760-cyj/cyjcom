app.controller("searchController",function ($scope,$controller,$location,searchService) {

    $controller('baseController',{$scope:$scope});//继承

    //关键字查询
    $scope.findByTerm=()=>{
        $scope.searchMap.page=parseInt($scope.searchMap.page);
        $scope.searchMap.pagesize=parseInt($scope.searchMap.pagesize);

        var keywords = $location.search()["keywords"];
        if(keywords){
            $scope.searchMap.keywords = keywords;
        }
        searchService.findByTerm($scope.searchMap).success(response=>{
            $scope.itemList = response;
            buildPageLabel();
        })
    }

    $scope.searchMap={"keywords":'',"category":'',"brand":'',"spec":{},'price':'','page':1,'pagesize':20,'sort':"",'sortField':""}

    $scope.addSearchItem=function (key,value) {
        if (key=="category" || key=="brand" || key == "price") {
            $scope.searchMap[key] = value;
        } else {
            $scope.searchMap.spec[key] = value;
        }
        $scope.findByTerm();
    }

    $scope.removeSearchItem=(key)=>{
        if(key=="category" || key=="brand"){
            $scope.searchMap[key] = "";
        }else {
            delete $scope.searchMap.spec[key];
        }
        $scope.findByTerm();
    }

    buildPageLabel = ()=>{
        $scope.pageLable=[];
        //设置首页
        var firstPage = 1;
        //设置最后一页的值
        var lastPage = $scope.itemList.totalPages;

        if ($scope.itemList.totalPages >= 5) {
            if ($scope.searchMap.page <= 3) {
                lastPage = 5;
            }else if ($scope.searchMap.page >= $scope.itemList.totalPages - 2) {
                firstPage = $scope.itemList.totalPages - 4;
            } else {
                firstPage = $scope.searchMap.page - 2;
                lastPage = $scope.searchMap.page + 2;
            }
        }
        for (var i = firstPage; i <= lastPage; i++) {
            $scope.pageLable.push(i);
        }
    }

    //向上向下翻页
    $scope.toPage=(page)=>{
        $scope.searchMap.page = parseInt(page);
        console.log(page);
        $scope.findByTerm();
    }

    //排序查询
    $scope.findTermBySort=(sort,field)=>{
        $scope.searchMap.sort = sort;
        $scope.searchMap.sortField = field;
        $scope.findByTerm();
    }

    //隐藏品牌
    $scope.hideBrandList=()=>{
        for (var i = 0; i < $scope.itemList.brandList.length; i++) {
            if ($scope.searchMap.keywords.indexOf($scope.itemList.brandList[i].text) >= 0) {
                return true;
            }
        }
        return false;
    }
})