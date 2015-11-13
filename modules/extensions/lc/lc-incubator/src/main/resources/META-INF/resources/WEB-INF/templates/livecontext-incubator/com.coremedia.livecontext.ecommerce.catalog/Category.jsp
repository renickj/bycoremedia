<%@ taglib prefix="cm" uri="http://www.coremedia.com/2004/objectserver-1.0-2.0" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html; charset=UTF-8" session="false" %>
<%@ include file="../../../includes/taglibs.jinc" %>
<%--@elvariable id="self" type="com.coremedia.livecontext.ecommerce.model.Category"--%>
<html>
    <head>
        <title>${self.name}</title>
    </head>
    <body>
        <h1>${self.name}</h1>

        <p>
            <b>id:</b> ${self.id}
        </p>
        <p>
            <b>External Id:</b> ${self.externalId}
        </p>
        <p>
            <b>locale:</b> ${self.locale}
        </p>
        <p>
            <b>name:</b> ${self.name}
        </p>
        <p>
            <b>shortDescription:</b> ${self.shortDescription}
        </p>
        <p>
            <b>thumbnail:</b> ${self.thumbnail}
        </p>

        <p>
          <b>breadCrumb:</b>
          <c:forEach items="${self.breadcrumb}" var="category">
            <cm:link var="link" target="${category}">
              <cm:param name="vanilla" value="true"/>
            </cm:link>
            <a href="${link}">${category.name} <c:if test="${!index.last}">/</c:if></a>
        </c:forEach>
        </p>
        <p>
          <b>Children:</b>
        </p>
        <p>
          <cm:include self="${self}" view="children">
            <cm:param name="depth" value="1"/>
          </cm:include>
        </p>
    </body>
</html>