pipelineJob('pip-as-code') {
  definition {
    cps {
      script(readFileFromWorkspace('groovy/parallel-pip.groovy'))
      sandbox()
    }
  }
}
