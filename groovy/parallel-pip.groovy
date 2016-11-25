def builds = ['windows', 'linux', 'bsd', 'unit']

def branches = [:]

for(def i=0; i<builds.size(); i++) {
  // ToDo: make if a function
  def command
  switch(builds[i]) {
    case 'windows':
      command = 'echo msbuild'
      break
    case 'linux':
      command = 'echo make?'
      break
    case 'bsd':
      command = 'echo make?'
      break
    case 'unit':
      command = 'echo run unit-tests'
      break
    default:
      // should really throw an error
    break
  }
  branches[builds[i]] = {
    node(builds[i]){
        unstash 'cut'
        dir('cut'){
          if(isUnix()){
            sh 'command'
          }else {
            bat 'command'
          }
        }
    }
  }
}

stage('init'){
  node('git'){
    checkout([$class: 'GitSCM',
      branches: [[name: 'branch']],
      doGenerateSubmoduleConfigurations: false,
      extensions: [[$class: 'RelativeTargetDirectory', relativeTargetDir: 'cut']],
      submoduleCfg: [],
      userRemoteConfigs: [[credentialsId: 'user', url: 'ssh://git']]])
    stash includes: 'cut/', name: 'cut'
  }
}
stage('builds'){
  parallel branches
}
