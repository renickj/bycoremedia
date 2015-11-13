<%@ tag body-content="scriptless" %>
<%@ attribute name="title" required="true" description="Title of the error page" %>
<%@ attribute name="message" required="true" description="Optional message to replace the default message" %>
<%@ attribute name="redirectUrl" required="false" description="Optional redirection URL, must be HTML attribute escaped." %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
  <c:if test="${!empty redirectUrl}">
    <meta http-equiv="Refresh" content="5; URL=${redirectUrl}">
  </c:if>
  <title><c:out value="${title}"/></title>
  <link rel='stylesheet' type='text/css' href='<c:url value="/css/errorpage.css"/>'/>
</head>
<body>
<table class="header">
  <tr>
    <td class="CMTitle CMHFill"><span class="large"><c:out value="${title}"/></span></td>
  </tr>
</table>
<div class="body">
  <p style="font-weight:bold;"><c:out value="${message}"/></p>
</div>
<table class="footer">
  <tr>
    <td class="CMHFill">&nbsp;</td>
  </tr>
</table>

<jsp:doBody/>

</body>
</html>
