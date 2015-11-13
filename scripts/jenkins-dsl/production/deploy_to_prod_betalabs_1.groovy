import  helper.Helper;
import javaposse.jobdsl.dsl.DslFactory;

final String deployJobName = 'Deploy_to_PROD_BetaLabs_BDCPRDCMBL01'
final String nodeHost = 'BDCPRDCMBL01.cms.boots.com'
final String environment = 'production'
final boolean useJenkinsUser=true;

Helper helper = new Helper();
helper.getDeployJob(this as DslFactory, deployJobName, nodeHost, environment, useJenkinsUser);