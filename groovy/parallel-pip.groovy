def builds = ['windows', 'linux', 'bsd', 'unit']

def branches = [:]

for(def i=0; i<builds.size(); i++) {
  // A quirk for pipelines
  def index=i
  def command
  // ToDo: make it a function
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
  // Actions to be used in parallel stage call below
  branches[builds[index]] = {
    node(builds[index]){
      // Unpack the workspace
        unstash 'cut'
        // Could use try/catch here for error handling
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

stage('init & prep pip'){
  node('git'){
    checkout([$class: 'GitSCM',
      branches: [[name: '*/ready/**']],
      doGenerateSubmoduleConfigurations: false,
      extensions: [[$class: 'RelativeTargetDirectory', relativeTargetDir: 'cut']],
      submoduleCfg: [],
      userRemoteConfigs: [[credentialsId: 'jenkins', url: 'git@github.com:drBosse/pipeline-snippets.git']]])
    // determine what branch triggered the jobdsl-gradle
    dir('cut'){
      if(isUnix()){
        sh 'git log --decorate --graph --oneline'
        sh 'git log --format=%d -n1 > GIT_LOG'
      } else {
        bat 'git log --format=%d -n1 > GIT_LOG'
      }
      def git_branch = readFile('GIT_LOG').replace(')','').split(',')[-1]
      currentBuild.description = git_branch
      if(isUnix()){
        sh 'git checkout origin/master'
        sh "git merge ${git_branch}"
      } else {
        bat 'git checkout origin/master'
        bat "git merge ${git_branch}"
      }
    }
    // Stash the workspace so we can use exact the same content on multiple nodes
    stash includes: 'cut/', name: 'cut'
  }
}

stage('builds'){
  parallel branches
}

stage('commit & cleanup'){
    node('git'){
        dir('cut'){
            if(isUnix()){
                sh 'git log --oneline --decorate --graph -n5'
                sh 'git push origin HEAD:refs/heads/master'
                sh "git push origin :refs/heads/${git_branch.replace('origin/', '')}"
            } else {
                bat 'git log --oneline --decorate --graph -n5'
                bat 'git push origin HEAD:refs/heads/master'
                bat "git push origin :refs/heads/${git_branch.replace('origin/', '')}"
            }
        }
    }
}
