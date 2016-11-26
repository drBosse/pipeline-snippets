def builds = ['windows', 'linux', 'bsd', 'unit']

def branches = [:]

for(def i=0; i<builds.size(); i++) {
  // ToDo: make if a function
  def index=i
  def command
  switch(builds[index]) {
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
  branches[builds[index]] = {
    node(builds[index]){
        unstash 'cut'
        dir('cut'){
          if(isUnix()){
            sh "${command}"
          }else {
            bat "${command}"
          }
        }
    }
  }
}

stage('init'){
  node('git'){
    checkout([$class: 'GitSCM',
      branches: [[name: 'master']],
      doGenerateSubmoduleConfigurations: false,
      extensions: [[$class: 'RelativeTargetDirectory', relativeTargetDir: 'cut']],
      submoduleCfg: [],
      userRemoteConfigs: [[credentialsId: 'jenkins', url: 'git@github.com:drBosse/pipeline-snippets.git']]])
    stash includes: 'cut/', name: 'cut'
  }
}
stage('builds'){
  parallel branches
}

stage('cleanup'){
    node('git'){
        dir('cut'){
            if(isUnix()){
                sh 'git log --oneline --decorate --graph -n5'
            } else {
                bat 'git log --oneline --decorate --graph -n5'
            }
        }
    }
}
