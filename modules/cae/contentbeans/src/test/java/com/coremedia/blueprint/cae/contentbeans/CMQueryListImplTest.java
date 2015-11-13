package com.coremedia.blueprint.cae.contentbeans;

import com.coremedia.blueprint.cae.search.SearchQueryBean;
import com.coremedia.blueprint.cae.search.SearchResultBean;
import com.coremedia.blueprint.cae.search.SearchResultFactory;
import com.coremedia.blueprint.common.contentbeans.CMQueryList;
import com.coremedia.blueprint.common.contentbeans.CMTaxonomy;
import com.coremedia.blueprint.testing.ContentBeanTestBase;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Collections;
import java.util.List;


public class CMQueryListImplTest extends ContentBeanTestBase {

  private SearchResultFactory resultFactory;
  private CMQueryList contentBean;
  private CMQueryList contextContentBean;

  @Before
  public void setUp() throws Exception {
    setUpPreviewDate();
    contentBean = getContentBean(108);

    resultFactory = Mockito.mock(SearchResultFactory.class);
    SearchResultBean srb = Mockito.mock(SearchResultBean.class);
    Mockito.when(resultFactory.createSearchResultUncached(Mockito.any(SearchQueryBean.class))).thenReturn(srb);
    List result = Collections.emptyList();
    Mockito.when(srb.getHits()).thenReturn(result);

    contextContentBean = getContentBean(150);
    CMTaxonomy taxonomyBean = getContentBean(76);
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setAttribute("cmpage_model", taxonomyBean);
    RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
  }


  @Test
  public void testGetItems() throws Exception {
    CMQueryListImpl queryListImpl = (CMQueryListImpl) contentBean;
    queryListImpl.setResultFactory(resultFactory);
    Assert.assertEquals(1, contentBean.getItems().size());
  }

  @Test
  public void testGetContextItems() throws Exception {
    CMQueryListImpl queryListImpl = (CMQueryListImpl) contextContentBean;
    queryListImpl.setResultFactory(resultFactory);
    Assert.assertEquals(1, contextContentBean.getItems().size());
  }
}
