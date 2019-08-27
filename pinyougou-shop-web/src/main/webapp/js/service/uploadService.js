//服务层
app.service('uploadService',function($http){
	    	
	//1。文件上传
	this.uploadFile=()=>{
		//1.1)构造文件上传的数据
		var formData = new FormData();
		formData.append("file",file.files[0]);
		//1.2)向后台发出上传请求
		return $http({
			method:"post",
			data:formData,
			url:"../upload.do",
			headers:{"Content-Type":undefined},
			transformRequest:angular.identity
		})
	}
});
