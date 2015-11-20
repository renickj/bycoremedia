<#ftl strip_whitespace=true>

<#-- responsive design slider information for studio -->
<#assign sliderMetadata={
  "cm_preferredWidth": 1281,
  "cm_responsiveDevices": {
    <#-- list of the devices.
    naming and icons see: BlueprintDeviceTypes.properties
    the default icons are in studio-core, but you can define
    your own style-classes in slider-icons.css.
    -->
    <#-- e.g. iphone4 -->
    "mobile_portrait": {
      "width": 320,
      "height": 480,
      "order": 1,
      "isDefault": true
    },
    <#-- e.g. iphone4 -->
    "mobile_landscape": {
      "width": 480,
      "height": 320,
      "order": 2
    },
    <#-- e.g. nexus7 -->
    "tablet_portrait": {
      "width": 600,
      "height": 800,
      "order": 3
    },
    <#-- e.g. nexus7 -->
    "tablet_landscape": {
      "width": 960,
      "height": 540,
      "order": 4
    },
    <#-- e.g. ipad -->
    "hybrid_app_portrait": {
      "width": 768,
      "height": 1024,
      "order": 5
    },
    <#-- e.g. ipad -->
    "hybrid_app_landscape": {
      "width": 1024,
      "height": 768,
      "order": 6
    }
  }
}/>