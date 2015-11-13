<%@ taglib prefix="cm" uri="http://www.coremedia.com/2004/objectserver-1.0-2.0" %>
<%@ page contentType="text/html; charset=UTF-8" session="false" %>
<%@ include file="../../../includes/taglibs.jinc" %>
<%--@elvariable id="self" type="com.coremedia.livecontext.ecommerce.model.Product"--%>
<cm:link var="link" target="${self}">
  <cm:param name="vanilla" value="true"/>
</cm:link>
<li><a href="${link}"><c:out value="${self.name} (${self.externalId})"/></a></li>