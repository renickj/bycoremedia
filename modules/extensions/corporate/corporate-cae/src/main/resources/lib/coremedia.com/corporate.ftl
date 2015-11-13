
<#--
 * get offset for given element for bootstrap grid
 *
 * @param index Integer
 * @param numberOfItems Integer
 * @param itemsPerRow Integer
 * @param prefix (optional) String, put in front of offset class
 * @param force (optional) Boolean, force offset class, even if it's zero
-->
<#function getBootstrapOffsetClass index numberOfItems itemsPerRow  prefix="" force=false>
  <#-- bootstrap default grid = 12 rows -->
  <#assign width=12/itemsPerRow />
  <#assign isFirstItemOfRow=(index % itemsPerRow == 0) />
  <#assign isLastRow=(numberOfItems - numberOfItems % itemsPerRow - index) <= 0 />
  <#-- define offset class to align items in rows containing less than 3 items centered -->
  <#assign offsetClass="" />
  <#-- offset only applies to first element of last row -->
  <#if (isLastRow && isFirstItemOfRow)>
    <#-- offset depends on the number of items in the last row -->
    <#assign offsetValue=(12-((numberOfItems % itemsPerRow)*width))/2 />
    <#assign offsetClass="${prefix}offset-${offsetValue}" />
  <#elseif (force)>
    <#assign offsetClass="${prefix}offset-0" />
  </#if>

  <#return offsetClass>
</#function>

<#--
 * render closing and opening div for bootstrap grid
 *
 * @param index Integer
 * @param itemsPerRow Integer
 * @param additionalClass (optional) String
-->
<#macro renderNewBootstrapRow index itemsPerRow additionalClass="">
  <#-- bootstrap default grid = 12 rows -->
  <#assign width=12/itemsPerRow />
  <#assign isFirstItemOfRow=(index % itemsPerRow == 0) />
  <#-- offset only applies to first element of last row -->
  <#if (isFirstItemOfRow && index != 0)>
    </div>
    <div class="${additionalClass}row">
  </#if>
</#macro>
