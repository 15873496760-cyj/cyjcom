 //控制层 
app.controller('goodsController' ,function($scope,$controller,itemCatService,goodsService){
	
	$controller('baseController',{$scope:$scope});//继承
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		goodsService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){			
		goodsService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//查询实体 
	$scope.findOne=function(id){				
		goodsService.findOne(id).success(
			function(response){
				$scope.entity= response;					
			}
		);				
	}
	
	//保存 
	$scope.save=function(){				
		var serviceObject;//服务层对象
		//得到富文本编辑的值，并给$scope.entity.goodsDesc.introduction赋值
        $scope.entity.goodsDesc.introduction = editor.html();
		if($scope.entity.goods.id){//如果有ID
			serviceObject=goodsService.update( $scope.entity ); //修改  
		}else{
			serviceObject=goodsService.add( $scope.entity  );//增加 
		}				
		serviceObject.success(
			function(response){
				if(response.success){
					//重新查询 
		        	$scope.reloadList();//重新加载
				}else{
					alert(response.message);
				}
			}		
		);				
	}
	
	 
	//批量删除 
	$scope.dele=function(){			
		//获取选中的复选框			
		goodsService.dele( $scope.selectIds ).success(
			function(response){
				if(response.success){
					$scope.search();//刷新列表
					$scope.selectIds=[];
				}						
			}		
		);				
	}
	
	$scope.searchEntity={};//定义搜索对象 
	
	//搜索
	$scope.search=function(){
		goodsService.search($scope.paginationConf.currentPage,$scope.paginationConf.itemsPerPage,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
    //定义状态值数组
    $scope.status=["未审核","己审核","审核未通过","己关闭"];
    //定义代表分类的数组
    $scope.categoryList = [];
    //查询所有的分类
    $scope.findAllCategorys=()=>{
        itemCatService.findAll().success(response=>{
            //遍历分类，将分类以分类id为key，分类的名字为值放到一个对象数组中
            for(var i = 0;i <response.length;i++){
                var itemCat = response[i];	//得到一个分类
                $scope.categoryList[itemCat.id] = itemCat.name;
            }
        })
    }
    //商品审核
	$scope.updateStatus=(status)=>{
    	goodsService.updateStatus(status,$scope.selectIds).success(response=>{
    		if(response.success){
    			$scope.search();
			}else{
    			alert(response.message);
			}
		})
	}
});	
