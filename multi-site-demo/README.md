# Multi-Site Demo

This is a demo project used for "Adobe IMMERSE 2018: Shared Component Properties - Saying goodbye to the global configs page."
The project demonstrates how to leverage Shared Component Properties on your site using latest coding patterns including
sling model injection and [WCM Core Components](https://github.com/Adobe-Marketing-Cloud/aem-core-wcm-components).

This project was previously used for "Adobe IMMERSE 2017: Multi-Site Platforms - Setting your Codebase Up for Success."
See details or pull the previous source code from the
[immerse-2017-multi-site-tips-aem-6.2](https://github.com/HS2-SOLUTIONS/hs2-aem-commons/tree/immerse-2017-multi-site-tips-aem-6.2/multi-site-demo)
branch.

## Contents

- Code packages
    - hfl-core.ui - Base code supporting all HFL sites.
    - hfl-boars.ui - Code specific to Chicago Boars site.
    - hfl-peckers.ui - Code specific to Green Bay Peckers site.
- Content package
    - Site content that is not part of the code base that would normally be created on the AEM server by
      content authors.
    - This content package is necessary to fully demo the coding principles w/o authoring the entire site contents
      on your own.
- Adobe IMMERSE 2018 presentation deck
    - Download [immerse-2018-hs2-solutions-shared-component-properties.pptx](https://github.com/HS2-SOLUTIONS/hs2-aem-commons/tree/master/multi-site-demo/releases/immerse-2018-hs2-solutions-shared-component-properties.pptx)

## How to install

NOTE: The current release code packages are built and tested against AEM 6.3 SP2. For later AEM versions you may need
to build the code packages (all but `hfl-content-2.0.zip`) from sources, changing the dependencies as appropriate.

1. Download the following packages from under the `releases` folder:
    - (code) `hfl-core.ui-2.0.0-SNAPSHOT.zip`
        - (embedded code) `acs-aem-commons-content-3.17.0.zip`
        - (embedded code) `core.wcm.components.all-2.0.6.zip`
            - (embedded code) `core.wcm.components.content-2.0.6.zip`
            - (embedded code) `core.wcm.components.config-2.0.6.zip`
    - (code) `hfl-boars.ui-2.0.0-SNAPSHOT.zip`
    - (code) `hfl-peckers.ui-2.0.0-SNAPSHOT.zip`
    - (content) `hfl-content-2.0.zip`
1. Upload the packages to an AEM server instance via CRX package manager and install in the order listed above.

## How to install from sources

If you have a running AEM author instance at http://localhost:4502 you can build and deploy the code packages
into AEM with  

    mvn clean install -PautoInstallPackage
    
If you have a running AEM publish instance at http://localhost:4503 you can build and deploy the code packages
into AEM with  

    mvn clean install -PautoInstallPackagePublish
    
Deploying the project with this method will deploy the "code" packages only.  You will still need to upload and install
the content package (`hfl-content-2.0.zip`), which should be compatible with any AEM version, via CRX package manager.