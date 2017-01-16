# On-Deploy Script Framework Example Scripts

**NOTE: `on-deploy-scripts-framework-example-scripts` should be
deployed only on a local development environment, as it updates content in the JCR to demonstrate functionality.**

This is an example package used to demonstrate how to leverage the
[on-deploy-scripts-framework](https://github.com/HS2-SOLUTIONS/hs2-aem-commons/tree/master/on-deploy-scripts-framework)
project at
[HS2 AEM Commons](https://github.com/HS2-SOLUTIONS/hs2-aem-commons) to implement one-time scripts that run
on an AEM server upon deploy of a code bundle.  See the
[on-deploy-scripts-framework](https://github.com/HS2-SOLUTIONS/hs2-aem-commons/tree/master/on-deploy-scripts-framework)
project for use cases and reasons why this can be a huge time saver for your AEM team!


## How to install

**NOTE: `on-deploy-scripts-framework-example-scripts` should be
deployed only on a local development environment, as it updates content in the JCR to demonstrate functionality.**

NOTE: The current release package is built against AEM 6.2. For alternate AEM versions, please submit an issue
requesting a build for your version of AEM.  Or, feel free to build the project from sources, changing the
dependencies as appropriate.

1. Download the `on-deploy-scripts-framework-example-scripts.ui-1.0.0.zip` package under the `releases/aem62` folder
1. Upload the packages to a ***local development*** AEM author server via CRX package manager and install
1. Upload the packages to a ***local development*** AEM publish server via CRX package manager and install

## How to install from sources

**NOTE: `on-deploy-scripts-framework-example-scripts` should be
deployed only on a local development environment, as it updates content in the JCR to demonstrate functionality.**

If you have a running AEM author instance at http://localhost:4502 you can build and deploy into AEM with  

    mvn clean install -PautoInstallPackage
    
If you have a running AEM publish instance at http://localhost:4503 you can build and deploy into AEM with  

    mvn clean install -PautoInstallPackagePublish


## How to use

The primary use case of this code package is not actually to install it, but rather to review the code so you can
see how to implement scripts of your own using the On-Deploy Scripts Framework (`on-deploy-scripts-framework`).

This package *can* be installed to demonstrate functionality. The
[on-deploy-scripts-framework](https://github.com/HS2-SOLUTIONS/hs2-aem-commons/tree/master/on-deploy-scripts-framework)
package from
[HS2 AEM Commons](https://github.com/HS2-SOLUTIONS/hs2-aem-commons) must be installed prior to installing
this package.  Once that is installed, you can then install the `on-deploy-scripts-framework-example-scripts` package.

**NOTE: `on-deploy-scripts-framework-example-scripts` should be
deployed only on a local development environment, as it updates content in the JCR to demonstrate functionality.**

Upon installation of the Examples package, search the standard
AEM `error.log` file for output that matches "OnDeploy" to verify output from the example scripts for the updates
being made in AEM.  See the "How to monitor" section of the
[on-deploy-scripts-framework](https://github.com/HS2-SOLUTIONS/hs2-aem-commons/tree/master/on-deploy-scripts-framework)
documentation for further monitoring instructions.
