<%@ page language="java" %>

<a href="<%=request.getContextPath()%>/search?query=lsm7">search for LSM7</a> </br>

</br>

<a href="<%=request.getContextPath()%>/session?new=true">New session</a> </br>

</br>

<a href="<%=request.getContextPath()%>/session?attr=param1&size=4096">Store 4096 bytes under 'param1'</a> </br>
<a href="<%=request.getContextPath()%>/session?attr=param1&clear=true">clear 'param1'</a> </br>

</br>

<a href="<%=request.getContextPath()%>/session?attr=param1&size=1048576">Store 4096 bytes under 'param2'</a> </br>
<a href="<%=request.getContextPath()%>/session?attr=param1&clear=true">clear 'param2'</a> </br>
