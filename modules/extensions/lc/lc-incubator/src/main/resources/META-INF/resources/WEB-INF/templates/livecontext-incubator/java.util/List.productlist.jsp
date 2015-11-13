<%@ taglib prefix="cm" uri="http://www.coremedia.com/2004/objectserver-1.0-2.0" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html; charset=UTF-8" session="false" %>
<%@ include file="../../../includes/taglibs.jinc" %>
<%--@elvariable id="self" type="java.util.List"--%>
<%--@elvariable id="item" type="com.coremedia.livecontext.ecommerce.model.Product"--%>
<c:url value="/servlet/productsearch" var="productSearchUrl"/>
<html>
<head>
  <title>Product Search</title>
</head>
<body>
<h1>Product Search</h1>

<form method="post" action="${productSearchUrl}">
  <input name="searchTerm" type="text"/>
  <select name="searchType">
    <option value="Products">Products</option>
    <option value="ProductVariants">ProductVariants</option>
  </select>
  <select name="locale">
    <option value="en">en</option>
    <option value="fr">fr</option>
  </select>
  <input type="submit"/>
</form>

<c:if test="${not empty self}">
  <h2>Search results (${fn:length(self)})</h2>
  <ul>
    <c:forEach var="item" items="${self}">
      <cm:link var="link" target="${item}">
        <cm:param name="vanilla" value="true"/>
      </cm:link>
      <li><a href="${link}"><c:out value="${item.name} (${item.externalId})"/></a></li>
    </c:forEach>
  </ul>
</c:if>

</body>
</html>