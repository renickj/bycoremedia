package com.coremedia.livecontext.fragment;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;

/**
 * <p>
 * Factory for creating FragmentParameters objects.
 * <p/>
 * The FragmentParameters object may contains different number of matrix parameters, depending
 * of the type of fragment request, but contains at least the storeId and the locale, both
 * part of the request URL or the fragment request parameters.
 * </p>
 */
public class FragmentParametersFactory {

  /**
   * Factory method for creating the FragmentParameters by URL parsing and reading header values as context parameters.
   * Although the fragment request itself is mapped in the {@see FragmentPageHandler} the parameters
   * are needed before, since they are already read by filters and interceptors.
   * @param requestUrl The normalized request path.
   */
  @Nonnull
  public static FragmentParameters create(@Nonnull String requestUrl) {
    //manual parsing matrix paramters, e.g.: http://localhost:40081/blueprint/servlet/service/fragment/10851/en-US/params;parameter=noLinkRewrite;placement=header
    if (!requestUrl.contains(";")) {
      throw new IllegalArgumentException("Cannot extract matrix parameters from URL " + requestUrl);
    }

    //e.g.: http://localhost:40081/blueprint/servlet/service/fragment/10851/en-US/params
    String url = requestUrl.substring(0, requestUrl.indexOf(";"));
    String[] segments = url.split("/");
    String localeString = segments[segments.length - 2];

    String storeId = segments[segments.length - 3];
    Locale locale = Locale.forLanguageTag(localeString);

    Map<String, String> matrixParams = getMatrixParams(requestUrl);


    return new FragmentParameters(storeId, locale, matrixParams);
  }

  /**
   * matrix tokens:
   * dear developer who maintains this code: if you know the correct spring class for a parsing
   * matrix parameters, feel free to replace this code, because I think its creepy to do this manually here.
   *
   * @param requestUrl the request URL to retrieve the matrix parameters from.
   */
  private static Map<String, String> getMatrixParams(String requestUrl) {
    Map<String, String> matrixParams = new HashMap<>();
    String matrixParamsValues = requestUrl.substring(requestUrl.indexOf(";") + 1, requestUrl.length());
    List<NameValuePair> parameters = new ArrayList<>();
    Scanner scanner = new Scanner(matrixParamsValues);
    URLEncodedUtils.parse(parameters, scanner, ";", "utf-8");
    for (NameValuePair param : parameters) {
      matrixParams.put(param.getName(), param.getValue());
    }
    return matrixParams;
  }
}
