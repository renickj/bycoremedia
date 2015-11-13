package com.coremedia.livecontext.asset.util;

import com.coremedia.blueprint.base.util.StructUtil;
import com.coremedia.cap.common.NoSuchPropertyDescriptorException;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentRepository;
import com.coremedia.cap.struct.Struct;
import com.coremedia.cap.struct.StructBuilder;
import com.coremedia.cap.struct.StructService;

import java.util.Collections;
import java.util.List;

/**
 * Helper for common livecontext asset operations.
 */
public class AssetHelper {

  public static final String STRUCT_PROPERTY_NAME = "localSettings";
  public static final String COMMERCE_SUBSTRUCT_NAME = "commerce";
  public static final String INHERIT_NAME = "inherit";
  public static final String ORIGIN_PRODUCT_LIST_NAME = "originProducts";
  public static final String PRODUCT_LIST_NAME = "products";

  /**
   * Update the picture document with a new list of product ids. That means the picture will be assigned
   * to each product as product picture. It handles all kinds of conflicts and corner cases when an update is
   * coming in. The following cases will be handled:
   * <pre>
   *           OLD STATE                                    NEW XMP DATA   RESULT STATE
   *
   * case  5:  null / []                                    null / []      No commerce struct
   * case  6:  null / []                                    [A, B]         inherit:TRUE, ori: [A, B], new: [A, B]
   * case  7:  inherit:TRUE, ori: [A, B], new: [A, B]       [A, C, D]      inherit:TRUE, ori: [A, C, D], new: [A, C, D]
   * case  8:  inherit:TRUE, ori: [A, B], new: [A, B]       null / []      No commerce struct
   * case  9:  inherit:FALSE, ori: [A, B], new: [A, C, D]   [E, F]         inherit:FALSE, ori: [E, F], new: [A, C, D]
   * case 10:  inherit:FALSE, ori: [A, B], new: [A, C, D]   null / []      inherit:FALSE, ori: [], new: [A, C, D]
   * case 11:  inherit:FALSE, ori: [A, B], new: []          [E, F]         inherit:TRUE, ori: [E, F], new: [E, F]
   * case 12:  inherit:FALSE, ori: [], new: [A, C, D]       [E, F]         inherit:FALSE, ori: [E, F], new: [A, C, D]
   * case 13:  new: [A, C, D]                               [E, F]         inherit:FALSE, ori: [E, F], new: [A, C, D]
   * case 14:  inherit:FALSE, ori: [A, B], new: []          []             inherit:FALSE, ori: [], new: []
   * case 15:  inherit:FALSE, ori: [], new: []              [A, B]         inherit:FALSE, ori: [A, B], new: [A, B]
   *</pre>
   *
   * Case 15 is same as case 6 but with an empty commerce struct.
   *
   * @param content the picture document
   * @param newProductIds the list of product ids that are to be assigned
   * @return the struct property that contains the updated commerce struct
   */
  public static Struct updateCMPictureForExternalIds(Content content, List<String> newProductIds, ContentRepository contentRepository) {
    // load/create localSettins struct
    ContentRepository repository = content == null ? contentRepository : content.getRepository();
    Struct struct = content == null ? null : content.getStruct(STRUCT_PROPERTY_NAME);
    Struct resultStruct = getEmptyStruct(repository);

    if (struct == null && newProductIds.size() > 0) {
      struct = getEmptyStruct(repository);
      resultStruct = updateStruct(struct, true, newProductIds, newProductIds, repository);
      return resultStruct;
    } else if (struct == null) {
      // case 3 and 5 (struct empty and externalIds empty)
      // do nothing --> return empty struct
      return resultStruct;
    }

    Struct commerceStruct = StructUtil.getSubstruct(struct, COMMERCE_SUBSTRUCT_NAME);
    if(commerceStruct == null || commerceStruct.getProperties().isEmpty()) {
      if (!newProductIds.isEmpty()) {
        // case 4 and 6
        // upload with first time XMP data
        resultStruct = updateStruct(struct, true, newProductIds, newProductIds, repository);
      }
    } else {
      // upload with existing struct
      List<String> oldProductsIds = StructUtil.getStrings(commerceStruct, PRODUCT_LIST_NAME);
      List<String> oldOriginProductIds = StructUtil.getStrings(commerceStruct, ORIGIN_PRODUCT_LIST_NAME);
      boolean inherit = StructUtil.getBoolean(commerceStruct, INHERIT_NAME);

      if (inherit) {
        // case 7-8 --> inherit = TRUE
        if (newProductIds.isEmpty()) {
          // case 8
          resultStruct = removeCommerceSubstruct(struct);
        } else {
          // case 7
          resultStruct = updateStruct(struct, true, newProductIds, newProductIds, repository);
        }
      } else {
        // inherit=FALSE && originProducts = []
        if (oldOriginProductIds.isEmpty()) {
          if (oldProductsIds.isEmpty()) {
            // case 15
            resultStruct = updateStruct(struct, true, newProductIds, newProductIds, repository);
          } else {
            // case 13
            resultStruct = updateStruct(struct, false, newProductIds, oldProductsIds, repository);
          }
        } else if (oldProductsIds.isEmpty()) {
          if (newProductIds.isEmpty()) {
            // case 14
            resultStruct = updateStruct(struct, false, newProductIds, newProductIds, repository);
          } else {
            // case 11
            resultStruct = updateStruct(struct, true, newProductIds, newProductIds, repository);
          }
        } else {
          // case 9-10,12
          resultStruct = updateStruct(struct, false, newProductIds, oldProductsIds, repository);
        }
      }
    }
    return resultStruct;
  }

  private static Struct updateStruct(Struct struct, Boolean inherit, List<String> originProductsIds, List<String> productIds, ContentRepository contentRepository) {
    Struct commerceStruct = getEmptyStruct(contentRepository);
    StructBuilder commerceStructBuilder = commerceStruct.builder();
    StructBuilder structBuilder;
    Struct newStruct = struct;

    if (StructUtil.getSubstruct(newStruct, COMMERCE_SUBSTRUCT_NAME) != null) {
      structBuilder = newStruct.builder().remove(COMMERCE_SUBSTRUCT_NAME); // step 1 of clear struct
      newStruct = structBuilder.build();// step 2 of clear struct
    }
    // check what if productIds = null
    commerceStructBuilder = commerceStructBuilder.declareBoolean(INHERIT_NAME, inherit);
    commerceStructBuilder = commerceStructBuilder.declareStrings(ORIGIN_PRODUCT_LIST_NAME, Integer.MAX_VALUE, originProductsIds);
    commerceStructBuilder = commerceStructBuilder.declareStrings(PRODUCT_LIST_NAME, Integer.MAX_VALUE, productIds);
    commerceStruct = commerceStructBuilder.build();
    structBuilder = newStruct.builder().declareStruct(COMMERCE_SUBSTRUCT_NAME, commerceStruct);
    newStruct = structBuilder.build();

    return newStruct;
  }

  public static Struct createStructWithProductIds(List<String> productIds, ContentRepository contentRepository) {

    StructService structService = contentRepository.getConnection().getStructService();

    Struct outerStruct = structService.emptyStruct();
    Struct commerceStruct = structService.emptyStruct();
    StructBuilder builder = outerStruct.builder().declareStruct(COMMERCE_SUBSTRUCT_NAME, commerceStruct);
    builder.enter(COMMERCE_SUBSTRUCT_NAME);
    builder.declareStrings(PRODUCT_LIST_NAME, Integer.MAX_VALUE, productIds);

    return builder.build();
  }

  public static List<String> getProductIdsFromPicture(Content picture) {
    try {
      Struct outerStruct = picture.getStruct(STRUCT_PROPERTY_NAME);
      if (outerStruct != null) {
        Struct commerceStruct = outerStruct.getStruct(COMMERCE_SUBSTRUCT_NAME);
        if (commerceStruct != null) {
          return commerceStruct.getStrings(PRODUCT_LIST_NAME);
        }
      }
    }
    catch (NoSuchPropertyDescriptorException e) {
      // do nothing
    }
    return Collections.emptyList();
  }

  /**
   * Removes the commerce struct from the given @param#struct
   * @param struct the local settings struct
   * @return A struct with no commerce substruct
   */
  public static Struct removeCommerceSubstruct(Struct struct) {
    StructBuilder structBuilder = struct.builder().remove(COMMERCE_SUBSTRUCT_NAME);
    return structBuilder.build();
  }

  /**
   * Removes the product data from the picture struct
   * @param content the image document
   * @return The updated struct
   */
  public static Struct updateCMPictureOnBlobDelete(Content content) {
    Struct struct = content == null ? null : content.getStruct(STRUCT_PROPERTY_NAME);
    if (struct == null) {
      return null;
    }

    Struct commerceStruct = StructUtil.getSubstruct(struct, COMMERCE_SUBSTRUCT_NAME);

    if(!(commerceStruct == null || commerceStruct.getProperties().isEmpty())) {
      boolean inherit = StructUtil.getBoolean(commerceStruct, INHERIT_NAME);
      if (inherit) {
        struct = updateStruct(struct, false, Collections.<String>emptyList(), Collections.<String>emptyList(), content.getRepository());
      }
    }
    return struct;
  }

  private static Struct getEmptyStruct(ContentRepository contentRepository) {
    return contentRepository.getConnection().getStructService().createStructBuilder().build();
  }

}
