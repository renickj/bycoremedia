import  helper.Helper;
import javaposse.jobdsl.dsl.DslFactory;

final String jobName = 'Import_to_TEST_1_content_users'
final String nodeHost = 'test-management1.cms.boots.com'
final String environment = 'betalabs-test1'


Helper helper = new Helper();
helper.getImportContentUsersJob(this as DslFactory, jobName, nodeHost, environment)
