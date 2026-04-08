<%-- 
    Document   : db_manager_index
    Created on : Dec 12, 2017, 10:21:54 AM
    Author     : imtiaz
--%>

<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" %>
<!DOCTYPE html>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html lang="en" ng-app="SyncApp">
    <head>
        <%@include file="common/resoucelink_head.jsp" %>
    </head>
    <body>
        <div id="wrapper" class="toggled">
            <%@include file="common/top_menu.jsp" %>
            <%@include file="common/left_menu.jsp" %>

            <div id="page-content-wrapper">
                <div class="container-fluid" ng-controller="SyncTableCrtl" ng-init="loadRecords()">

                    <div class="row">

                        <div class="col-md-12"><h3>{{NAME}}</h3></div>

                        <div class="col-md-12" ng-show="entryMode === 1">
                            <form>
                                <div class="input-group">
                                    <span class="input-group-addon">TYPE</span>
                                    <select class="form-control" ng-model="record.TBL_TYPE"
                                            ng-change="onModeChange(record.TBL_TYPE)" required="true">
                                        <option value="" disabled="disabled">SELECT TBL TYPE</option>
                                        <option value="M">MASTER</option>
                                        <option value="C">CLIENT</option>
                                        <option value="MC">MASTER-CLIENT</option>
                                    </select>
                                </div>
                                <c:if test="${IS_CLIENT}">
                                    <div class="input-group">
                                        <span class="input-group-addon">SERVER</span>
                                        <select class="form-control" ng-model="record.SERVER_ID">
                                            <option ng-value="0" disabled="disabled">SELECT SERVER</option>
                                            <option ng-repeat="obj in serverList" ng-value="obj.SERVER_ID">{{obj.SERVER_NAME}}
                                            </option>
                                        </select>
                                    </div>
                                </c:if>
                                <div class="input-group">
                                    <span class="input-group-addon">DATABASE</span>
                                    <select class="form-control" ng-model="record.DB_CONFIG_ID"
                                            ng-change="fetchTableNames(record.DB_CONFIG_ID)" required="true">
                                        <option ng-value="0" disabled="disabled">SELECT DATABASE</option>
                                        <option ng-repeat="obj in dbConfigList" ng-value="obj.DB_CONFIG_ID">
                                            {{obj.CONFIG_NAME}}
                                        </option>
                                    </select>
                                </div>
                                <div class="input-group" ng-if="!tableNamesAvailable">
                                    <span class="input-group-addon">NAME</span>
                                    <input type="text" class="form-control" ng-model="record.TBL_NAME" placeholder="name">
                                </div>

                                <div class="input-group" ng-if="tableNamesAvailable">
                                    <span class="input-group-addon">NAME</span>
                                    <select class="form-control" ng-model="record.TBL_NAME" required="true">
                                        <option value="" disabled="disabled" selected="selected">SELECT TABLE NAME</option>
                                        <option ng-repeat="table in tblNameList" ng-value="table">{{table}}</option>
                                    </select>
                                </div>
                                <div class="input-group">
                                    <span class="input-group-addon">CODE</span>
                                    <input type="text" class="form-control" ng-model="record.TBL_CODE" placeholder="code">
                                </div>
                                <div class="input-group">
                                    <span class="input-group-addon">PRIMARY KEY</span>
                                    <input type="text" class="form-control" ng-model="record.TBL_PRIMARY_COLUMN" ng-value=""
                                           placeholder="e.g id">
                                </div>
                                <c:if test="${IS_CLIENT}">
                                    <div class="input-group">
                                        <span class="input-group-addon">S TRACKING KEY</span>
                                        <input type="text" class="form-control" ng-model="record.TBL_S_TRACKING_COLUMN" ng-value=""
                                               placeholder="e.g version_no">
                                    </div>

                                    <div class="input-group">
                                        <span class="input-group-addon">S TRACKING SQL</span>
                                        <input type="text" class="form-control" ng-model="record.TBL_S_TRACKING_SQL" ng-value=""
                                               placeholder="e.g SELECT MAX(VERSION_NO) from user_info WHERE GROUP_ID = 1 AND HOME_URL IS NULL;">
                                    </div>

                                    <div ng-show="select_upsert == 1 || select_upsert == 3" class="input-group">
                                        <span class="input-group-addon">C TRACKING KEY</span>
                                        <input type="text" class="form-control" ng-model="record.TBL_C_TRACKING_COLUMN" ng-value=""
                                               placeholder="e.g sent_flag">
                                    </div>
                                </c:if>
                                <div ng-show="select_upsert == 1 || select_upsert == 3" class="input-group">
                                    <span class="input-group-addon">SELECT SQL</span>
                                    <textarea class="form-control" ng-model="record.SELECT_SQL" rows="6"></textarea>
                                </div>

                                <div ng-show="select_upsert == 2 || select_upsert == 3" class="input-group">
                                    <span class="input-group-addon">UPSERT SQL</span>
                                    <textarea class="form-control" ng-model="record.UPSERT_SQL" rows="6"></textarea>
                                </div>
                                <div class="input-group">
                                    <span class="input-group-addon">SYNC PRIORITY</span>
                                    <input type="text" class="form-control" ng-model="record.SYNC_PRIORITY" placeholder="101">
                                </div>
                                <div class="input-group">
                                    <span class="input-group-addon">CHUNK SIZE</span>
                                    <input type="text" class="form-control" ng-model="record.CHUNK_SIZE" placeholder="10">
                                </div>

                                <button class="btn btn-primary" ng-show="editMode === 0" ng-click="submitObject('NEW')"><i
                                        class="fa fa-check"></i> Save
                                </button>
                                <button class="btn btn-success" ng-show="editMode === 1" ng-click="submitObject('UPDATE')"><i
                                        class="fa fa-check"></i> Update
                                </button>

                                <button class="btn btn-danger" ng-show="editMode === 0" ng-click="clearForm()"><i
                                        class="fa fa-refresh"></i> Reset
                                </button>
                                &nbsp;&nbsp;&nbsp;&nbsp;<button class="btn btn-warning" ng-click="closeMode()"><i
                                        class="fa fa-close"></i> Close
                                </button>

                            </form>
                        </div>

                        <div class="col-md-12" ng-show="entryMode === 0">

                            <div class="table-responsive">
                                <label ng-show="showAlert === 1" class="label label-info">Data has been saved...</label>

                                <table class="table">
                                    <thead>
                                        <tr>
                                            <th width="10%">CODE</th>
                                            <th>NAME</th>
                                            <th width="10%" style="text-align: center;">TYPE</th>
                                            <th width="20%" style="text-align: center;">DATABASE</th>
                                                <c:if test="${IS_CLIENT}">
                                                <th width="20%" style="text-align: center;">SERVER</th>
                                                </c:if>
                                            <th width="10%" style="text-align: center;">PRIORITY</th>
                                            <th width="15%">
                                                <button type="button" class="btn btn-primary btn-s" ng-click="showAddMode()">
                                                    <span class="glyphicon glyphicon-plus"></span>
                                                </button>
                                            </th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <tr ng-repeat="obj in recordList">
                                            <td width="10%">{{obj.TBL_CODE}}</td>
                                            <td>{{obj.TBL_NAME}}</td>
                                            <td width="10%" style="text-align: center;">{{obj.TBL_TYPE}}</td>
                                            <td width="20%" style="text-align: center;">{{obj.CONFIG_NAME}}</td>
                                            <c:if test="${IS_CLIENT}">
                                                <td width="20%" style="text-align: center;">{{obj.SERVER_NAME}}</td>
                                            </c:if>
                                            <td width="10%" style="text-align: center;">{{obj.SYNC_PRIORITY}}</td>
                                            <td width="15%">
                                                <button type="button" class="btn btn-warning btn-s" title="Edit"
                                                        ng-click="showEditMode(obj)">
                                                    <span class="glyphicon glyphicon-pencil"></span>
                                                </button>

                                                <button ng-show="obj.STATE === 0" type="button" title="Activate"
                                                        class="btn btn-primary btn-s" ng-click="switchStateObject(obj, 1)">
                                                    <span class="glyphicon glyphicon-check"></span>
                                                </button>

                                                <button ng-show="obj.STATE === 1" type="button" title="De-activate"
                                                        class="btn btn-danger btn-s" ng-click="switchStateObject(obj, 0)">
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
