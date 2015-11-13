final String deployJobName = 'Deploy_to_TEST_1_Delivery'
final String buildJobName = 'Build_LiveContext'

job(deployJobName)
        {
          logRotator(-1, 10, -1, 10)
          parameters
                  {
                    booleanParam('COPY_ARTIFACTS', false, 'Copy RPMs, content, users, etc. to target node?')
                    choiceParam('LICENSE', ['dev', 'prod'], 'CoreMedia license')
                  }
          scm
                  {
                    git
                            {
                              remote
                                      {
                                        url ('https://bitbucket.org/bgaworsk/boots-betalabs.git')
                                        credentials('BitBucket Access (https)')
                                      }
                            }
                  }

          wrappers
                  {
                    timestamps()
                    colorizeOutput()
                    runOnSameNodeAs(buildJobName, true)
                    sshAgent('bootsucd')
                  }

          steps
                  {
                    shell (readFileFromWorkspace('scripts/jenkins-dsl/deploy-urbanCode.sh'))
                  }
        }
