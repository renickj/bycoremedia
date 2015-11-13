<#-- @ftlvariable name="self" type="com.coremedia.livecontext.ecommerce.p13n.MarketingImage" -->

<li class="cm-collection__item">
    <div class="cm-accordion-item">
        <div class="cm-accordion-item__header"><i class="icon-arrow-right"></i><i class="icon-arrow-down"></i><span>${(self.name)!""}</span></div>
        <div class="cm-accordion-item__content"><@cm.include self=self /></div>
    </div>
</li>
