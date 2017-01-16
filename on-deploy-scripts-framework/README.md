# On-Deploy Script Framework

This is a framework to allow developers to create and leverage one-time scripts that run on an AEM server
upon deploy of a code bundle.


## Use Cases

##### Content updates that correlate with code updates/refactors
- Updating all instance of a component to a new `sling:resourceType` name or path.  This often happens when developers
reorganize the code structure such as /apps/mysite/facebook and /apps/mysite/twitter -> /apps/mysite/social/facebook
/apps/mysite/social/twitter.
- Copying a node property to a new property name.  This often happens as components over time refer to a single type
of data but with a different name, and there is a desire to consolidate to a common name so that code can easily work
with all nodes - e.g. `postal/zip/zipcode` -> `postalcode`

##### Admin updates that would need to be done on all servers in all environments
- Creating a user group
- Adding users/groups as members of a group
- Creating a site blueprint
- Configuring cloud configs (e.g. translation configuration) for a node tree

##### Mass content updates required by business that would be onerous for authors
- Updating a phone number everywhere from 555-555-5555 to 999-999-9999
- Updating all pages of a particular type to set a property such as "Hide in Navigation"
- Deleting all instances of a particular component
- Removing obsolete content

##### One-time content creations that you prefer not to have in your content package
- Preloading of data into AEM from a remote data source (content package could have thousands of nodes or more
depending on the data source)
- Creation of a default site structure (having this in your content package requires you to use a `merge` or `update`
deployment strategy for those nodes to not overwrite author updates, meaning your content package does not effectively
match reality on the servers)
- Creation of content meant for local developer environments only (having this in your content package will deploy it
to all environments, whereas an on-deploy script can check server run modes and execute only when applicable)


## Why use this tool?

There are other tools out there that allow you to run ad-hoc scripts against your AEM instance manually, but this is
the only fully automated tool that we know of.  The benefits of this tool include:

- Automation gives you automatic testing in your pre-prod environments.  You can be confident that things will work
correctly in production because the scripts have run on deployment to your pre-prod environments as well.
- Automation eliminates the chance of user error and/or omission when releasing to production (e.g. dev-ops forgetting
to run a script on the server post-deployment, developers forgetting to tell dev-ops to run the script in the first
place, etc.)
- Automation reduces work and required coordination.
    - No longer does anyone need to make sure they update all of the pre-prod environments with updates related to a
    code deployment.
    - No longer do pre-prod environments contain obsolete content because having an author update all environments
    would be too much work for the value.
    - No longer are developers sending out messages of "When you pull down my latest code, make sure you run "X" script
    else "Y" will blow up."
- Automation eases setup for new developers and QA personnel on your projects.  We've all played the game of "Your
AEM server isn't working properly? Did you run this script? Did you configure this property?" This issue simply goes
away as every person's AEM server checks the on-deploy scripts and runs whichever ones have not yet
been run automatically.
- Scripts are run at the same time as the code deployment.  As such, there is effectively no period of time where the
server is in a "broken" state for cases such as the example where a `sling:resourceType` path is moved and content needs
to reference the new `sling:resourceType` path.
- Scripts run on both the author and publish servers.  As such, content updates do not need to be activated/published.
This can be a big deal for mass content updates.
- Script run status is preserved within AEM, allowing easy access to determine if a script has run successfully on
any given server.


## How to install

NOTE: The current release package is built against AEM 6.2. For alternate AEM versions, please submit an issue
requesting a build for your version of AEM.  Or, feel free to build the project from sources, changing the
dependencies as appropriate.

1. Download the `on-deploy-scripts-framework.ui-1.0.0.zip` package under the `releases/aem62` folder
1. Upload the package to an AEM author instance via CRX package manager and install
1. Upload the package to an AEM publish instance via CRX package manager and install

## How to install from sources

If you have a running AEM author instance at http://localhost:4502 you can build and deploy into AEM with  

    mvn clean install -PautoInstallPackage
    
If you have a running AEM publish instance at http://localhost:4503 you can build and deploy into AEM with  

    mvn clean install -PautoInstallPackagePublish


## How to use

Once you have the `on-deploy-scripts-framework` package installed, you will need to create within your own code base:

- A service implementation of `com.hs2solutions.aem.ondeploy.core.execute.OnDeployScriptProvider`.  This service
implements a single function that returns a list of one-time script classes to run.
- Any number of one-time script classes.  These classes must adhere to the
`com.hs2solutions.aem.ondeploy.core.base.OnDeployScript` interface, and should generally do so by extending
`com.hs2solutions.aem.ondeploy.core.base.OnDeployScriptBase` which provides many OOTB helper functions, a logger, and
useful instance variables.


## Example Implementation

See the `on-deploy-scripts-framework-example-scripts` project in
[HS2 AEM Commons](https://github.com/HS2-SOLUTIONS/hs2-aem-commons) for an example `OnDeployScriptProvider`
implementation and a handful of script examples.

**NOTE: `on-deploy-scripts-framework-example-scripts` (the EXAMPLES project) should be
deployed only on a local development environment, as it updates content in the JCR to demonstrate functionality.**


## How to monitor

On-deploy scripts generate a status node under `/var/on-deploy-scripts-status` in AEM when they run. This status node
indicates whether the script was successful or not, and when it ran. A script that completes successfully will never
run again on this server (unless the status node is removed). A failed script will attempt to execute again during the
next code deployment.