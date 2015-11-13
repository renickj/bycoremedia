<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cm" uri="http://www.coremedia.com/2004/objectserver-1.0-2.0" %>
<%@ page contentType="text/html; charset=UTF-8" session="false" %>
<%@ include file="../../../includes/taglibs.jinc" %>
<%--@elvariable id="self" type="com.coremedia.livecontext.ecommerce.model.Category"--%>
<%--@elvariable id="depth" type="java.lang.Integer"--%>
<%--<cm:include self="${self}" view="asLink"/>--%>

<c:set var="children" value="${self.children}"/>
<c:if test="${not empty children && depth > 0}">
  <ul>
    <c:forEach var="child" items="${children}">
      <cm:include self="${child}" view="asLink"/>
      <cm:include self="${child}" view="children">
        <cm:param name="depth" value="${depth - 1}"/>
      </cm:include>
    </c:forEach>
  </ul>
</c:if>