#!/usr/bin/env bash
#
# Copyright 2021 Bounteous.
#
# Permission is hereby granted, free of charge, to any person obtaining a copy
# of this software and associated documentation files (the "Software"), to deal
# in the Software without restriction, including without limitation the rights
# to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
# copies of the Software, and to permit persons to whom the Software is
# furnished to do so, subject to the following conditions:

# The above copyright notice and this permission notice shall be included in all
# copies or substantial portions of the Software.

# THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
# IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
# FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
# AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
# LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
# OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
# SOFTWARE.
#
set -eo pipefail
readonly execPath="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"
if ! git -C "${execPath}" status >/dev/null 2>&1; then
    echo "$(basename "${BASH_SOURCE[0]}") must be located in a git repo before it can be executed." >&2
    exit 1
fi
readonly srcPath="$( git -C "${execPath}" rev-parse --show-toplevel )"
readonly projectPath="${srcPath}/target/cmreactor-project"
export LC_ALL=C

### BEGIN FUNCTIONS ###

template() {
cat <<EOT
[cmreactor]
	pushremote = adobe
	pushprefix = cmreactor/
	modules = base
	modules = event-proxy
[cmreactor "module.base"]
	type = git
	url = .
[cmreactor "module.event-proxy"]
	type = zip
	url = https://github.com/AdobeDocs/adobeio-events/releases/download/2020_07_20_13_00/aem-event-proxy-skyline-6.6.105.zip
EOT
}
usage() {
cat <<EOU
$(basename "${BASH_SOURCE[0]}") -f configFile

Merge multiple AEM Cloud Service project sources into a single-commit
git branch with a generated reactor pom.xml file and force-push to a
Cloud Manager remote git repository.

 CLI Options
  -f | --file <file>      read cmreactor config options from <file>
  -b | --branch <branch>  the base name for the reactor branch in the new repo
  -r | --remote-url <url> override the push remote url
  -t | --config-template  print the cmreactor config template JSON and exit
     | --no-push          skip the actual push step
  -x | --debug            debug script execution
  -h | --help             print this help message and exit

 Config Options
  cmreactor.pushremote    the name of the git remote in the ${srcPath}
                          repo to push the constructed branch to.

                          default: "adobe"

  cmreactor.pushprefix    a prefix to add to any branch name that is created
                          and pushed, to avoid unintentional collisions.

                          default: "cmreactor/"

  cmreactor.pushbranch    the default branch base name if not specifed with the
                          -b/--branch argument.

                          default: "main"

  cmreactor.configsync    a list of the git config property names to
                          sync to the constructed repo from
                          ${srcPath}.

                          default:
                            user.email
                            user.name

  cmreactor.modules       a list of module names to import. Module config sections
                          are identified by a key that is used as the directory
                          name for the module. See the Module Config Options
                          section for details about the related config sections.

 Module Config Options

  Append each key to a prefix of cmreactor.module.{name} where {name} matches an
  element in cmreactor.modules.

  .type                   the type of module fetch handler, which must be either
                          "git" or "zip".

  .url                    the url from where the source can be retrieved.

                          allowed url formats
                            git: any url supported by git clone, including local
                                 directories. The "." special case is treated as
                                 a reference to ${srcPath}.
                            zip: a path to a local zip file or any http or
                                 https url that serves an application/zip
                                 response in response to a curl -L request.

  .branch                 (git only; optional) if set, checkout the specified
                          branch or tag after cloning
EOU
}
### END FUNCTIONS ###
configFile=""
pushBranch=""
pushUrl=""
noPush=""
while [[ $# -gt 0 ]]; do
  opt="$1"
  shift
  case "$opt" in
    -b|--branch)              pushBranch="$1"; shift;;
    -r|--remote-url)          pushUrl="$1"; shift;;
    -h|--help)                usage; exit;;
    -t|--config-template)     template; exit;;
    --no-push)                noPush=true;;
    -f|--file)                configFile="$1"; shift;;
    -x|--debug)               set -x;;
    *)                        echo "unknown option $opt" >&2; usage; exit 1;;
  esac
done

if [[ "$configFile" == "" ]] || [[ ! -f "${configFile}" ]]; then
    echo "Please specify a json config file with -f / --file" >&2
    usage
    exit 1
fi

### BEGIN CMREACTOR FUNCTIONS ###
gitc() {
  git --no-pager -C "${srcPath}" "$@"
}

gitp() {
  git --no-pager -C "${projectPath}" "$@"
}

pathInSource() {
  local relPath
  local absPath
  local relParent
  local relName

  relPath="$1"
  if [[ "$relPath" == '' ]] || [[ "$relPath" == '/' ]] || [[ "$relPath" == "${srcPath}" ]]; then
    echo "${srcPath}"
    return
  else
    if [[ "$relPath" == "${srcPath}"/* ]]; then
      relPath="${relPath#"${srcPath}"}"
    fi
    relParent="$(dirname "${relPath}")"
    relName="$(basename "${relPath}")"
    case "$relParent" in
    /*) absPath="$(cd "${srcPath}${relParent}" && pwd)/${relName}" ;;
    *) absPath="$(cd "${srcPath}/${relParent}" && pwd)/${relName}" ;;
    esac
    if [[ "${absPath}" == "${srcPath}" ]] || [[ "${absPath}" == "${srcPath}"/* ]]; then
      echo "${absPath}"
      return
    else
      echo "${absPath} not a child of ${srcPath}" >&2
      exit 1
    fi
  fi
}

pathInProject() {
  local relPath
  local absPath
  local relParent
  local relName

  relPath="$1"
  if [[ "$relPath" == '' ]] || [[ "$relPath" == '/' ]] || [[ "$relPath" == "${projectPath}" ]]; then
    echo "${projectPath}"
    return
  else
    if [[ "$relPath" == "${projectPath}"/* ]]; then
      relPath="${relPath#"${projectPath}"}"
    fi
    relParent="$(dirname "${relPath}")"
    relName="$(basename "${relPath}")"
    case "$relParent" in
    /*) absPath="$(cd "${projectPath}${relParent}" && pwd)/${relName}" ;;
    *) absPath="$(cd "${projectPath}/${relParent}" && pwd)/${relName}" ;;
    esac
    if [[ "${absPath}" == "${projectPath}" ]] || [[ "${absPath}" == "${projectPath}"/* ]]; then
      echo "${absPath}"
      return
    else
      echo "${absPath} not a child of ${projectPath}" >&2
      exit 1
    fi
  fi

}

reactorPomInit() {
    local pomFile
    local module
    pomFile="$(pathInProject pom.xml)"
    cat > "${pomFile}" <<EOROOTXMLSTART
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <groupId>cmreactor</groupId>
    <artifactId>cmreactor</artifactId>
    <version>0-SNAPSHOT</version>
    <packaging>pom</packaging>
    <modules>
EOROOTXMLSTART
while [[ "$#" -gt 0 ]]; do
    module="$1"
    shift
cat >> "${pomFile}" <<EOROOTXMLMOD
        <module>${module}</module>
EOROOTXMLMOD
done
cat >> "${pomFile}" <<EOROOTXMLEND
    </modules>
</project>
EOROOTXMLEND
}

readConfigValue() {
    local config
    local key
    local defaultValue

    if [[ $# -lt 2 ]]; then
        echo "readConfigValue requires two args: config key [optional default]" >&2
        exit 1
    fi

    config="$1"
    key="$2"
    defaultValue="$3"

    if gitc config -f "${config}" "${key}" >/dev/null; then
        gitc config -f "${config}" "${key}"
    elif [[ "$defaultValue" != "" ]]; then
        echo "$defaultValue"
    fi
}

readGitConfigSyncElements() {
    local config
    config="$1"

    if gitc config -f "${config}" --get-all "cmreactor.configsync" >/dev/null; then
        gitc config -f "${config}" --get-all "cmreactor.configsync"
    else
        echo user.email
        echo user.name
    fi
}

readModulesFromConfig() {
    local config
    config="$1"

    if gitc config -f "${config}" --get-all "cmreactor.modules" >/dev/null; then
        gitc config -f "${config}" --get-all "cmreactor.modules"
    fi
}

readModuleConfigValue() {
    local config
    local module
    local key
    local defaultValue

    if [[ $# -lt 3 ]]; then
        echo "readModuleConfigValue requires three args: config module key [optional default]" >&2
        exit 1
    fi

    config="$1"
    module="$2"
    key="$3"
    defaultValue="$4"

    if gitc config -f "${config}" "cmreactor.module.${module}.${key}" >/dev/null; then
        gitc config -f "${config}" "cmreactor.module.${module}.${key}"
    elif [[ "$defaultValue" != "" ]]; then
        echo "$defaultValue"
    fi
}

handleZipModule() {
    local config
    local module
    local tempBase
    local url
    local zipFile
    local extractedDir
    local firstPomParentDir
    local modulePath
    local metaPath
    local hash

    config="$1"
    module="$2"
    tempBase="$3"

    modulePath="$(pathInProject "${module}")"
    url="$(readModuleConfigValue "${configFile}" "${module}" "url")"
    zipFile="${tempBase}/${module}.zip"

    if [[ "${url}" == http* ]]; then
        curl -f -s -L -o "${zipFile}" "${url}"
    elif [[ -f "${url}" ]]; then
        cp "${url}" "${zipFile}"
    else
        return 1
    fi
    hash="$(git hash-object "${zipFile}")"
    extractedDir="${tempBase}/${module}"
    [[ -d "${extractedDir}" ]] && rm -rf "${extractedDir}"
    mkdir -p "${extractedDir}"
    if pushd "${extractedDir}" >>/dev/null; then
        unzip "${zipFile}" >>/dev/null
        popd >>/dev/null
    else
        return 1
    fi
    firstPomParentDir="$(find "${extractedDir}" -name 'pom.xml' -exec dirname '{}' \; | sort | head -n 1)"
    if [[ "${firstPomParentDir}" != "" ]]; then
        [[ -d "${modulePath}" ]] && rm -rf "${modulePath}"
        mv "${firstPomParentDir}" "${modulePath}"

        metaFile="${modulePath}/cmreactor-meta.gitconfig"
        gitp config -f "${metaFile}" "cmreactor.meta.${module}.type" "zip"
        gitp config -f "${metaFile}" "cmreactor.meta.${module}.url" "${url}"
        gitp config -f "${metaFile}" "cmreactor.meta.${module}.hash" "${hash}"
    fi
}

handleGitModule() {
    local config
    local module
    local tempBase
    local url
    local origin
    local branch
    local extractedDir
    local modulePath
    local metaFile

    config="$1"
    module="$2"
    tempBase="$3"

    modulePath="$(pathInProject "${module}")"
    url="$(readModuleConfigValue "${configFile}" "${module}" "url")"
    if [[ "$url" == "" ]]; then
        return 1
    elif [[ "$url" == "." ]]; then
        url="$(pwd)"
    fi

    if [[ -d "${url}/.git" ]]; then
        origin="$(git --no-pager -C "${url}" remote get-url origin)"
    else
        origin="$url"
    fi

    extractedDir="${tempBase}/${module}"
    [[ -d "${extractedDir}" ]] && rm -rf "${extractedDir}"

    git clone "${url}" "${extractedDir}"
    branch="$(readModuleConfigValue "${configFile}" "${module}" "branch")"
    if [[ "$branch" != "" ]]; then
        git --no-pager -C "${extractedDir}" checkout "$branch"
    fi
    git --no-pager -C "${extractedDir}" remote set-url origin "${origin}"

    [[ -d "${modulePath}" ]] && rm -rf "${modulePath}"
    mv "${extractedDir}" "${modulePath}"

    metaFile="${modulePath}/cmreactor-meta.gitconfig"
    gitp config -f "${metaFile}" "cmreactor.meta.${module}.type" "git"
    gitp config -f "${metaFile}" "cmreactor.meta.${module}.url" "${url}"
    gitp config -f "${metaFile}" "cmreactor.meta.${module}.origin" "${origin}"
    gitp config -f "${metaFile}" "cmreactor.meta.${module}.branch" "$(git --no-pager -C "${modulePath}" rev-parse --abbrev-ref HEAD)"
    gitp config -f "${metaFile}" "cmreactor.meta.${module}.commit" "$(git --no-pager -C "${modulePath}" rev-parse HEAD)"
    gitp config -f "${metaFile}" "cmreactor.meta.${module}.oneline" "$(git --no-pager -C "${modulePath}" show -s --oneline)"
}

recordReactorGitMeta() {
    local metaFile
    metaFile="$(pathInProject "cmreactor-meta.gitconfig")"

    gitp config -f "${metaFile}" "cmreactor.reactor-meta.origin" "$(gitc remote get-url origin)"
    gitp config -f "${metaFile}" "cmreactor.reactor-meta.branch" "$(gitc rev-parse --abbrev-ref HEAD)"
    gitp config -f "${metaFile}" "cmreactor.reactor-meta.commit" "$(gitc rev-parse HEAD)"
    gitp config -f "${metaFile}" "cmreactor.reactor-meta.oneline" "$(gitc show -s --oneline)"
}

syncMavenRepositoryFolders() {
    find "${projectPath}" -name maven_repository -type d -mindepth 2 \
        -exec rsync -a '{}'/ "${projectPath}/maven_repository/" \;
}
### END CMREACTOR FUNCTIONS ###

if [[ "${projectPath}" == "/" ]]; then
  echo "$(basename "${BASH_SOURCE[0]}") prevented root path removal" >&2
  exit 1
fi

[[ -d "${projectPath}" ]] && rm -rf "${projectPath}"
mkdir -p "${projectPath}"

declare -a moduleNames
moduleNames=($(readModulesFromConfig "${configFile}"))

reactorPomInit "${moduleNames[@]}"
recordReactorGitMeta

if [[ "${moduleNames[*]}" != "" ]]; then
    tempBase="$(pathInSource target/cmreactor-tmp)"
    mkdir -p "${tempBase}"
    for moduleName in "${moduleNames[@]}"; do
        moduleType="$(readModuleConfigValue "${configFile}" "${moduleName}" "type")"
        case "$moduleType" in
        zip)    handleZipModule "${configFile}" "${moduleName}" "${tempBase}";;
        git)    handleGitModule "${configFile}" "${moduleName}" "${tempBase}";;
        *)      echo "Unsupported module type $moduleType for module $moduleName" >&2
                continue;;
        esac
        rm -rf "$(pathInProject "${moduleName}/.git")"
    done
fi

syncMavenRepositoryFolders

pushRemote="$(readConfigValue "${configFile}" "cmreactor.pushremote" "adobe")"
pushPrefix="$(readConfigValue "${configFile}" "cmreactor.pushprefix" "cmreactor/")"
if [[ "$pushBranch" == "" ]]; then
    pushBranch="$(readConfigValue "${configFile}" "cmreactor.pushbranch" "main")"
fi
cp -f "${configFile}" "$(pathInProject cmreactor.gitconfig)"
gitp init --initial-branch="${pushPrefix}${pushBranch}"

declare -a gitConfigs
gitConfigs=($(readGitConfigSyncElements "${configFile}"))
for gitConfig in "${gitConfigs[@]}"; do
    gitp config "${gitConfig}" "$(gitc config "${gitConfig}")"
done
if [[ "${pushUrl}" == "" ]]; then
    pushUrl="$(gitc remote get-url "${pushRemote}")"
fi
gitp remote add "${pushRemote}" "${pushUrl}"
gitp add .
gitp commit -m "cmreactor commit"
if [[ "$noPush" == "true" ]]; then
    echo "skipped push. run command to push:" >&2
    echo "" >&2
    echo "  git -C '${projectPath}' push --force '${pushRemote}' '${pushPrefix}${pushBranch}'" >&2
    echo "" >&2
else
    gitp push --force "${pushRemote}" "${pushPrefix}${pushBranch}"
fi
