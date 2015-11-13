<%@ page contentType="text/html; charset=UTF-8" session="false" %>
<%@ include file="../../../includes/taglibs.jinc" %>
<%--@elvariable id="self" type="com.coremedia.livecontext.ecommerce.model.ProductVariant"--%>
<html>
    <head>
        <title>${self.title}</title>
        <meta name="description" content="${self.metaDescription}"/>
        <meta name="keywords" content="${self.metaKeywords}"/>
    </head>
    <body>
        <h1>ProductVariant: ${self.title}</h1>

        <p>
            <b>ExternalId:</b> ${self.externalId}
        </p>
        <p>
            <b>Name:</b> ${self.name}
        </p>
        <p>
            <b>Short description:</b> ${self.shortDescription}
        </p>
        <p>
          <img alt="${self.defaultImageAlt}" src="${self.defaultImageUrl}"/>
        </p>
        <p>
            <b>Long description:</b> ${self.longDescription}
        </p>
        <p>
            <b>Meta keywords:</b> ${self.metaKeywords}
        </p>
        <p>
            <b>Meta description:</b> ${self.metaDescription}
        </p>
        <p>
            <b>List Price:</b> ${self.listPrice}
        </p>
        <p>
            <b>Offer Price:</b> ${self.offerPrice}
        </p>
        <p>
            <b>Availability enabled:</b> ${displayAvailability}
        </p>

        <c:if test="${displayAvailability && self.availabilityInfo != null}">
          <p>
            <b>Availability:</b> Status: ${self.availabilityInfo.inventoryStatus}, Count: ${self.availabilityInfo.quantity}
          </p>
        </c:if>
        <p>
            <b>Category breadcrumb:</b>
          <c:forEach items="${self.category.breadcrumb}" var="category" varStatus="index">
            <cm:link var="link" target="${category}">
              <cm:param name="vanilla" value="true"/>
            </cm:link>
             <a href="${link}">${category.name} <c:if test="${!index.last}">/</c:if></a>
          </c:forEach>
        </p>
        <p>
            <b>SEO Segment:</b> ${self.seoSegment}
        </p>
        <cm:link var="parentLink" target="${self.parent}">
          <cm:param name="vanilla" value="true"/>
        </cm:link>

        </br></br></br>
        <h1>Properies inherited from the <a href="${parentLink}">Parent Product:</a></h1>

        <cm:include self="${self}" view="variants"/>

    </body>
</html>