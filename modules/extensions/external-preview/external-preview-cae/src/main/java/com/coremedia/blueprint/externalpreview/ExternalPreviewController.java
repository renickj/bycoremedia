package com.coremedia.blueprint.externalpreview;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Accepts Studio requests to update the data to preview for a specific user, identified by a token.
 * Invalidates outdated preview data automatically.
 */
public class ExternalPreviewController implements Controller {
  private static final Logger LOGGER = LoggerFactory.getLogger(ExternalPreviewController.class);

  //the max length for data
  private static final int MAX_DATA_LENGTH = 4048;
  private static final int MAX_URL_LENGTH = 2000;


  private static final String PARAMETER_METHOD = "method";
  private static final String PARAMETER_DATA = "data";
  private static final String PARAMETER_TOKEN = "token";

  private static final String METHOD_UPDATE = "update";
  private static final String METHOD_LIST = "list";
  private static final String METHOD_LOGIN = "login";
  private static final String METHOD_INVALIDATE = "invalidate";

  private static final String STATUS_ERROR = "{\"status\":\"no matching token found for polling\"}";
  private static final String STATUS_ERROR_METHOD = "{\"status\":\"no matching method found\"}";
  private static final String STATUS_ERROR_LOGIN = "{\"status\":\"no matching token found after login\"}";
  private static final String STATUS_OK = "{\"status\":\"ok\"}";


  @Override
  public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
    try {
      String token = request.getParameter(PARAMETER_TOKEN);
      String method = request.getParameter(PARAMETER_METHOD);

      if(method == null) {
        writeResponse(response, STATUS_ERROR_METHOD);
      } else if (method.equalsIgnoreCase(METHOD_UPDATE)) { //data put by the Studio REST extension
        String data = request.getParameter(PARAMETER_DATA);
        updatePreviewData(token, data);
      } else if (method.equalsIgnoreCase(METHOD_INVALIDATE)) { //invalidate token
        PreviewInfoService.getInstance().removePreview(token);
      } else if (method.equalsIgnoreCase(METHOD_LOGIN)) { //token login
        loginUser(response, token);
      } else if (method.equalsIgnoreCase(METHOD_LIST)) { //data requested by the external preview HTML
        readPreviewData(response, token);
      } else {
        writeResponse(response, STATUS_ERROR_METHOD);
      }

    } catch (WebApplicationException e) {
      LOGGER.error("Error in preview controller: " + e.getMessage(), e);//NOSONAR
      response.setStatus(e.getResponse().getStatus());
      writeResponse(response, String.valueOf(e.getResponse().getEntity()));
    }

    return null;
  }

  /**
   * Validates the login of the user by the given token.
   * @param response The response to write the validation result into.
   * @param token The token that identifies the user.
   * @throws IOException
   */
  private void loginUser(HttpServletResponse response, String token) throws IOException {
    String result = STATUS_OK;
    PreviewInfoItem item = PreviewInfoService.getInstance().getPreviewInfo(token);
    if (item == null) {
      result = STATUS_ERROR;
    }
    writeResponse(response, result);
  }

  /**
   * Writes the preview json data to the response if data is available for the given token.
   * @param response The response to write the data into.
   * @param token The token that identifies the user data.
   */
  private void readPreviewData(HttpServletResponse response, String token) throws IOException {
    PreviewInfoItem item = PreviewInfoService.getInstance().getPreviewInfo(token);
    String result;
    if (item != null) {
      result = item.asJSON();
    } else {
      result = STATUS_ERROR_LOGIN;
    }
    writeResponse(response, result);
  }

  /**
   * Updates the user preview data for the given token, if data is valid.
   * @param token The token that identifies the preview data.
   * @param data The json data that contains the preview information.
   */
  private void updatePreviewData(String token, String data) {
    if (isValidData(data)) {
      PreviewInfoService.getInstance().applyPreview(token, data);
    }
    else {
      Response r = Response.status(403).entity("External preview CAE controller rejected to store preview data: '" + data + "'").build();//NOSONAR
      throw new WebApplicationException(r);
    }
  }

  /**
   * Checks if the data is valid JSON data and does
   * not exceed the maximum size.
   *
   * @param json The json data passed by the Studio
   * @return
   */
  private boolean isValidData(String json) {
    JsonNode dataNode = readJsonNode(json);
    if(dataNode == null) {
      return false;
    }

    if(dataNode.get("previewUrl") == null) {
      return false;
    }
    String previewUrl = dataNode.get("previewUrl").getTextValue();
    if(previewUrl == null || !previewUrl.startsWith("http") || previewUrl.length() > MAX_URL_LENGTH) {
      return false;
    }
    if(dataNode.get("name") == null || dataNode.get("name").getTextValue() == null) {
      return false;
    }
    if(dataNode.get("id") == null || dataNode.get("id").getIntValue() < 0) {
      return false;
    }
    return true;
  }

  /**
   * Creates a json node instance from the json string value.
   * @param json The json data in string format.
   * @return The jackson JsonNode representation
   */
  private JsonNode readJsonNode(String json) {
    if(json == null || json.length() > MAX_DATA_LENGTH) {
      return null;
    }
    try {
      ObjectMapper mapper = new ObjectMapper();
      JsonNode df=mapper.readValue(json,JsonNode.class);
      return df.iterator().next();
    } catch (IOException e) {
      LOGGER.warn("Invalid json data passed to the external preview CAE controller: " + json, e);
    }
    return null;
  }

  /**
   * Writes the json response
   *
   * @param response The response object to write.
   * @param result   The JSON data that contains the request result.
   * @throws IOException
   */
  private void writeResponse(HttpServletResponse response, String result) throws IOException {
    response.setContentType("application/json");
    PrintWriter writer = response.getWriter();
    writer.write(result);
  }
}
