package com.coremedia.blueprint.cae.web.taglib;

import com.coremedia.blueprint.cae.ContentBeanTestBase;
import com.coremedia.blueprint.common.contentbeans.CMPicture;
import com.coremedia.cap.common.Blob;
import com.coremedia.objectserver.view.ViewServices;
import com.coremedia.objectserver.web.links.LinkFormatter;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockPageContext;
import org.springframework.mock.web.MockServletContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@Deprecated
public class ImgLinkTagTest extends ContentBeanTestBase {
  private ImgLinkTag imgLinkTag;
  private MockPageContext pageContext;

  @Before
  public void setUp() throws Exception {
    CMPicture image = getContentBean(16);
    MockHttpServletRequest request = new MockHttpServletRequest();
    LinkFormatter linkFormatter = Mockito.mock(LinkFormatter.class);

    Mockito.when(linkFormatter.formatLink(Mockito.any(Blob.class), Mockito.anyString(), Mockito.any(HttpServletRequest.class), Mockito.any(HttpServletResponse.class), Mockito.anyBoolean())).thenReturn("/Blob");

    MockServletContext context = new MockServletContext();
    request.setAttribute(ViewServices.LINK_FORMATTER, linkFormatter);
    pageContext = new MockPageContext(context, request);


    imgLinkTag = new ImgLinkTag();
    imgLinkTag.setPageContext(pageContext);

    imgLinkTag.setTheTarget(image);
  }

  @Test
  public void testDoEndTag() throws Exception {
    String link = "var";
    imgLinkTag.setVar(link);
    imgLinkTag.doEndTag();
    Assert.assertEquals("/Blob", pageContext.getAttribute(link));
    Map<String, Object> imageTransformation = new HashMap<>();
    imageTransformation.put("transformationName", "large3x2");
    imageTransformation.put("width", 50);
    imageTransformation.put("height", 50);
    imgLinkTag.setImageTransformation(imageTransformation);
    imgLinkTag.doEndTag();
    Assert.assertEquals("/Blob", pageContext.getAttribute(link));
  }
}
