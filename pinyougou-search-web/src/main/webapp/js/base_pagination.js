var app=angular.module('pinyougou',['pagination']);
app.filter("showHtml",["$sce",function ($sce) {
    return function (data) {
        return $sce.trustAsHtml(data);
    }
}])