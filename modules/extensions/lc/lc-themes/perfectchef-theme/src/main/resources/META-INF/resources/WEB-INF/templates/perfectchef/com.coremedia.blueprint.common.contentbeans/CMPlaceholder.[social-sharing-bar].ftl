<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMPlaceholder" -->
<#-- @ftlvariable name="cmpage" type="com.coremedia.blueprint.common.contentbeans.Page" -->

<#if self.content?has_content>
  <#assign link=cm.getLink(cmpage.content, {"absolute":true}) />
</#if>

<div class="cm-social-share-bar cm-clearfix">
    <#-- Sharing -->
    <div class="cm-share">
        <h3 class="cm-share__title"><@bp.message key="Share_Label" /></h3>
        <#-- Facebook -->
        <a title="Facebook" class="cm-share__icon" href="https://www.facebook.com/share.php?u=${link}" onclick="javascript:window.open(this.href,
'', 'menubar=no,toolbar=no,resizable=yes,scrollbars=yes,height=600,width=600');return false;">
            <i class="icon-facebook-social"></i>
            <span class="cm-visuallyhidden">Facebook</span>
        </a>
        <#-- Twitter -->
        <a title="Twitter" class="cm-share__icon" href="https://twitter.com/home?status=${link}" onclick="javascript:window.open(this.href,
'', 'menubar=no,toolbar=no,resizable=yes,scrollbars=yes,height=600,width=600');return false;">
            <i class="icon-twitter-social"></i>
            <span class="cm-visuallyhidden">Twitter</span>
        </a>
        <#-- Google+ -->
        <a title="Google+" class="cm-share__icon" href="https://plus.google.com/share?url=${link?html}" onclick="javascript:window.open(this.href,
'', 'menubar=no,toolbar=no,resizable=yes,scrollbars=yes,height=600,width=600');return false;">
            <i class="icon-googleplus-social"></i>
            <span class="cm-visuallyhidden">Google+</span>
        </a>
        <#-- Pinterest -->
        <a title="Pinterest" class="cm-share__icon" href="http://pinterest.com/pin/create/button/?url=${link}" onclick="javascript:window.open(this.href,
'', 'menubar=no,toolbar=no,resizable=yes,scrollbars=yes,height=600,width=600');return false;">
            <i class="icon-pinterest-social"></i>
            <span class="cm-visuallyhidden">Pinterest</span>
        </a>
    </div>
</div>