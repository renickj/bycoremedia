<%@ taglib prefix="cm" uri="http://www.coremedia.com/2004/objectserver-1.0-2.0" %>
<%@ page contentType="text/html; charset=UTF-8" session="false" %>
<%@ include file="../../../includes/taglibs.jinc" %>
<%--@elvariable id="self" type="java.util.List"--%>
<%--@elvariable id="depth" type="java.lang.Integer"--%>
<%--@elvariable id="item" type="com.coremedia.livecontext.ecommerce.model.Category"--%>
<html>
<head>
  <title>Categories</title>
</head>
<body>
<h1>Categories</h1>
  <ul>
    <c:forEach var="item" items="${self}">
      <cm:include self="${item}" view="asLink"/>
      <cm:include self="${item}" view="children">
        <cm:param name="depth" value="${depth - 1}"/>
      </cm:include>
    </c:forEach>
  </ul>
</body>
</html>