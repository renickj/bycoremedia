package com.coremedia.livecontext.ecommerce.ibm.user;


import com.coremedia.livecontext.ecommerce.ibm.common.AbstractServiceTest;
import com.coremedia.livecontext.ecommerce.user.UserContext;
import org.junit.Test;
import org.mockito.Mockito;

import static junit.framework.Assert.assertNull;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class UserContextHelperTest extends AbstractServiceTest {

  @Test
  public void testNullContext(){
    assertNull(UserContextHelper.getForUserName(null));
  }

  @Test
  public void testInvalidValues(){
    UserContext userContext = Mockito.mock(UserContext.class);
    when(userContext.get(UserContextHelper.FOR_USER_NAME)).thenReturn(new Object());

    assertNull(UserContextHelper.getForUserName(userContext));
  }

  @Test
  public void testCreateContext(){
    UserContext userContext = UserContextHelper.createContext("forUser", null);
    assertEquals("forUser",  UserContextHelper.getForUserName(userContext));
  }

  @Test
  public void testCurrentContext(){
    UserContext userContext = UserContextHelper.createContext("forUser", null);
    UserContextHelper.setCurrentContext(userContext);
    assertEquals(userContext, UserContextHelper.getCurrentContext());

    UserContext userContext2 = UserContextHelper.createContext("forUser2", null);
    UserContextHelper.setCurrentContext(userContext2);
    assertEquals(userContext2, UserContextHelper.getCurrentContext());
  }
}
