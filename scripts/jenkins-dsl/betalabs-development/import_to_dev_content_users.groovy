import  helper.Helper;
import javaposse.jobdsl.dsl.DslFactory;

final String jobName = 'Import_to_DEV_content_users'
final String nodeHost = 'dev.cms.boots.com'
final String environment = 'betalabs-development'

Helper helper = new Helper();
helper.getImportContentUsersJob(this as DslFactory, jobName, nodeHost, environment)