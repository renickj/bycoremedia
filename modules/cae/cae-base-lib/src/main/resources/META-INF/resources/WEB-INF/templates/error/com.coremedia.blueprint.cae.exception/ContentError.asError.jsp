<%@ page contentType="text/html; charset=UTF-8" session="false" %>
<%@ include file="../../../../WEB-INF/includes/taglibs.jinc" %>
<%--@elvariable id="self" type="com.coremedia.blueprint.cae.exception.ContentError"--%>
<bp:errorpage title="Content Error" message="${self.message}">
  <c:if test="${not empty self.wrappedException}">
    <!-- stacktrace
    <ul>
    <c:forEach items="${self.wrappedException.stackTrace}" var="message">
      <li>${message}</li>
    </c:forEach>
    </ul>
    -->
    <!-- root cause:
    <c:if test="${not empty self.wrappedException.cause.message}">
      message=${self.wrappedException.cause.message}
    </c:if>
    -->
  </c:if>
</bp:errorpage>
