<%@ page contentType="text/html; charset=UTF-8" session="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--@elvariable id="self" type="com.coremedia.blueprint.optimizely.OptimizelyPageAspect"--%>
<c:if test="${self.enabled}">
  <%-- Optimizely snippet must be the first JavaScript file loaded --%>
  <script type="text/javascript" language="JavaScript" src="//cdn.optimizely.com/js/${self.optimizelyId}.js"></script>
</c:if>
