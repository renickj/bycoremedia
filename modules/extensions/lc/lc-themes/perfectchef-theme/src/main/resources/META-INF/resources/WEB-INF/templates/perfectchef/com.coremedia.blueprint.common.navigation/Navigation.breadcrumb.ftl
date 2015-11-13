<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.navigation.Navigation" -->
<#-- @ftlvariable name="navigation" type="com.coremedia.blueprint.common.navigation.Navigation" -->
<#-- @ftlvariable name="fragmentParameter" type="com.coremedia.livecontext.fragment.FragmentParameters" -->

<#if (self.navigationPathList?size > 1)>
<div class="row margin-true">
    <div class="col12" data-slot-id="1">
        <div id="widget_breadcrumb">
            <ul aria-label="breadcrumb navigation region">
              <#list self.navigationPathList![] as navigation>
                <#if navigation_has_next>
                  <#if navigation.isRoot()>
                      <li>
                          <a href="${fragmentParameter.getParameter()}">${navigation.title!""}</a>
                          <span class="divider" aria-hidden="true">\</span>
                      </li>
                  <#elseif !navigation.isHidden()>
                      <li>
                        <@cm.include self=navigation view="asLink" />
                          <span class="divider" aria-hidden="true">\</span>
                      </li>
                  </#if>
                <#else>
                    <li class="current">${self.title!""}</li>
                </#if>
              </#list>
            </ul>
        </div>
    </div>
</div>
</#if>