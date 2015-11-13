package com.coremedia.livecontext.handler;

import com.coremedia.livecontext.ecommerce.common.StoreContextProvider;
import com.coremedia.livecontext.ecommerce.user.UserContext;
import com.coremedia.livecontext.ecommerce.user.UserContextProvider;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class LiveContextPageHandlerBaseTest {
  @Mock
  private StoreContextProvider storeContextProvider;

  @Mock
  private UserContextProvider userContextProvider;

  @Mock
  private SecurityContext securityContext;

  @Before
  public void beforeEachTest() {
    initMocks(this);
    LiveContextPageHandlerBase testling = spy(new LiveContextPageHandlerBase());
    doReturn(securityContext).when(testling).getSecurityContext();
  }

  @Test
  public void testInitUserContextNoKnownUser() throws Exception {
    configureSpringSecurity("anonymous");

    verify(userContextProvider, times(0)).createContext(anyString());
    verify(userContextProvider, times(0)).setCurrentContext(any(UserContext.class));
  }

  @Test
  public void testInitUserContextNoSession() throws Exception {
    verify(userContextProvider, times(0)).createContext(anyString());
    verify(userContextProvider, times(0)).setCurrentContext(any(UserContext.class));
  }

  private void configureSpringSecurity(Object user) {
    Authentication authentication = mock(Authentication.class);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    when(authentication.getPrincipal()).thenReturn(user);
  }
}
