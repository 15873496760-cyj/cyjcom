 //控制层 
app.controller('goodsController' ,function($scope,$controller,$location,itemCatService,typeTemplateService,uploadService,goodsService){
	
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
	$scope.findOne=function(){
		var id = $location.search()["id"];
		goodsService.findOne(id).success(
			function(response){
				$scope.entity= response;
				//为富文本框赋值
				editor.html($scope.entity.goodsDesc.introduction);
				//转换entity中的json串为json对象
				//1.转换图片
				$scope.entity.goodsDesc.itemImages=JSON.parse($scope.entity.goodsDesc.itemImages);
				//2.转换扩展属性
				$scope.entity.goodsDesc.customAttributeItems=JSON.parse($scope.entity.goodsDesc.customAttributeItems);
				//3.转换规格列表
				$scope.entity.goodsDesc.specificationItems=JSON.parse($scope.entity.goodsDesc.specificationItems);
				//4.转换sku商品列表中的spec这个json字段
				var items = $scope.entity.items;
				for(var i = 0;i <items.length;i++){
					//4.1)得到某一个sku商品（tb_item表的一条数据）
					var item = items[i];
					//4.2)转换其中的spec这个字段
					item.spec=JSON.parse(item.spec);
				}

			}
		);
	}
	//根据指定的规格名称及规格选项的值，判断是否复选某个规格
	$scope.checkAttribute=(specName,optionName)=>{
		//1.得到规格选项列表
		var items = $scope.entity.goodsDesc.specificationItems;
		//2.在集合中根据key="attributeName"，查询是否存在指定的规格名称
		var object = searchObjectByKey(items,"attributeName",specName);
		if(object == null){
			return false;
		}else{
			// if(object.attributeValue.indexOf(optionName) >= 0){  //判断在数组中是否有指定的规格选项
			// 	return true;
			// }
			// return false;
			//或者
			return object.attributeValue.indexOf(optionName) >= 0;
		}
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
					//添加或修改后，要清空原来的数据
					$scope.entity={};
					editor.html("");		//清空富文本编辑器
				}else{
					alert(response.message);
				}
			}		
		);				
	}
	//到修改页面
	$scope.toUpdate=(id)=>{
		location.href="goods_edit.html#?id="+id;
	}
	 
	//批量删除 
	$scope.dele=function(){			
		//获取选中的复选框			
		goodsService.dele( $scope.selectIds ).success(
			function(response){
				if(response.success){
					$scope.reloadList();//刷新列表
					$scope.selectIds=[];
				}						
			}		
		);				
	}
	
	$scope.searchEntity={};//定义搜索对象 
	
	//搜索
	$scope.search=function(page,rows){			
		goodsService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
    //文件上传
	$scope.uploadFile=()=>{
		uploadService.uploadFile().success(response=>{
			if(response.success){
				//1。上传成功
				$scope.image_entity.url=response.message;
			}else{
				alert(response.message);
			}
		})
	}
    $scope.entity={goods:{},goodsDesc:{itemImages:[],specificationItems:[]}};
	//将上传的文件对象添加到$scope.entity.goodsDesc.itemImages中
	$scope.addItemImages=()=>{
        $scope.entity.goodsDesc.itemImages.push($scope.image_entity);
	}
	//删除图片
	$scope.removePic=(index)=>{
        $scope.entity.goodsDesc.itemImages.splice( index,1);
	}
    //根据父id查询此父id下的所有子节点列表
    $scope.findByParentId=(parentId)=>{
        itemCatService.findByParentId(parentId).success(response=>{
            //如果传入的是0的话，就是一级分类
            $scope.itemCatList1 = response;
        })
    }

    //当一级分类发生变化时，会自动查询此分类下的所有二级分类列表
	//回调函数中，参数1：代表新选中的值  参数2：代表未选中之间的值
	$scope.$watch("entity.goods.category1Id",function (newValue,oldValue) {
		itemCatService.findByParentId(newValue).success(response=>{
			$scope.itemCatList2 = response;
		})
    })
	//当二级分类发生变化时，会自动查询三级分类
	$scope.$watch("entity.goods.category2Id",function (newValue,oldValue) {
        itemCatService.findByParentId(newValue).success(response=>{
            $scope.itemCatList3 = response;
        })
    })
	//当三级分类发生变化时，其对应的模板也会发生改变
    $scope.$watch("entity.goods.category3Id",function (newValue,oldValue) {
    	//1.查询得到模板id
		itemCatService.findOne(newValue).success(response=>{
            $scope.entity.goods.typeTemplateId = response.typeId;
		})
    });
	//监控模板id的值发生变化时，就会对应的品牌列表
    $scope.$watch("entity.goods.typeTemplateId",function (newValue,oldValue) {
        typeTemplateService.findOne(newValue).success(response=>{
        	//1.根据模板id查询出此模板对应的品牌列表
        	$scope.brandList=JSON.parse(response.brandIds);
        	//2.根据模板id查询出此模板对应的扩展属性
			//2.1)得到商品id
			var id = $location.search()["id"];
			if(id == null){  //代表添加操作
                $scope.entity.goodsDesc.customAttributeItems = JSON.parse(response.customAttributeItems);
			}

		})
        typeTemplateService.findSpecByTypeId(newValue).success(response=>{
        	$scope.specList=response;
		})
    })
	//查找在指定数组中是否包含指定的元素
    //举例如下：
	//Var list = [{“id”:”11”,”text”:”aaa”},{“id”:”12”,”text”:”bbb”}]
	//searchObjectByKey (list,”text”,”aaa”) 返回：{“id”:”11”,”text”:”aaa”}
	searchObjectByKey=(list,key,value)=>{
    	for(var i = 0;i < list.length;i++){
    		if(list[i][key] == value){
    			return list[i];
			}
		}
		return null;
	}
	//根据用户选择的规格选项动态创建 $scope.entity.goodsDesc.specifictionItems这个数组对象
	//参数1：代表当前点击所触发的事件对象
	//参数2：代表当前key的值，如：attrubiteName的值
	//参数3：代表规格选项的值
	$scope.updateSelectOption=(event,name,value)=>{
    	//1.在$scope.entity.goodsDesc.specifictionItems查询指定的对象
        var obj = searchObjectByKey($scope.entity.goodsDesc.specificationItems,"attributeName",name);
        //2.判断对象是否存在
		//2.1)如果存在此对象
		if(obj){
			if(event.target.checked){  //如果勾选了某个规格选项的值
                obj.attributeValue.push(value);
			}else{					   //如果未被复选，就从obj.attributeValue这个数组中删除它
				var index = obj.attributeValue.indexOf(value);
				obj.attributeValue.splice(index,1);
				//删除此选项后，再判断obj.attributeValue是否为null，如果是就从$scope.entity.goodsDesc.specifictionItems删除obj对象
				if(obj.attributeValue.length == 0){
					//① 得到要删除的索引
					var index1 = $scope.entity.goodsDesc.specificationItems.indexOf(obj);
					//② 从$scope.entity.goodsDesc.specificationItems这个集合中删除此元素
                    $scope.entity.goodsDesc.specificationItems.splice(index1,1);
				}
			}
		}else{//2.2）如果不存在此对象，就将当前的值设置进这个对象中
            $scope.entity.goodsDesc.specificationItems.push({"attributeName":name,"attributeValue":[value]});
		}
        createItemList();
	}
	//生成sku商品列表
	createItemList=()=>{
    	//1.先为sku商品列表赋初始值
    	$scope.entity.items=[{spec:{},num:0,price:999,isDefault:0,status:0}];
        //2.获取规格列表
        var items = $scope.entity.goodsDesc.specificationItems;

        //3.遍历这个商品列表，动态生成新的商品列表
		for(var i = 0;i < items.length;i++){
            $scope.entity.items=addColumn($scope.entity.items,items[i].attributeName,items[i].attributeValue);
		}
	}
	//动态生成sku商品列表
     addColumn=(items, attributeName, attributeValue) =>{
    	//1.定义一个空的数组用于存放动态生成的sku列表
		 var newList = [];
		 //2.遍历items，得到原来的数据，并根据原来的数据进行深克隆，目的是为了与原来的数据分开
		 for(var i = 0;i < items.length;i++){
		 	//2.1)得到原来的旧的一行数据
			 var oldRow = items[i];
			 //2.2)根据旧的数据进行深克隆得到一个新行
			 for(var j = 0; j < attributeValue.length;j++){
			 	//2.3)对原来的行进行深克隆
			 	var newRow = JSON.parse(JSON.stringify(oldRow));
			 	//2.4)对新行的spec属性进行赋值
			 	newRow.spec[attributeName]=attributeValue[j];
			 	//2.5)将新行添加到newlist数组中
				 newList.push(newRow);
			 }
		 }
         // [{spec:{“网络”:”3G”},price:0,num:99999,status:'0',isDefault:'0' },
		// {spec:{“网络”:”4G”},price:0,num:99999,status:'0',isDefault:'0' }]
        return newList;
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
});	
