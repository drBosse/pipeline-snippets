pipelineJob('pip-as-code') {
  triggers {
    scm('H/3 * * * *')
  }
  definition {
    cps {
      script(readFileFromWorkspace('groovy/parallel-pip.groovy'))
      sandbox()
    }
  }
}
