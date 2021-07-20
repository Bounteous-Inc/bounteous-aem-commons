# cmreactor.sh

A standalone, portable bash script to merge multiple AEM Cloud Service project sources 
into a single-commit git branch with a generated reactor pom.xml file and to force-push to a
Cloud Manager remote git repository.

You can easily add git repos to a cmreactor configuration file, which is 
written in git's own configuration format (hint: use `git config -f <my-cmreactor-config>`), 
with control over whether to checkout a particular branch or tag.

The default cmreactor config template starts with a `base` module that references the git
repository where you are running `cmreactor.sh` (via `cmreactor.modules.base.url = .`), along
with an `event-proxy` module that pulls in a release zip file from [Adobe I/O Event Proxy](https://www.adobe.io/apis/experienceplatform/events/docs.html#!adobedocs/adobeio-events/master/aem/aem_skyline_install.md) to demonstrate the flexibility of the script.

The script will `rsync` the `maven_repository` folder in any module into the
new reactor root directory, merging the contents of the folder with those
found in other modules, so that maven profile references to `file://${maven.multiModuleProjectDirectory}/maven_repository` 
will continue to work in Cloud Manager.


## Installation and Setup

1. Copy `cmreactor.sh` into the root directory of any git project you are
deploying to Adobe Cloud Manager.

2. Ensure the file is executable by running `chmod +x cmreactor.sh`.

3. Run `./cmreactor.sh --config-template > cmreactor.gitconfig` to create
a template cmreactor configuration.

4. If your push remote name is something other than `adobe`, run 
`git config -f cmreactor.gitconfig cmreactor.pushremote <myCMremote>`.

5. Run `./cmreactor.sh --file cmreactor.gitconfig` to push your first `cmreactor/*` branch,
which should then be available for selecting in a Cloud Manager Pipeline.

## Usage

    cmreactor.sh -f configFile

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

