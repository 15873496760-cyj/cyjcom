app.service("searchService", function ($http) {
    this.findByTerm=(searchMap)=>{
        return $http.post("./itemSearch/search.do",searchMap);
    }
});