<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<html lang="en" ng-app="SyncApp">
    <head>
        <%@include file="common/resoucelink_head.jsp" %>
        <link href="<c:url value="/resources/css/login.css" />" rel="stylesheet">
        <title>${PAGE_TITLE}</title>
    </head>
    <body>
        <div class="container-fluid">
            <div class="form-container">
                <img src="<c:url value='/resources/images/appSync27.png'/>" alt="Avatar">
                <form action="<c:url value='/try-login' />" method="POST">
                    <label><b>Username</b></label>
                    <input type="text" placeholder="Enter Username" name="username" required>
                    <label><b>Password</b></label>
                    <input type="password" placeholder="Enter Password" name="password" required>
                    <button type="submit">Login</button>

                    <!-- Display error message if present -->
                    <c:if test="${not empty loginError}">
                        <div class="alert alert-danger" role="alert">
                            ${loginError}
                        </div>
                    </c:if>
                </form>
                <div class="form-footer">
                    <div class="version">Version: ${APPLICATION_VERSION}</div> <!-- Add version number -->
                </div>
            </div>
        </div>

        <%@include file="common/resoucelink_footer.jsp" %>
    </body>
</html>
