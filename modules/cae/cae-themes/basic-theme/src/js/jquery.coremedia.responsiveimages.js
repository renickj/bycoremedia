/**
 * Responsive Image Resizer Plugin
 *
 * Picks a suitable image from a given set of images regarding given dimensions and the maximum size needed.
 *
 * Version 1.4
 * Updated 06/11/2015
 *
 * Copyright (c) 2015 CoreMedia AG
 *
 * Usage: $(".cm-image--responsive").responsiveImage();
 *
 * Example
 * <img src="image3x1.jpg" class="cm-image--responsive" data-cm-responsive-image="{
 *    "3x1" : {"320": "image3x1_small.jpg", "640": "image_medium.jpg", "1024": "image_large.jpg"},
 *    "2x1" : {"200": "image2x1_small.jpg", "400": "image2x1_other.jpg"}}" />
 *
 */

/*! Responsive Image Resizer Plugin | Copyright (c) 2015 CoreMedia AG */
;(function ($) {
  "use strict";

  $.fn.responsiveImages = function () {
    return this.each(function () {

      var $image = $(this);

      if ($image.data("cm-responsive-image-state") === undefined) {
        $image.data("cm-responsive-image-state", "initialized");
        $image.on("load", function () {
          $image.trigger({
            type: "srcChanged",
            src: $image.attr("src"),
            maxWidth: $image.data("lastMaxWidth"),
            ratio: $image.data("lastRatio")
          });
        });
      }

      var responsiveImages = $image.data("cm-responsive-image");

      // only run if there is at least one aspect ratio defined
      if (typeof responsiveImages !== "undefined") {

        var $imageContainer = $(this).parent();
        var containerWidth = $imageContainer.width();
        var containerHeight = $imageContainer.height();
        if (!containerWidth || !containerHeight) {
          return; // image is not visible, do not touch
        }

        // detect best fitting aspect ratio for box
        var containerRatio = containerWidth / containerHeight;
        var fittingRatio = {
          name: undefined,
          difference: undefined,
          linksForWidth: []
        };
        var regexp = /^[a-zA-Z_]*(\d+)x(\d+)$/;
        for (var name in responsiveImages) {

          if (!responsiveImages.hasOwnProperty(name)) {
            continue;
          }

          var match = regexp.exec(name);
          if (match != null) {
            var ratioWidth = parseInt(match[1]);
            var ratioHeight = parseInt(match[2]);
            var candidateRatio = {
              name: name,
              difference: Math.abs(containerRatio - (ratioWidth / ratioHeight)),
              linksForWidth: responsiveImages[name]
            };

            if (typeof fittingRatio.name === "undefined"
                    || typeof fittingRatio.difference === "undefined"
                    || (fittingRatio.difference > candidateRatio.difference)) {
              fittingRatio = candidateRatio;
            }
          }
        }

        // only run if a valid ratio is defined
        if (typeof fittingRatio.name !== "undefined") {

          // find fitting link
          var fittingImage = {
            maxWidth: undefined,
            link: undefined
          };
          for (var maxWidth in fittingRatio.linksForWidth) {

            if (!fittingRatio.linksForWidth.hasOwnProperty(maxWidth)) {
              continue;
            }

            var candidateImage = {
              maxWidth: parseInt(maxWidth),
              link: fittingRatio.linksForWidth[maxWidth]
            };

            // calculate fitting image, allow no quality loss
            if (// case: no fitting image is set
            // -> take the candidate image
            typeof fittingImage.maxWidth === "undefined"
            || typeof fittingImage.link === "undefined"
              // case: fittingImage and candidate are smaller than the container
              // -> take candidate if the image is bigger (lesser quality loss)
            || (fittingImage.maxWidth < containerWidth
            && candidateImage.maxWidth < containerWidth
            && candidateImage.maxWidth > fittingImage.maxWidth)
              // case: fittingImage is smaller and candidate is bigger than the container
              // -> take candidate image (no quality loss is better than any quality loss)
            || (fittingImage.maxWidth < containerWidth
            && candidateImage.maxWidth >= containerWidth)
              // case: fittingImage and candidate are bigger than the container
              // -> take candidate if the image is smaller (no quality loss and smaller size)
            || (fittingImage.maxWidth >= containerWidth
            && candidateImage.maxWidth >= containerWidth
            && candidateImage.maxWidth < fittingImage.maxWidth)) {
              fittingImage = candidateImage;
            }
          }

          // @since 1.3
          // image can be an <img> tag
          if ($image.is("img")) {
            // replace link if not the same
            if (fittingImage.link !== $image.attr("src")) {
              coremedia.blueprint.logger.log("Change Responsive Image to aspect ratio: '" + fittingRatio.name + "' and maxWidth: '" + fittingImage.maxWidth + "'");
              $image.trigger({
                type: "srcChanging",
                src: $image.attr("src"),
                maxWidth: fittingImage.maxWidth,
                ratio: fittingRatio.name
              });
              $image.data("lastMaxWidth", fittingImage.maxWidth);
              $image.data("lastRatio", fittingRatio.name);
              $image.attr("src", fittingImage.link);
            }
            // or a background image via style attribute
          } else {
            // replace link if not the same
            if ("background-image: url('" + fittingImage.link + "');" !== $image.attr("style")) {
              coremedia.blueprint.logger.log("Change Responsive Background Image to aspect ratio: '" + fittingRatio.name + "' and maxWidth: '" + fittingImage.maxWidth + "'");
              $image.data("lastMaxWidth", fittingImage.maxWidth);
              $image.data("lastRatio", fittingRatio.name);
              $image.attr("style", "background-image: url('" + fittingImage.link + "');");
            }
          }
        }
      }
    });
  };
})(coremedia.blueprint.$ || jQuery);
