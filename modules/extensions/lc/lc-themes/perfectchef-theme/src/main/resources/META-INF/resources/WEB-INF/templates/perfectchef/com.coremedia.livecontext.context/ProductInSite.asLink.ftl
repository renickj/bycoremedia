<#-- @ftlvariable name="self" type="com.coremedia.livecontext.context.ProductInSite" -->

<@bp.optionalLink href="${cm.getLink(self)}">${(self.product.name)!""}</@bp.optionalLink>