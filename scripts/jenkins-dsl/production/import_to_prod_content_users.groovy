import  helper.Helper;
import javaposse.jobdsl.dsl.DslFactory;

final String jobName = 'Import_to_PROD_content_users'
final String buildJobName = 'Build_LiveContext'

final String nodeHost = 'BDCPRDCMM01.cms.boots.com'
final String environment = 'production'
final boolean useJenkinsUser=true;

Helper helper = new Helper();
helper.getImportContentUsersJob(this as DslFactory, jobName, nodeHost, environment, useJenkinsUser)