<@cm.responseHeader name="Content-Type" value="text/html; charset=UTF-8"/>
<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.Page" -->

<!DOCTYPE html>
<!--[if lte IE 8]> <html class="no-js cm-ie cm-lt-ie9" lang="${self.locale.language!'en'}" dir="${self.direction!'ltr'}" <@cm.metadata data=bp.getPageMetadata(self)!"" />> <![endif]-->
<!--[if IE 9]> <html class="no-js cm-ie cm-ie9" lang="${self.locale.language!'en'}" dir="${self.direction!'ltr'}" <@cm.metadata data=bp.getPageMetadata(self)!"" />> <![endif]-->
<!--[if (gt IE 9)|!(IE)]><!--> <html class="no-js" lang="${self.locale.language!'en'}" dir="${self.direction!'ltr'}" <@cm.metadata data=bp.getPageMetadata(self)!"" />> <!--<![endif]-->
<@cm.include self=self view="head"/>

<@cm.include self=self view="body"/>

</html>
