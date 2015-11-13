import  helper.Helper;
import javaposse.jobdsl.dsl.DslFactory;

final String deployJobName = 'Deploy_to_PROD_Delivery_2'
final String nodeHost = 'BDCPRDCMDE02.cms.boots.com'
final String environment = 'production'
final boolean userJenkinsUser=true;

Helper helper = new Helper();
helper.getDeployJob(this as DslFactory, deployJobName, nodeHost, environment, userJenkinsUser);