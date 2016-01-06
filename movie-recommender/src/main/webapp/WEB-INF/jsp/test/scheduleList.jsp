<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html>
<head>
    <title></title>
</head>
<body>
<h3>排期列表</h3>
<table>
    <%--<tr>--%>
    <%--<th></th>--%>
    <%--<th></th>--%>
    <%--<th></th>--%>
    <%--<th></th>--%>
    <%--<th></th>--%>
    <%--</tr>--%>
    <c:forEach items="${scheduleList}" var="item">
        <tr>
            <td>${item.baseScheduleId}</td>
            <%--<td><fmt:formatDate pattern="yyyy-MM-dd"--%>
                                <%--value="${item.showDate}"/></td>--%>
            <td>${item.showDate}</td>
            <td>${item.showTime}</td>
            <td>${item.movieName}</td>
            <td>${item.hallName}</td>
            <td>${item.actualSellPrice}</td>
            <td><a target="_blank"
                   href="${pageContext.request.contextPath}/test/seats?scheduleId=${item.baseScheduleId}&cinemaNo=${item.cinemaNo}&channelId=${item.tpId}">座位图</a>
            </td>
        </tr>
    </c:forEach>
</table>
</body>
</html>
