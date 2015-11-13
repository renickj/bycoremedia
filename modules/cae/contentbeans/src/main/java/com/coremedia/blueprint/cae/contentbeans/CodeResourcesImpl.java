package com.coremedia.blueprint.cae.contentbeans;

import com.coremedia.blueprint.common.contentbeans.CMAbstractCode;
import com.coremedia.blueprint.common.contentbeans.CMContext;
import com.coremedia.blueprint.common.contentbeans.CodeResources;

import javax.annotation.Nullable;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class CodeResourcesImpl implements CodeResources {

  private static final String DIGEST_ALGORITHM = "MD5";
  private final boolean developerMode;

  private CMContext context;
  private String codePropertyName;

  private String contentHash;

  private List<CMAbstractCode> mergeableResources = new ArrayList<>();
  private List<CMAbstractCode> ieExcludes = new ArrayList<>();
  private List<CMAbstractCode> externalLinks = new ArrayList<>();
  private List<CMAbstractCode> traversed = new ArrayList<>();

  public CodeResourcesImpl(CMContext context, String codePropertyName, boolean developerMode) {
    this.context = context;
    this.codePropertyName = codePropertyName;
    this.developerMode = developerMode;
    MessageDigest digest = createDigest();
    traverse(getCodeResourcsFromContext(), digest);
    contentHash = String.format("%01x", new BigInteger(1, digest.digest()));
  }

  @Override
  public CMContext getContext() {
    return context;
  }

  public String getCodePropertyName() {
    return codePropertyName;
  }

  @Override
  public String getETag() {
    return contentHash;
  }

  public boolean isDeveloperMode() {
    return developerMode;
  }

  @Override
  public List<?> getLinkTargetList() {
    List<Object> result = new ArrayList<>();
    result.addAll(getExternalLinks());
    if (isDeveloperMode()) {
      result.addAll(getMergeableResources());
    } else {
      result.add(this);
    }
    result.addAll(getIeExcludes());
    return result;
  }

  @Override
  public List<CMAbstractCode> getMergeableResources() {
    return mergeableResources;
  }

  //--- methods for calculating a hash for merged resources ---------

  /**
   * Compile a single hash code for the tree of codes linked in a navigation.
   */
  public MessageDigest createDigest() {
    try {
      return MessageDigest.getInstance(DIGEST_ALGORITHM);
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException("Unsupported digest algorithm", e);
    }
  }

  private List<? extends CMAbstractCode> getCodeResourcsFromContext() {
    return  CMNavigationBase.CSS.equals(getCodePropertyName())
      ? getContext().getCss()
      : getContext().getJavaScript();
  }

  //=== Internal =======================================================================================================

  // --- methods to get lists of resources for a given navigation ---------

  private List<CMAbstractCode> getIeExcludes() {
    return ieExcludes;
  }

    private List<CMAbstractCode> getExternalLinks() {
    return externalLinks;
  }

  /**
   * Compute a filtered lists of {@link CMAbstractCode codes} for the given list of codes and their {@link CMAbstractCode#getInclude() includes}.
   *
   */
  private void traverse(@Nullable List<? extends CMAbstractCode> codes, MessageDigest digest) {
    if (codes==null) {
      return;
    }
    for (CMAbstractCode code : codes) {
      //only traverse code if not already traversed.
      if (!traversed.contains(code)) {
        traversed.add(code);
        // get all included contents as well.
        if (!code.getInclude().isEmpty()) {
          traverse(code.getInclude(), digest);
        }
        boolean isIeExclude = code.getIeExpression().length() != 0;
        boolean isExternalLink = code.getDataUrl().length() != 0;
        // If an external links is also an IE exclude, treat it as an IE exclude as the conditional comments are required:
        if (isIeExclude) {
          ieExcludes.add(code);
        } else if (isExternalLink) {
          externalLinks.add(code);
        } else if(code.getCode() != null) {
          mergeableResources.add(code);
        }
        byte[] statusFlags = new byte[]{(byte) (isIeExclude ? 1 : 0), (byte) (isExternalLink ? 1 : 0)};
        digest.update(statusFlags);
        if(code.getCode() != null) {
          digest.update(Integer.toString(code.getCode().hashCode()).getBytes());
        }
      }
    }
  }
}
