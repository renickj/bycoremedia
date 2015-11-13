package com.coremedia.livecontext.fragment;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class FragmentContextTest {
  @Test
  public void isFragmentRequest() {
    FragmentContext fragmentContext = new FragmentContext();
    assertFalse(new FragmentContext().isFragmentRequest());

    fragmentContext.setFragmentRequest(true);
    assertTrue(fragmentContext.isFragmentRequest());
  }
}
