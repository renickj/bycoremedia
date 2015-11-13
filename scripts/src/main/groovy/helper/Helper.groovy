package helper;

import javaposse.jobdsl.dsl.DslFactory

class Helper {

  final String buildJobName = "Build_LiveContext";


  def getScmConfig() {
    return {
      git
              {
                remote
                        {
                          url('https://bgaworsk@bitbucket.org/bgaworsk/boots-betalabs.git')
                          credentials('BitBucket Access (https)')
                        }
              }
    }

  }

  //  ######################################### deploy job #########################################

  def getDeployJob(DslFactory dslFactory, deployJobName, nodeHost, environment, useJenkinsUser=false) {
    return dslFactory.job(deployJobName) {
      logRotator(-1, 10, -1, 10)
      parameters
              {
                booleanParam('COPY_ARTIFACTS', false, 'Copy RPMs, content, users, etc. to target node?')
                choiceParam('LICENSE', ['dev', 'prod'], 'CoreMedia license')
                stringParam('NODE_HOST', nodeHost, 'Node do deploy to')
                stringParam('ENVIRONMENT', environment, 'Environment to deploy to ')
              }
      scm this.getScmConfig()

      wrappers
              {
                timestamps()
                colorizeOutput()
                runOnSameNodeAs(buildJobName, true)
                sshAgent('jenkins')
              }

      steps
              {
                if(useJenkinsUser){
                  shell(dslFactory.readFileFromWorkspace('scripts/jenkins-dsl/deploy-jenkinsUser.sh'))
                }
                else{
                  shell(dslFactory.readFileFromWorkspace('scripts/jenkins-dsl/deploy.sh'))
                }

              }
    }
  }

//  ######################################### content and users import job #########################################

  def getImportContentUsersJob(DslFactory dslFactory, jobName, nodeHost,environment, production=false) {
    return dslFactory.job(jobName) {
      logRotator(-1, 10, -1, 10)
      parameters
              {
                stringParam('NODE_HOST', nodeHost, 'Node do deploy to')
                stringParam('ENVIRONMENT', environment, 'Environment to deploy to ')
              }
      scm this.getScmConfig()

      wrappers
              {
                timestamps()
                colorizeOutput()
                runOnSameNodeAs(buildJobName, true)
                sshAgent('jenkins')
              }

      steps
              {
                if(production){
                  shell(dslFactory.readFileFromWorkspace('scripts/jenkins-dsl/import_content_users-prod.sh'))
                }
                else{
                  shell(dslFactory.readFileFromWorkspace('scripts/jenkins-dsl/import_content_users.sh'))
                }

              }
    }


  }

  //  ######################################### build job #########################################

  def getBuildJob(DslFactory dslFactory) {

    return dslFactory.job(buildJobName) {
      logRotator(-1, 10, -1, 10)
      scm this.getScmConfig()
      wrappers
              {
                timestamps()
                colorizeOutput()
              }
      steps
              {
                maven
                        {
                          goals('-DskipTests -U clean install')
                          mavenInstallation('Maven3')
                        }
              }
    }
  }


  //  ######################################### deploy frontend job #########################################

  def getDeployFrontendJob(DslFactory dslFactory, jobName, environment) {

    return dslFactory.job(jobName) {
      logRotator(-1, 10, -1, 10)
      parameters
              {
                stringParam('ENVIRONMENT', environment, 'Environment to deploy frontend to ')
              }
      scm this.getScmConfig()
      wrappers
              {
                timestamps()
                colorizeOutput()
                runOnSameNodeAs(buildJobName, true)
              }
      steps

              {
                maven
                        {
                          goals('-DskipTests -U clean install  -pl:estore-theme')
                          mavenInstallation('Maven3')
                        }
                shell (dslFactory.readFileFromWorkspace('scripts/jenkins-dsl/deploy_frontend.sh'))
              }
    }
  }



}