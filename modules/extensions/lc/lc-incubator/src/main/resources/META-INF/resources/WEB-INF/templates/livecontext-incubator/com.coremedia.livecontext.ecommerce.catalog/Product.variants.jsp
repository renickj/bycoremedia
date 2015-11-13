<%@ page import="com.coremedia.livecontext.ecommerce.model.AxisFilter" %>
<%@ page import="com.coremedia.livecontext.ecommerce.model.Product" %>
<%@ page import="com.coremedia.livecontext.ecommerce.model.ProductAttribute" %>
<%@ page import="com.coremedia.livecontext.ecommerce.model.ProductVariant" %>
<%@ page import="com.coremedia.livecontext.ecommerce.model.VariantFilter" %>
<%@ page import="java.util.List" %>
<%@ page contentType="text/html; charset=UTF-8" session="false" %>
<%@ include file="../../../includes/taglibs.jinc" %>
<%--@elvariable id="self" type="com.coremedia.livecontext.ecommerce.model.Product"--%>
<%--@elvariable id="availabilityMap" type="java.util.Map"--%>
<p>
    <b>All Product Variants:</b>
    <ul>
    <c:forEach items="${self.variants}" var="variant">
        <li><cm:include self="${variant}" view="short">
          <cm:param name="availabilityMap" value="${availabilityMap}"/>
        </cm:include></li>
    </c:forEach>
    </ul>
</p>
    <%
        Product product =  ((Product) request.getAttribute("self"));
        List<String> axis = product.getVariantAxis();
        out.println("avialable axis (<b>getVariantAxis()</b>): " + axis);
        for (String ax : axis) {
            out.println("</p>");
            List<Object> availableValues = product.getAttributeValues(ax, (VariantFilter) null);
            out.println("available values for axis '" + ax + "' (<b>getAttributeValues(\""+ax+"\", null)</b>): " + availableValues);
            out.println("<ul>");
            for (Object value : availableValues) {
                List<ProductVariant> productVariants = product.getVariants(new AxisFilter(ax, value));
                out.println("<li>available variants for '"+ax+"' '" + value + "' (<b>getVariants(new AxisFilter(\""+ax+"\", \""+value+"\"))</b>): ");
                out.println("[");
                for (int i = 0; i < productVariants.size(); i++) {
                    ProductVariant productVariant = productVariants.get(i);
                    List<ProductAttribute> attributes = productVariant.getDefiningAttributes();
                    for (ProductAttribute attribute : attributes) {
                        if (!ax.equals(attribute.getType())) {
                            out.println(attribute.getValue() + " (" + productVariant.getExternalId() + ")");
                        }
                    }
                    if (i < productVariants.size()-1) {
                        out.println(", ");
                    }
                }
                out.println("]");
                out.println("</li>");
            }
            out.println("</ul>");
        }
    %>
