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
                <div class="container-fluid" ng-controller="DbManagerCrtl" ng-init="loadRecords()">

                    <div class="row">

                        <div class="col-md-12"><h3>{{NAME}}</h3></div>

                        <div class="col-md-12" ng-show="entryMode === 1">
                            <form>

                                <div class="input-group">
                                    <span class="input-group-addon">Name</span>
                                    <input id="configname" type="text" class="form-control" ng-model="record.CONFIG_NAME" placeholder="Name">
                                </div>
                                <div class="input-group">
                                    <span class="input-group-addon">DB TYPE</span>
                                    <select class="form-control" ng-model="record.DB_TYPE" required="true">
                                        <option value="" disabled="disabled">SELECT DB TYPE</option>
                                        <option ng-repeat="obj in typeList" value="{{obj}}">{{obj}}</option>
                                    </select>
                                </div>
                                <div class="input-group">
                                    <span class="input-group-addon">DB URL</span>
                                    <input id="dburl" type="text" class="form-control" ng-model="record.DB_URL" placeholder="jdbc:postgresql://localhost:5432/mydb?autoReconnect=true">
                                </div>
                                <div class="input-group">
                                    <span class="input-group-addon">Username</span>
                                    <input id="dbuser" type="text" class="form-control" ng-model="record.DB_USER_NAME" placeholder="root">
                                </div>
                                <div class="input-group">
                                    <span class="input-group-addon">Password</span>
                                    <input id="dbpasswd" type="password" class="form-control" ng-model="record.DB_PASSWORD" placeholder="12345678">
                                </div>
                                <div class="input-group">
                                    <span class="input-group-addon">Schema</span>
                                    <input id="dbschema" type="text" class="form-control" ng-model="record.SCHEMA" placeholder="public">
                                </div>
                                <!-- Save Button -->
                                <button class="btn btn-primary" 
                                        ng-show="editMode === 0" 
                                        ng-click="submitObject('NEW')" 
                                        ng-disabled="isProcessing">
                                    <span ng-if="!isProcessing"><i class="fa fa-check"></i> Save</span>
                                    <span ng-if="isProcessing"><i class="fa fa-spinner fa-spin"></i> Processing...</span>
                                </button>

                                <!-- Update Button -->
                                <button class="btn btn-success" 
                                        ng-show="editMode === 1" 
                                        ng-click="submitObject('UPDATE')" 
                                        ng-disabled="isProcessing">
                                    <span ng-if="!isProcessing"><i class="fa fa-check"></i> Update</span>
                                    <span ng-if="isProcessing"><i class="fa fa-spinner fa-spin"></i> Processing...</span>
                                </button>

                                <button class="btn btn-danger" ng-show="editMode === 0" ng-click="clearForm()"> <i class="fa fa-refresh"></i> Reset</button>
                                <button class="btn btn-warning" ng-click="closeMode()"> <i class="fa fa-close"></i> Close</button>

                                <!-- Test Connection Button (Demonstration) -->
                                <button class="btn btn-primary" ng-click="openTestConnectionModal()">
                                    <i class="fa fa-plug"></i> Test Connection
                                </button>
                            </form>
                        </div>

                        <div class="col-md-12" ng-show="entryMode === 0">
                            <!-- Success Alert (shown when save/update is successful) -->
                            <div ng-show="showAlert === 1" class="alert alert-success alert-dismissible fade in">
                                <button type="button" class="close" ng-click="hideAlert()">&times;</button>
                                <strong>Success!</strong> Operation completed successfully!
                            </div>

                            <!-- Error Alert (shown when there are errors) -->
                            <div ng-show="showError === 1" class="alert alert-danger alert-dismissible fade in">
                                <button type="button" class="close" ng-click="hideAlert()">&times;</button>
                                <strong>Error!</strong> {{errorMessage}}
                            </div>
                            <div class="table-responsive">

                                <table class="table">
                                    <thead>
                                        <tr>
                                            <th class="col-md-2" >Config name</th>
                                            <th class="col-md-5" >URL</th>
                                            <th class="col-md-2" >Schema</th>
                                            <th class="col-md-1" >DB Type</th>
                                            <th class="col-md-1" style="text-align: center;">Connection</th>
                                            <th class="col-md-2">
                                                <button type="button" class="btn btn-primary btn-s" ng-click="showAddMode()">
                                                    <span class="glyphicon glyphicon-plus"></span>
                                                </button>
                                            </th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <tr ng-repeat="obj in recordList">
                                            <td class="col-md-2" >{{obj.CONFIG_NAME}}</td>
                                            <td class="col-md-5" >{{obj.DB_URL}}</td>
                                            <th class="col-md-2" >{{obj.SCHEMA}}</th>
                                            <td class="col-md-1" >{{obj.DB_TYPE}}</td>
                                            <td class="col-md-1" style="text-align: center;">
                                                <i ng-if="obj.DB_CONNECTION_STATE === 0" 
                                                   class="fa fa-times-circle text-danger"></i>
                                                <i ng-if="obj.DB_CONNECTION_STATE === 1" 
                                                   class="fa fa-check-circle-o text-success"></i>
                                            </td>

                                            <td class="col-md-2">

                                                <button type="button" class="btn btn-warning btn-s" title="Edit" ng-click="showEditMode(obj)">
                                                    <span class="glyphicon glyphicon-pencil"></span>
                                                </button>

                                                <button ng-show="obj.STATE === 0" type="button" title="Activate" class="btn btn-primary btn-s" ng-click="switchStateObject(obj, 1)">
                                                    <span class="glyphicon glyphicon-check"></span>
                                                </button>

                                                <button ng-show="obj.STATE === 1" type="button" title="De-activate" class="btn btn-danger btn-s" ng-click="switchStateObject(obj, 0)">
                                                    <span class="glyphicon glyphicon-remove"></span>
                                                </button>
                                            </td>
                                        </tr>
                                    </tbody>
                                </table>
                            </div>
                        </div>
                    </div>

                    <!-- Test Connection Modal -->
                    <div class="modal fade" id="testConnectionModal" tabindex="-1" role="dialog" 
                         aria-hidden="true" data-backdrop="static" data-keyboard="false">
                        <div class="modal-dialog" role="document">
                            <div class="modal-content">
                                <div class="modal-header">
                                    <h5 class="modal-title">Test Database Connection</h5>
                                    <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                                        <span aria-hidden="true">&times;</span>
                                    </button>
                                </div>
                                <div class="modal-body">
                                    <!-- Modal Form Fields -->
                                    <div class="input-group">
                                        <span class="input-group-addon">DB URL</span>
                                        <input type="text" class="form-control" ng-model="testRecord.DB_URL" placeholder="">
                                    </div>
                                    <div class="input-group">
                                        <span class="input-group-addon">Username</span>
                                        <input type="text" class="form-control" ng-model="testRecord.DB_USER_NAME" placeholder="">
                                    </div>
                                    <div class="input-group">
                                        <span class="input-group-addon">Password</span>
                                        <input type="{{passwordFieldType}}" class="form-control" ng-model="testRecord.DB_PASSWORD" placeholder="">
                                    </div>
                                    <div class="input-group">
                                        <button class="btn btn-secondary" type="button" ng-click="togglePasswordVisibility()">Reset Password</button>
                                    </div>
                                </div>
                                <div class="modal-footer">
                                    <button class="btn btn-primary" ng-click="testConnectionInModal()" ng-disabled="isTesting">
                                        <i ng-if="!isTesting" class="fa fa-plug"></i>
                                        <i ng-if="isTesting" class="fa fa-spinner fa-spin"></i> Test
                                    </button>
                                    <button type="button" class="btn btn-secondary" data-dismiss="modal">Close</button>

                                    <button class="btn btn-info" ng-click="setPasswordToMainForm()">Set Password To Main Form & Close</button>

                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <%@include file="common/resoucelink_footer.jsp" %>
    </body>
</html>
