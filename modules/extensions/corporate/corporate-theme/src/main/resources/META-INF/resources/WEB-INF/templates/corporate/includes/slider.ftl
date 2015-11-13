<#ftl strip_whitespace=true>

<#-- responsive design slider information for studio -->
<#assign sliderMetadata={
  "cm_preferredWidth": 1280,
  "cm_responsiveDevices": {
    <#-- list of the devices.
    naming and icons see: BlueprintDeviceTypes.properties
    the default icons are in studio-core, but you can define
    your own style-classes in slider-icons.css.
    -->
    <#-- e.g. iphone6 -->
    "mobile_portrait": {
      "width": 375,
      "height": 667,
      "order": 1,
      "isDefault": true
    },
    "mobile_landscape": {
      "width": 667,
      "height": 375,
      "order": 2
    },
    <#-- e.g. ipad -->
    "tablet_portrait": {
      "width": 768,
      "height": 1024,
      "order": 3
    },
    "tablet_landscape": {
      "width": 1024,
      "height": 768,
      "order": 4
    }
  }
}/>