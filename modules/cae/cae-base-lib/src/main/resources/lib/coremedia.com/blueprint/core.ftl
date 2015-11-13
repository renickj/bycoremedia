<#ftl strip_whitespace=true>
<#-- @ftlvariable name="blueprintFreemarkerFacade" type="com.coremedia.blueprint.cae.web.taglib.BlueprintFreemarkerFacade" -->
<#-- @ftlvariable name="cmFacade" type="com.coremedia.objectserver.view.freemarker.CAEFreemarkerFacade" -->
<#-- @ftlvariable name="cmpage" type="com.coremedia.blueprint.common.contentbeans.Page" -->
<#-- @ftlvariable name="settingsService" type="com.coremedia.blueprint.base.settings.SettingsService" -->

<#-- createBeanFor -->
<#function createBeanFor content>
  <#return blueprintFreemarkerFacade.createBeanFor(content)>
</#function>

<#-- createBeansFor -->
<#function createBeansFor contents>
  <#return blueprintFreemarkerFacade.createBeansFor(contents)>
</#function>

<#-- FindNavigationContext -->
<#function findNavigationContext bean>
  <#return blueprintFreemarkerFacade.findNavigationContext(bean)>
</#function>

<#function isActiveNavigation navigation navigationPathList>
    <#return blueprintFreemarkerFacade.isActiveNavigation(navigation, navigationPathList)>
</#function>

<#-- SettingsFunction -->
<#function setting self key default=cm.UNDEFINED>
  <#return blueprintFreemarkerFacade.setting(self, key, default)>
</#function>

<#-- GenerateUniqueId -->
<#function generateId prefix="">
  <#return blueprintFreemarkerFacade.generateId(prefix)>
</#function>

<#-- CssClassFor -->
<#function cssClassFor itemHasNext index createCssClassAttribute>
  <#return blueprintFreemarkerFacade.cssClassFor(itemHasNext, index, createCssClassAttribute)>
</#function>
<#function cssClassForFirstLast itemHasNext index createCssClassAttribute>
  <#return blueprintFreemarkerFacade.cssClassForFirstLast(itemHasNext, index, createCssClassAttribute)>
</#function>
<#function cssClassForOddEven itemHasNext index createCssClassAttribute>
  <#return blueprintFreemarkerFacade.cssClassForOddEven(itemHasNext, index, createCssClassAttribute)>
</#function>
<#function cssClassAppendNavigationActive currentCssClass appendix navigation navigationPathList>
  <#return blueprintFreemarkerFacade.cssClassAppendNavigationActive(currentCssClass, appendix, navigation, navigationPathList)>
</#function>

<#-- Truncate Text -->
<#function truncateText text maxLength>
  <#return blueprintFreemarkerFacade.truncateText(text, maxLength)>
</#function>

<#-- Truncate Text and closes last opened but not closed <b> tag -->
<#function truncateHighlightedText text maxLength>
  <#return blueprintFreemarkerFacade.truncateHighlightedText(text, maxLength)>
</#function>

<#-- deprecated, use isEmptyRichtext instead -->
<#function isEmptyMarkup markup>
  <#return blueprintFreemarkerFacade.isEmptyRichtext(markup)>
</#function>
<#-- Check if Richtext is empty -->
<#function isEmptyRichtext richtext>
  <#return blueprintFreemarkerFacade.isEmptyRichtext(richtext)>
</#function>

<#-- Get filtered related from teaser -->
<#function filterRelated related filter>
  <#return blueprintFreemarkerFacade.filterRelated(related, filter)>
</#function>

<#-- Blob functions -->
<#function blobLink blob view>
  <#return blueprintFreemarkerFacade.blobLink(blob, view)>
</#function>

<#-- Image functions -->
<#function responsiveImageLinksData picture aspectRatios=[]>
  <#return blueprintFreemarkerFacade.responsiveImageLinksData(picture, cmpage, aspectRatios)>
</#function>

<#function uncroppedImageLink picture>
  <#return blueprintFreemarkerFacade.uncroppedImageLink(picture)>
</#function>

<#--
 * Return list of area configurations with the 'coords' attribute being transformed according to the image map's
 * picture transformations. If cropping is disabled, an empty list is returned.
 *
 * @param imageMap CMImageMap to retrieve areas from
 * @param limitAspectRatios List of aspect ratios to be calculated. If empty, all aspect ratios will be calculated
 -->
<#function responsiveImageMapAreas imageMap limitAspectRatios=[]>
  <#return blueprintFreemarkerFacade.responsiveImageMapAreas(imageMap, limitAspectRatios)>
</#function>

<#--
 * Returns Map containing information to be rendered as data attribute delivering informationen about the ImageMap
 * areas to JavaScript.
 *
 * @param coords map of transformation => points key/value pairs
 -->
<#function responsiveImageMapAreaData coords>
  <#return blueprintFreemarkerFacade.responsiveImageMapAreaData(coords) />
</#function>

<#-- deprecated -->
<#function substitute id original>
  <#return cmFacade.substitute(id, original)>
</#function>

<#-- ErrorReporterHelper -->
<#function getStackTraceAsString exception>
  <#return blueprintFreemarkerFacade.getStackTraceAsString(exception)>
</#function>

<#function id id>
  <#return blueprintFreemarkerFacade.parseContentId(id)>
</#function>

<#function getPageMetadata page>
  <#return blueprintFreemarkerFacade.getPageContext(page).content />
</#function>

<#function getPlacementPropertyName placement>
  <#return blueprintFreemarkerFacade.getPlacementPropertyName(placement) />
</#function>

<#--
 * Returns the metadata that was determined for the container, either as list or as plain object
 *
 * @param container The container the metadata should be determined for
 -->
<#function getContainerMetadata container>
  <#return blueprintFreemarkerFacade.getContainerMetadata(container) />
</#function>

<#--
 * Utility function to allow rendering of containers with custom items, e.g. partial containers with a subset of
 * the items the original container had.
 *
 * @param items The items to be put inside the new container
 * @return a new container
 -->
<#function getContainer items=[]>
  <#return blueprintFreemarkerFacade.getContainer(items) />
</#function>

<#--
 * Utility function to allow rendering of containers with custom items, e.g. partial containers with a subset of
 * the items the original container had.
 *
 * @param baseContainer The base container the new container shall be created from
 * @param items The items to be put inside the new container
 -->
<#function getContainerFromBase baseContainer items=[]>
  <#return blueprintFreemarkerFacade.getContainer(baseContainer, items) />
</#function>

<#function isWebflowRequest>
  <#return blueprintFreemarkerFacade.isWebflowRequest()!false>
</#function>

<#function getDisplaySize size>
  <#return blueprintFreemarkerFacade.getDisplaySize(size) />
</#function>

<#function getPlacementByName name page>
  <#return blueprintFreemarkerFacade.getPlacementByName(name, page) />
</#function>

<#--
 * Localization
 -->
<#function localizedString bundle key languageTag>
  <#return blueprintFreemarkerFacade.getLocalizedString(bundle, key, languageTag) />
</#function>

<#assign viewHookEventNames=blueprintFreemarkerFacade.getViewHookEventNames()/>

<#--
 * The width all image transformations are based on.
 -->
<#assign IMAGE_TRANSFORMATION_BASE_WIDTH=blueprintFreemarkerFacade.imageTransformationBaseWidth />
