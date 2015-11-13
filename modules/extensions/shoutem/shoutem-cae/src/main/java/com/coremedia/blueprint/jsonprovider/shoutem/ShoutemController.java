package com.coremedia.blueprint.jsonprovider.shoutem;

import com.coremedia.blueprint.common.contentbeans.CMSettings;
import com.coremedia.cap.common.NoSuchPropertyDescriptorException;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentRepository;
import com.coremedia.cap.struct.Struct;
import com.coremedia.elastic.social.api.users.CommunityUserService;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Locale;

/**
 * Spring controller used as a request facade if the Jersey ShoutemResource.
 * is used in a CAE. The problem raises then that the injections of the ShoutemApiImpl
 * are still null for each request, even if they have been initialized during startup.
 * (Probably 'cos the request is not passed through the dispatcher servlet.)
 */
@SuppressWarnings("PMD.CyclomaticComplexity")
public class ShoutemController implements Controller {
  private static final Logger LOG = LoggerFactory.getLogger(ShoutemController.class);

  static final String RESPONSE_MESSAGE_NOT_IMPLEMENTED = "NOT IMPLEMENTED";

  private ShoutemApi shoutemApi;
  private String settingsDocumentPath;
  private ContentRepository repository;

  @Inject
  private CommunityUserService userService;

  @Inject
  private AuthenticationManager authenticationManager;



  @Required
  public void setShoutemApi(ShoutemApi api) {
    this.shoutemApi = api;
  }

  @Required
  public void setSettingsDocumentPath(String settingsDocumentPath) {
    this.settingsDocumentPath = settingsDocumentPath;
  }

  @Required
  public void setContentRepository(ContentRepository repository) {
    this.repository = repository;
  }

  @Override
  public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception { // NOSONAR
    try {
      MultivaluedMap<String, String> params = buildParameterMap(request);
      String result = executeMethod(request, response, params);
      //shoutem api always returns json!
      response.setContentType("application/json");
      PrintWriter writer = response.getWriter();
      writer.write(result);
    } catch (WebApplicationException e) {
      //take the response code and message and write it to the response.
      PrintWriter writer = response.getWriter();
      response.setStatus(e.getResponse().getStatus());
      writer.write(String.valueOf(e.getResponse().getEntity()));
    }

    return null;
  }

  /**
   * Builds a request parameter map that can be processed by the jersey based resource class.
   *
   * @param request The servlet request that contains parameter values.
   * @return A multivalue map with a string to list mapping.
   */
  private MultivaluedMap<String, String> buildParameterMap(HttpServletRequest request) {
    MultivaluedMap<String, String> params = new MultivaluedMapImpl();
    for (String paramName : request.getParameterMap().keySet()) {
      String value = request.getParameter(paramName);
      params.put(paramName, Arrays.asList(value));
    }
    return params;
  }


  /**
   * Extracts the method name and it's parameters from the request value map.
   *
   * @param request  the request
   * @param response the response
   * @param params   All get or post parameters.
   */
  @SuppressWarnings("all")
  private String executeMethod(HttpServletRequest request, HttpServletResponse response,
                               MultivaluedMap<String, String> params) throws IOException {
    if (getParam(params, ShoutemApi.PARAM_METHOD) != null) {
      String method = params.get(ShoutemApi.PARAM_METHOD).get(0);

      /**
       * Checks the session and build the credentials first.
       */
      ShoutemApiCredentials credentials = createCredentials(request);
      String sessionId = getParam(params, ShoutemApi.PARAM_SESSION_ID);
      if (sessionId != null
              && !ShoutemApi.METHOD_POST_USERS_AUTHENTICATE.equalsIgnoreCase(method)
              && !credentials.isValid(sessionId)) {
        Response r = Response.status(401).entity("INVALID SESSION").build(); //NOSONAR
        throw new WebApplicationException(r);
      }

      /**
       * Try GET request first...
       */
      Object representation = getGetRequest(method, params, request, response, credentials);
      if (representation == null) {
        representation = getPostRequest(method, params, credentials);
      }

      //finally serialize the result if there is one
      if (representation != null) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "POST, PUT, GET, DELETE, HEAD, OPTIONS");
        return serializeRepresentation(representation);
      }
    }

    Response r = Response.status(501).entity(RESPONSE_MESSAGE_NOT_IMPLEMENTED).build(); //NOSONAR
    throw new WebApplicationException(r);
  }

    /**
     * Handles all POST requests or returns null if the method
     * is not a POST call.
     *
     * @param method      The name of the method that should be executed.
     * @param params      The request parameters
     * @param credentials The credentials of the user requesting the data.
     * @return The representation of the result or null if the method is not a POST request.
     */

  private Object getPostRequest(String method,
                                MultivaluedMap<String, String> params,
                                ShoutemApiCredentials credentials) {
    Object representation = null;
    if (ShoutemApi.METHOD_POST_USERS_AUTHENTICATE.equalsIgnoreCase(method)) {
      representation = shoutemApi.getUserAuthenticate(credentials,
              getMandatoryParam(params, ShoutemApi.PARAM_USERNAME),
              getMandatoryParam(params, ShoutemApi.PARAM_PASSWORD));
    } else if (ShoutemApi.METHOD_POST_POSTS_LIKES_NEW.equalsIgnoreCase(method)) {
      representation = shoutemApi.postPostsLikesNew(credentials,
              getMandatoryParam(params, ShoutemApi.PARAM_POST_ID));
    } else if (ShoutemApi.METHOD_POST_POSTS_LIKES_DELETE.equalsIgnoreCase(method)) {
      representation = shoutemApi.postPostsLikesDelete(credentials,
              getMandatoryParam(params, ShoutemApi.PARAM_POST_ID));
    } else if (ShoutemApi.METHOD_POST_POSTS_COMMENTS_NEW.equalsIgnoreCase(method)) {
      representation = shoutemApi.postPostsCommentsNew(credentials,
              getMandatoryParam(params, ShoutemApi.PARAM_POST_ID),
              getParam(params, ShoutemApi.PARAM_PARENT_COMMENT_ID),
              getParam(params, ShoutemApi.PARAM_AUTHOR_ID),
              getParam(params, ShoutemApi.PARAM_AUTHOR_NICKNAME),
              getParam(params, ShoutemApi.PARAM_AUTHOR_EMAIL),
              getParam(params, ShoutemApi.PARAM_AUTHOR_URL),
              getParam(params, ShoutemApi.PARAM_MESSAGE),
              getParam(params, ShoutemApi.PARAM_SUBJECT));
    } else if (ShoutemApi.METHOD_POST_POSTS_COMMENTS_UPDATE.equalsIgnoreCase(method)) {
      representation = shoutemApi.postPostsCommentsUpdate(credentials,
              getMandatoryParam(params, ShoutemApi.PARAM_COMMENT_ID),
              getParam(params, ShoutemApi.PARAM_MESSAGE));
    } else if (ShoutemApi.METHOD_POST_POSTS_COMMENTS_DELETE.equalsIgnoreCase(method)) {
      representation = shoutemApi.postPostsCommentsDelete(credentials,
              getMandatoryParam(params, ShoutemApi.PARAM_COMMENT_ID));
    }
    return representation;
  }

  /**
   * Handles all GET requests or returns null if the method
   * is not a GET call.
   *
   * @param method      The name of the method that should be executed.
   * @param params      The request parameters
   * @param request
   * @param response
   * @param credentials The credentials of the user requesting the data.
   * @return The representation of the result or null if the method is not a GET request.
   */
  private Object getGetRequest(String method,
                               MultivaluedMap<String, String> params,
                               HttpServletRequest request,
                               HttpServletResponse response,
                               ShoutemApiCredentials credentials) {
    Object representation = null;
    if (ShoutemApi.METHOD_GET_SERVICE_INFO.equalsIgnoreCase(method)) {
      representation = shoutemApi.getServiceInfo();
    } else if (ShoutemApi.METHOD_POST_USERS_LOGOUT.equalsIgnoreCase(method)) {
      representation = shoutemApi.getUserLogout(credentials);
    } else if (ShoutemApi.METHOD_GET_POSTS_CATEGORY.equalsIgnoreCase(method)) {
      representation = shoutemApi.getPostsCategories(credentials);
    } else if (ShoutemApi.METHOD_GET_POSTS_FIND.equalsIgnoreCase(method)) {
      representation = shoutemApi.getPostsFind(credentials, request, response,
              getIntParam(params, ShoutemApi.PARAM_CATEGORY_ID),
              getParam(params, ShoutemApi.PARAM_TERM),
              getIntParam(params, ShoutemApi.PARAM_OFFSET, 0),
              getIntParam(params, ShoutemApi.PARAM_LIMIT, ShoutemApi.DEFAULT_LIMIT));
    } else if (ShoutemApi.METHOD_GET_POSTS_GET.equalsIgnoreCase(method)) {
      representation = shoutemApi.getPostsGet(credentials, request, response,
              getMandatoryParam(params, ShoutemApi.PARAM_POST_ID));
    } else if (ShoutemApi.METHOD_GET_POSTS_LIKES.equalsIgnoreCase(method)) {
      representation = shoutemApi.getPostsLikes(credentials,
              getMandatoryParam(params, ShoutemApi.PARAM_POST_ID),
              getIntParam(params, ShoutemApi.PARAM_OFFSET, 0),
              getIntParam(params, ShoutemApi.PARAM_LIMIT, ShoutemApi.DEFAULT_LIMIT));
    } else if (ShoutemApi.METHOD_GET_POSTS_COMMENTS.equalsIgnoreCase(method)) {
      representation = shoutemApi.getPostsComments(credentials,
              getMandatoryParam(params, ShoutemApi.PARAM_POST_ID),
              getIntParam(params, ShoutemApi.PARAM_OFFSET, 0),
              getIntParam(params, ShoutemApi.PARAM_LIMIT, ShoutemApi.DEFAULT_LIMIT));
    }
    return representation;
  }

  /**
   * Creates a new credentials object for each request.
   * The credentials are "manually injected" with ES components to
   * verify and authenticate the current user.
   *
   * @param request The request
   * @return The Shoutem credentials instance.
   */
  private ShoutemApiCredentials createCredentials(HttpServletRequest request) {
    ShoutemApiCredentials credentials = new ShoutemApiCredentials();
    credentials.setAuthenticationManager(authenticationManager);
    credentials.setUserService(userService);

    String[] path = request.getPathInfo().split("/");
    int siteIndex = Arrays.asList(path).indexOf("shoutemapi") + 1;
    if (path.length <= siteIndex || siteIndex <= 0) {
      Response r = Response.status(501).entity("SITE SEGMENT NOT FOUND IN REQUEST URI '" + request.getPathInfo() + "'").build(); //NOSONAR
      throw new WebApplicationException(r);
    }
    credentials.setSite(path[siteIndex]);

    //resolve host name
    if(repository != null) {
      Content settings = repository.getChild(settingsDocumentPath);
      if(settings != null && settings.getType().isSubtypeOf(CMSettings.NAME)) {
        try {
          Struct settingsStruct = settings.getStruct(CMSettings.SETTINGS);
          String host = settingsStruct.getString("image-prefix");
          credentials.setHost(host);
        }
        catch (NoSuchPropertyDescriptorException e) {
          LOG.warn("No 'image-prefix' value defined for Shoutem settings. Create settings document " + settingsDocumentPath);
        }
      }
    }

    return credentials;
  }

  /**
   * Returns the formatted numeric parameter value or null if not set.
   *
   * @param params The params value map.
   * @param param  The name of the parameter to format.
   * @return null or the numeric value.
   */
  private static Integer getIntParam(MultivaluedMap<String, String> params, String param) {
    if (params.containsKey(param) && params.get(param) != null && !params.get(param).isEmpty()) {
      return getIntParam(params, param, 0);
    }

    return null;
  }

  /**
   * Returns the formatted numeric parameter value or null if not set.
   *
   * @param params       The params value map.
   * @param param        The name of the parameter to format.
   * @param defaultValue The numeric default value to use when the param is not set.
   * @return null or the numeric value.
   */
  private static Integer getIntParam(MultivaluedMap<String, String> params, String param, Integer defaultValue) {
    if (params.containsKey(param) && params.get(param) != null && !params.get(param).isEmpty()) {
      String value = params.get(param).get(0);
      try {
        return Integer.parseInt(value);
      } catch (NumberFormatException e) {
        return defaultValue;
      }
    }

    return defaultValue;
  }

  /**
   * Returns the formatted parameter value or throws Exception when not set.
   * Spec.: 401 => is the response when that post requires authenticated user and no
   * authentication data had been provided OR incorrect data has been provided.
   *
   * @param params The params value map.
   * @param param  The name of the parameter to format.
   * @return null or the value.
   */
  private static String getMandatoryParam(MultivaluedMap<String, String> params, String param) {
    String paramValue = getParam(params, param);
    if (paramValue == null) {
      Response r = Response.status(401).entity("MISSING MANDATORY PARAMETER '" + param + "'").build();//NOSONAR
      throw new WebApplicationException(r);
    }
    return paramValue;
  }

  /**
   * Returns the formatted parameter value or null if not set.
   *
   * @param params The params value map.
   * @param param  The name of the parameter to format.
   * @return null or the value.
   */
  private static String getParam(MultivaluedMap<String, String> params, String param) {
    if (params.containsKey(param) && params.get(param) != null && !params.get(param).isEmpty()) {
      return params.get(param).get(0);
    }

    return null;
  }

  /**
   * Use Jackson to convert representation to JSON.
   *
   * @param object The JSON representation POJO.
   * @return JSON string.
   * @throws java.io.IOException
   */
  private static String serializeRepresentation(Object object) throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    mapper.setDateFormat(new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH));
    return mapper.writeValueAsString(object);
  }
}
