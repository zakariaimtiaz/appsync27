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
                <div class="container-fluid" ng-controller="ClientManagerCrtl" ng-init="loadRecords()">

                    <div class="row">

                        <div class="col-md-12"><h3>{{NAME}}</h3></div>

                        <div class="col-md-12" ng-show="entryMode===1">
                            <form>

                                <div class="input-group">
                                    <span class="input-group-addon">NAME</span>
                                    <input type="text" class="form-control" ng-model="record.NAME" placeholder="NAME">
                                </div>
                                <div class="input-group">
                                    <span class="input-group-addon">CODE</span>
                                    <input type="text" class="form-control" ng-model="record.CODE" placeholder="CODE">
                                </div>

                                <button class="btn btn-primary" ng-show="editMode===0" ng-click="submitObject('NEW')"> <i class="fa fa-check"></i> Save</button>
                                <button class="btn btn-success" ng-show="editMode===1" ng-click="submitObject('UPDATE')"> <i class="fa fa-check"></i> Update</button>

                                <button class="btn btn-danger" ng-show="editMode===0" ng-click="clearForm()"> <i class="fa fa-refresh"></i> Reset</button>
                                <button class="btn btn-warning" ng-click="closeMode()"> <i class="fa fa-close"></i> Close</button>

                            </form>
                        </div>

                        <div class="col-md-12" ng-show="entryMode===0">

                            <div class="table-responsive">
                                <label ng-show="showAlert===1" class="label label-info">Data has been saved...</label>

                                <table class="table">
                                    <thead>
                                        <tr>
                                            <th class="col-md-2">NAME</th>
                                            <th class="col-md-8">CODE</th>
                                            <th class="col-md-2">
                                                <button type="button" class="btn btn-primary btn-s" ng-click="showAddMode()">
                                                    <span class="glyphicon glyphicon-plus"></span>
                                                </button>
                                            </th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <tr ng-repeat="obj in recordList">
                                            <td class="col-md-2">{{obj.NAME}}</td>
                                            <td class="col-md-8">{{obj.CODE}}</td>
                                            <td class="col-md-2">

                                                <button type="button" class="btn btn-warning btn-s" title="Edit" ng-click="showEditMode(obj)">
                                                    <span class="glyphicon glyphicon-pencil"></span>
                                                </button>

                                                <button ng-show="obj.STATE===0" type="button" title="Activate" class="btn btn-primary btn-s" ng-click="switchStateObject(obj, 1)">
                                                    <span class="glyphicon glyphicon-check"></span>
                                                </button>

                                                <button ng-show="obj.STATE===1" type="button" title="De-activate" class="btn btn-danger btn-s" ng-click="switchStateObject(obj, 0)">
                                                    <span class="glyphicon glyphicon-remove"></span>
                                                </button>
                                            </td>
                                        </tr>
                                    </tbody>
                                </table>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
            
        <%@include file="common/resoucelink_footer.jsp" %>
    </body>
</html>
