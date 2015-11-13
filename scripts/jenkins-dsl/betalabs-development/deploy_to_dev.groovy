import  helper.Helper;
import javaposse.jobdsl.dsl.DslFactory;

final String deployJobName = 'Deploy_to_DEV'
final String nodeHost = 'dev.cms.boots'
final String environment = 'betalabs-development'

Helper helper = new Helper();
helper.getDeployJob(this as DslFactory, deployJobName, nodeHost, environment);