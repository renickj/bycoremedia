import  helper.Helper;
import javaposse.jobdsl.dsl.DslFactory;

final String deployJobName = 'Deploy_to_TEST_1_Management'
final String nodeHost = 'test-management1.cms.boots.com'
final String environment = 'betalabs-test1'

Helper helper = new Helper();
helper.getDeployJob(this as DslFactory, deployJobName,  nodeHost, environment);