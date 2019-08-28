app.service("contentService",function ($http) {
    //1.查询广告通过广告分类id查询
    this.findContentByCategoryId=(categoryId)=>{
        return $http.get("./content/findContentByCategoryId.do?categoryId="+categoryId);
    }
})