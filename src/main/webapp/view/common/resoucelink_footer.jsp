<%-- 
    Document   : resoucelink_head
    Created on : Dec 12, 2017, 11:21:32 AM
    Author     : imtiaz
--%>

<script src="<c:url value="/resources/js/jquery-2.2.3.min.js" />"></script>
<script src="<c:url value="/resources/js/jquery-ui.min.js" />"></script>

<script src="<c:url value="/resources/bootstrap/js/bootstrap.min.js" />"></script>
<!-- AngularJS 1.6.6 -->
<script src="<c:url value="/resources/angular/angular.min.js" />"></script>
<script src="<c:url value="/resources/angular/ui-bootstrap-tpls-0.10.0.min.js" />"></script>

<script src="<c:url value="/resources/angular/angular-animate.min.js" />"></script>
<script src="<c:url value="/resources/angular/angular-aria.min.js" />"></script>
<script src="<c:url value="/resources/angular/angular-messages.min.js" />"></script>
<script src="<c:url value="/resources/angular/angular-sanitize.js" />"></script>

<script src="<c:url value="/resources/angular/angular-material.min.js" />"></script>



<script src="<c:url value="/resources/jqx-ng/jqxcore.js" />"></script>
<script src="<c:url value="/resources/jqx-ng/jqxdata.js" />"></script>
<script src="<c:url value="/resources/jqx-ng/jqxbuttons.js" />"></script>
<script src="<c:url value="/resources/jqx-ng/jqxscrollbar.js" />"></script>
<script src="<c:url value="/resources/jqx-ng/jqxdatatable.js" />"></script>

<script src="<c:url value="/resources/jqx-ng/jqxgrid.js" />"></script>
<script src="<c:url value="/resources/jqx-ng/jqxlistbox.js" />"></script>
<script src="<c:url value="/resources/jqx-ng/jqxgrid.filter.js" />"></script>
<script src="<c:url value="/resources/jqx-ng/jqxgrid.sort.js" />"></script>
<script src="<c:url value="/resources/jqx-ng/jqxdropdownlist.js" />"></script>

<script src="<c:url value="/resources/jqx-ng/jqxmenu.js" />"></script>
<script src="<c:url value="/resources/jqx-ng/jqxgrid.selection.js" />"></script>
<script src="<c:url value="/resources/jqx-ng/jqxgrid.pager.js" />"></script>

<script src="<c:url value="/resources/jqx-ng/jqxangular.js" />"></script>

<!-- SweetAlert2 JS -->
<script src="<c:url value="/resources/js/sweetalert2-11.14.0.js" />"></script>


<script type="text/javascript">
    var _baseurl_ = "${BASE_URL != null ? BASE_URL : ''}";
    var IS_SERVER = ${IS_SERVER != null ? IS_SERVER : false};
    var IS_CLIENT = ${IS_CLIENT != null ? IS_CLIENT : false};
    
    $(document).ready(function(){        
        $("#menu-toggle").click(function(e) {
            e.preventDefault();
            $("#wrapper").toggleClass("toggled");
        });
    });
    
    
    
    var app = angular.module('SyncApp', ['ui.bootstrap', 'ngMaterial', 'ngMessages', 'ngSanitize', 'ngAnimate', 'jqwidgets']);
    app.directive('onFinishRender', function ($timeout) {
        return {
            restrict: 'A',
            link: function (scope, element, attr) {
                if (scope.$last === true) {
                    $timeout(function () {
                        scope.$emit(attr.onFinishRender);
                    });
                }
            }
        };
    });
</script>

<script src="<c:url value="/resources/controllers/app.services.js" />"></script>
<script src="<c:url value="/resources/controllers/controller.readme.js" />"></script>
<script src="<c:url value="/resources/controllers/controller.appmanager.js" />"></script>
<script src="<c:url value="/resources/controllers/controller.svrmanager.js" />"></script>
<script src="<c:url value="/resources/controllers/controller.clientmanager.js" />"></script>
<script src="<c:url value="/resources/controllers/controller.dbmanager.js" />"></script>
<script src="<c:url value="/resources/controllers/controller.synctblmanager.js" />"></script>
