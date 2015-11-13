<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMLinkable" -->
<#-- @ftlvariable name="cmpage.content" type="com.coremedia.blueprint.common.contentbeans.CMChannel" -->
<#-- @ftlvariable name="navigation" type="com.coremedia.blueprint.common.navigation.Navigation" -->
<#-- @ftlvariable name="fragmentParameter" type="com.coremedia.livecontext.fragment.FragmentParameters" -->

<#if (cmpage.content.navigationPathList?size > 1)>
<div class="row margin-true">
    <div class="col12" data-slot-id="1">
        <div id="widget_breadcrumb">
            <ul aria-label="breadcrumb navigation region">
              <#list cmpage.content.navigationPathList![] as navigation>
                <#if navigation.isRoot()>
                    <li>
                        <a href="${fragmentParameter.getParameter()}">${navigation.title!""}</a>
                        <span class="divider" aria-hidden="true">\</span></li>
                    </li>
                <#elseif !navigation.isHidden()>
                    <li>
                      <@cm.include self=navigation view="asLink" />
                        <span class="divider" aria-hidden="true">\</span></li>
                    </li>
                </#if>
              </#list>
                <li class="current">${self.title!""}</li>
            </ul>
        </div>
    </div>
</div>
</#if>