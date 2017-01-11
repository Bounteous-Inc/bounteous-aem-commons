# On-Deploy Script Framework

This is a framework to allow developers to create scripts to run on the AEM server on-deploy of a code bundle. There
are numerous use cases for this, including:

- Updating the sling:resourceType of all content instances of a component that has changed name/path in the code base
- Creating a new user group and/or adding members to a user group

## How to install from package

1. Download the XXX packages
1. Upload the packages to AEM author via CRX package manager and install
1. Upload the packages to AEM publish via CRX package manager and install

## How to install from sources

If you have a running AEM author instance at http://localhost:4502 you can build and deploy into AEM with  

    mvn clean install -PautoInstallPackage
    
If you have a running AEM publish instance at http://localhost:4503 you can build and deploy into AEM with  

    mvn clean install -PautoInstallPackagePublish