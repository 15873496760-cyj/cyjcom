var app = angular.module("pinyougou",[]);
app.filter("showHtml",["$sce",function ($sce) {
    return function (data) {
        return $sce.trustAsHtml(data);
    }
}])