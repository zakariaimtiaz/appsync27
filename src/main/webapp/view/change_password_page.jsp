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

                        <div class="col-md-12"><h3>Change Password</h3></div>

                        <div class="col-md-12">
                            <form action="<c:url value='/UserInfo/change-password' />" method="POST">
                                <div class="form-group">
                                    <label for="currentPassword">Current Password</label>
                                    <input type="password" class="form-control" id="currentPassword" name="currentPassword" required>
                                </div>
                                <div class="form-group">
                                    <label for="newPassword">New Password</label>
                                    <input type="password" class="form-control" id="newPassword" name="newPassword" required>
                                </div>
                                <div class="form-group">
                                    <label for="confirmPassword">Confirm New Password</label>
                                    <input type="password" class="form-control" id="confirmPassword" name="confirmPassword" required>
                                </div>
                                <button type="submit" class="btn btn-primary">Change Password</button>
                            </form>
                                <div class="mt-3" style="padding-top: 10px;">
                                <!-- Display success or error message -->
                                <c:if test="${!empty changePasswordError}">
                                    <div class="alert alert-danger">${changePasswordError}</div>
                                </c:if>
                                <c:if test="${!empty changePasswordSuccess}">
                                    <div class="alert alert-success">${changePasswordSuccess}</div>
                                </c:if>
                            </div>
                        </div>

                    </div>
                </div>
            </div>
        </div>

        <%@include file="common/resoucelink_footer.jsp" %>
    </body>
</html>
