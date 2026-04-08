<%-- 
    Document   : db_manager_index
    Created on : Dec 12, 2017, 10:21:54 AM
    Author     : imtiaz
--%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<html lang="en" ng-app="SyncApp">
    <head>
        <%@include file="common/resoucelink_head.jsp" %>
    </head>
    <body>
        <div id="wrapper" class="toggled">
            <%@include file="common/top_menu.jsp" %>
            <%@include file="common/left_menu.jsp" %>

            <div id="page-content-wrapper">
                <div class="container-fluid" ng-controller="AppManagerCrtl" ng-init="loadRecords()">

                    <div class="row">
                        <!-- Success/Error Message Display Area -->
                        <div class="col-md-12" ng-show="showMessage">
                            <div class="alert alert-dismissible" 
                                 ng-class="{'alert-success': isSuccess, 'alert-danger': !isSuccess}"
                                 style="margin: 15px 0;">
                                <button type="button" class="close" ng-click="hideMessage()">&times;</button>
                                <strong>
                                    <i ng-class="{'fa fa-check-circle': isSuccess, 'fa fa-exclamation-circle': !isSuccess}"></i>
                                    {{isSuccess ? 'Success!' : 'Error!'}}
                                </strong> 
                                {{messageText}}
                            </div>
                        </div>
                        <div class="col-md-12"><h3>{{NAME}}</h3></div>

                        <div class="col-md-12">
                            <form>
                                <div class="input-group">
                                    <span class="input-group-addon">TYPE</span>
                                    <select class="form-control" ng-model="record.TYPE" required="true">
                                        <option value="" disabled="disabled">SELECT APP TYPE</option>
                                        <option ng-repeat="obj in typeList" value="{{obj}}">{{obj}}</option>
                                    </select>
                                </div>
                                <div class="input-group">
                                    <span class="input-group-addon">NAME</span>
                                    <input type="text" class="form-control" ng-model="record.NAME" placeholder="NAME" required="true">
                                </div>
                                <div class="input-group">
                                    <span class="input-group-addon">CODE</span>
                                    <input type="text" class="form-control" ng-model="record.CODE" placeholder="CODE" required="true">
                                </div>
                                <div class="input-group">
                                    <span class="input-group-addon">STATE</span>
                                    <select class="form-control" ng-model="record.STATE" required="true">
                                        <option value="1">Active</option>
                                        <option value="0">Inactive</option>
                                    </select>
                                </div>
                                <!-- Update Button with Processing State -->
                                <button class="btn btn-success" ng-click="submitObject('UPDATE')" ng-disabled="isProcessing">
                                    <span ng-if="!isProcessing"><i class="fa fa-check"></i> Update</span>
                                    <span ng-if="isProcessing"><i class="fa fa-spinner fa-spin"></i> Processing...</span>
                                </button>
                            </form>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <%@include file="common/resoucelink_footer.jsp" %>
    </body>
</html>