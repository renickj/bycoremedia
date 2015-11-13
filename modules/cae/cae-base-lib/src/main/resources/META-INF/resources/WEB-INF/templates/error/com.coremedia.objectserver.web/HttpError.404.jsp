<%@ page contentType="text/html; charset=UTF-8" session="false"
%><%@ include file="../../../../WEB-INF/includes/taglibs.jinc"
%><%--@elvariable id="self" type="com.coremedia.objectserver.web.HttpError"--%>
<c:set var="message" value="The requested resource does not exist." scope="page"/>
<c:if test="${!empty self.errorMessage}">
  <c:set var="message" value="${self.errorMessage}" scope="page"/>
</c:if>
<bp:errorpage title="Resource not found" message="${message}"/>