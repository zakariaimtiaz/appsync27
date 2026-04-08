<%-- 
    Document   : top_menu
    Created on : Dec 12, 2017, 12:53:32 PM
    Author     : imtiaz
--%>

<nav class="navbar navbar-inverse" style="border-radius: 0px;">
    <div class="container-fluid">
        <div class="navbar-header">
            <a class="navbar-brand" href="#" id="menu-toggle">
                <i class="fa fa-bars"></i> App Sync as ${IS_CLIENT ? 'CLIENT':'SERVER'} 
            </a>
        </div>
        <ul class="nav navbar-nav navbar-right">
            <li class="navbar-text">
                
            </li>
            <li class="dropdown">
                <a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-expanded="false">
                    <i class="fa fa-user"></i>&nbsp;Welcome, ${USER_NAME} <span class="caret"></span>
                </a>
                <ul class="dropdown-menu" role="menu">
                    <li><a href="<c:url value='/UserInfo/change-password' />"><i class="fa fa-key"></i> Change Password</a></li>
                    <li><a href="<c:url value='/logout'/>"><i class="fa fa-sign-out"></i> Logout</a></li>
                </ul>
            </li>
        </ul>
    </div>
</nav>


