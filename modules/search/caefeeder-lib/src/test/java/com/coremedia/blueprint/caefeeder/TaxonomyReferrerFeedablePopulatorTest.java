package com.coremedia.blueprint.caefeeder;

import com.coremedia.blueprint.common.contentbeans.CMLocTaxonomy;
import com.coremedia.blueprint.common.contentbeans.CMSite;
import com.coremedia.blueprint.common.contentbeans.CMTaxonomy;
import com.coremedia.cae.testing.TestInfrastructureBuilder;
import com.coremedia.cap.feeder.MutableFeedable;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static com.coremedia.blueprint.testing.ContentTestCaseHelper.getContentBean;
import static org.junit.Assert.assertEquals;

/**
 * Tests for {@link TaxonomyReferrerFeedablePopulator}
 */
@SuppressWarnings("AssignmentToStaticFieldFromInstanceMethod")
public class TaxonomyReferrerFeedablePopulatorTest {

  private static TaxonomyReferrerFeedablePopulator testling;
  private static CMTaxonomy subjectTaxonomy;
  private static CMTaxonomy crosslinkingTaxonomy;
  private static CMLocTaxonomy locationTaxonomy;

  private MutableFeedable feedable;

  @BeforeClass
  public static void setupBeforeClass() {
    TestInfrastructureBuilder.Infrastructure infrastructure = TestInfrastructureBuilder
            .create()
            .withContentRepository("classpath:/com/coremedia/blueprint/caefeeder/taxonomreferrerfeedablepopulator/content.xml")
            .withContentBeanFactory()
            .withBeans("classpath:/framework/spring/blueprint-contentbeans.xml")
            .withBeans("classpath:/com/coremedia/blueprint/caefeeder/taxonomreferrerfeedablepopulator/spring-beans.xml")
            .withCache()
            .withSites()
            .build();

    testling = infrastructure.getBean("taxonomyReferrerFeedablePopulator", TaxonomyReferrerFeedablePopulator.class);

    locationTaxonomy = getContentBean(infrastructure, 42);
    subjectTaxonomy = getContentBean(infrastructure, 44);
    crosslinkingTaxonomy = getContentBean(infrastructure, 46);
  }

  /**
   * Test a {@link CMTaxonomy subjectTaxonomy} linked in only one {@link CMSite site}.
   */
  @Test
  public void testSubjectTaxonomy() {

    feedable = new MutableFeedableImpl() {
      @Override
      public void setElement(String s, Object o) {
        Set<String> expected = new HashSet<>();
        expected.add("/Sites/SubjectTaxonomySiteFolder");
        assertEquals("sites do not match", expected, o);
      }
    };

    testling.populate(feedable, subjectTaxonomy);

  }

  /**
   * Test a {@link CMLocTaxonomy locationTaxonomy} linked in only one {@link CMSite site}.
   */
  @Test
  public void testLocationTaxonomy() {

    feedable = new MutableFeedableImpl() {
      @Override
      public void setElement(String s, Object o) {
        Set<String> expected = new HashSet<>();
        expected.add("/Sites/LocationTaxonomySiteFolder");
        assertEquals("sites do not match", expected, o);
      }
    };

    testling.populate(feedable, locationTaxonomy);

  }

  /**
     * Test a {@link CMTaxonomy subjectTaxonomy} linked in two {@link CMSite sites}.
     */
  @Test
  public void testCrosslinkingTaxonomy() {

    feedable = new MutableFeedableImpl() {
      @Override
      public void setElement(String s, Object o) {
        Set<String> expected = new HashSet<>();
        expected.add("/Sites/SubjectTaxonomySiteFolder");
        expected.add("/Sites/LocationTaxonomySiteFolder");
        assertEquals("sites do not match", expected, o);
      }
    };

    testling.populate(feedable, crosslinkingTaxonomy);

  }

}
