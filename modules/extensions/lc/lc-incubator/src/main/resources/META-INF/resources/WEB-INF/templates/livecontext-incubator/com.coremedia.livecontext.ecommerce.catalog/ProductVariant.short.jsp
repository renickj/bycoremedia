<%@ page import="com.coremedia.livecontext.ecommerce.model.AvailabilityInfo" %>
<%@ page import="com.coremedia.livecontext.ecommerce.model.ProductAttribute" %>
<%@ page import="com.coremedia.livecontext.ecommerce.model.ProductVariant" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ taglib prefix="cm" uri="http://www.coremedia.com/2004/objectserver-1.0-2.0" %>
<%@ page contentType="text/html; charset=UTF-8" session="false" %>
<%@ include file="../../../includes/taglibs.jinc" %>
<%--@elvariable id="self" type="com.coremedia.livecontext.ecommerce.model.ProductVariant"--%>
<%--@elvariable id="availabilityMap" type="java.util.Map"--%>
<cm:link var="link" target="${self}">
  <cm:param name="vanilla" value="true"/>
</cm:link>
<c:out value="${self.name}"/><%
    ProductVariant productVariant =  ((ProductVariant) request.getAttribute("self"));
    List<ProductAttribute> attributes = productVariant.getDefiningAttributes();
    out.println(" - ");
    for (int i = 0; i < attributes.size(); i++) {
        ProductAttribute attribute = attributes.get(i);
        out.println(attribute.getValue());
        if (i<attributes.size()-1) {
            out.println(", ");
        }
    }
    out.println(" (" + productVariant.getExternalId() + ")");

  Map<ProductVariant, AvailabilityInfo> availabilityMap =  ((Map<ProductVariant, AvailabilityInfo>) request.getAttribute("availabilityMap"));
  //TODO: out.println(" - in Stock: " + availabilityMap.get(productVariant));//.getQuantity()

%><a href="${link}">open</a>
