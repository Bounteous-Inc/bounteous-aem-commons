# Multi-Site Demo

This is a demo project used for "AEM IMMERSE 2017: Multi-Site Platforms - Setting your Codebase Up for Success."
The project demonstrates 15 coding principles for multi-site platforms.  See more details on the
[Adobe community blog](http://blogs.adobe.com/contentmanagement/2017/04/24/aem-multi-site-tips-tricks-preview-immerse-2017).

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
- Adobe IMMERSE 2017 presentation deck
    - A PowerPoint deck used to give the mentality and reasoning behind the 15 principles demonstrated by this project
      along with a 16th principle supported by the
      [On-Deploy Script Framework](https://github.com/HS2-SOLUTIONS/hs2-aem-commons/tree/master/on-deploy-scripts-framework)
    - Download [immerse-multi-site-success.pptx](https://github.com/HS2-SOLUTIONS/hs2-aem-commons/blob/master/multi-site-demo/releases/immerse-multi-site-success.pptx?raw=true)

## How to install

NOTE: The current release code packages are built and tested against AEM 6.2. For alternate AEM versions you will need
to build the code packages (all but `hfl-content-1.0.zip`) from sources, changing the dependencies as appropriate.

1. Download the following packages from under the `releases` folder:
    - (code) `hfl-core.ui-1.0.0-SNAPSHOT.zip` (contains embedded `acs-aem-commons-content-3.8.5-SNAPSHOT.zip`)
    - (code) `hfl-boars.ui-1.0.0-SNAPSHOT.zip`
    - (code) `hfl-peckers.ui-1.0.0-SNAPSHOT.zip`
    - (content) `hfl-content-1.0.zip`
1. Upload the packages to an AEM server instance via CRX package manager and install in the order listed above.

## How to install from sources

If you have a running AEM author instance at http://localhost:4502 you can build and deploy the code packages
into AEM with  

    mvn clean install -PautoInstallPackage
    
If you have a running AEM publish instance at http://localhost:4503 you can build and deploy the code packages
into AEM with  

    mvn clean install -PautoInstallPackagePublish
    
Deploying the project with this method will deploy the "code" packages only.  You will still need to upload and install
the content package (`hfl-content-1.0.zip`), which should be compatible with any AEM version, via CRX package manager.