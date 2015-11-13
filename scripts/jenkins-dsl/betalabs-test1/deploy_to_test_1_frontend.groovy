import  helper.Helper;
import javaposse.jobdsl.dsl.DslFactory;

final String jobName = 'Deploy_to_TEST_1_Frontend'
final String environment = 'betalabs-test1'

Helper helper = new Helper();
helper.getDeployFrontendJob(this as DslFactory, jobName, environment)