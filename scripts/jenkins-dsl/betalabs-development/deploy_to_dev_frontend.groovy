import  helper.Helper;
import javaposse.jobdsl.dsl.DslFactory;

final String jobName = 'Deploy_to_DEV_Frontend'
final String environment = 'betalabs-development'

Helper helper = new Helper();
helper.getDeployFrontendJob(this as DslFactory, jobName, environment)