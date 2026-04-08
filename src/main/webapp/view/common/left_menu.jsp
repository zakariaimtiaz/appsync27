<%-- 
    Document   : top_menu
    Created on : Dec 12, 2017, 12:53:32 PM
    Author     : imtiaz
--%>

<%@ taglib uri = "http://java.sun.com/jsp/jstl/functions" prefix = "fn" %>

<div id="sidebar-wrapper">
    <ul class="sidebar-nav">
        <li class="sidebar-brand">
            <a href="#">
                MENU
            </a>
        </li>
        
        <li><a class="${fn:contains(CURL, '/AppProperties/readme')? 'selected' : ''}" href='<c:url value = "/AppProperties/readme"/>'>Read Me</a></li>
        <li><a class="${fn:contains(CURL, '/AppProperties/index')? 'selected' : ''}" href='<c:url value = "/AppProperties/index"/>'>App Configuration</a></li>
        <c:if test="${IS_SERVER || IS_CLIENT}">
            <c:if test="${!IS_SERVER}">
                <li><a class="${fn:contains(CURL, '/SvrManager/index')? 'selected' : ''}" href='<c:url value = "/SvrManager/index"/>'>Server Configuration</a></li>
            </c:if>
            <c:if test="${!IS_CLIENT}">
                <li><a class="${fn:contains(CURL, '/ClientManager/index')? 'selected' : ''}" href='<c:url value = "/ClientManager/index"/>'>Client Configuration</a></li>
            </c:if>

            <li><a class="${fn:contains(CURL, '/DbManager/index')? 'selected' : ''}" href='<c:url value = "/DbManager/index"/>'>Database Configuration</a></li>
            <li><a class="${fn:contains(CURL, '/SyncTable/index')? 'selected' : ''}" href='<c:url value = "/SyncTable/index"/>'>Table Configuration</a></li>
        </c:if>
        <li><a class="${fn:contains(CURL, '/AppProperties/logs')? 'selected' : ''}" href='<c:url value = "/AppProperties/logs"/>'>Server Logs</a></li>

    </ul>
</div>
