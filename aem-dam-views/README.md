# AEM DAM Views

***NOTE: THIS PROJECT IS A WORK IN PROGRESS***

*The version hosted here is a demo-level feature for developers only, and is not production ready.*

AEM DAM Views severs the coupling of asset storage and asset navigation allowing the DAM structure to satisfy all users
and use cases.  The marketer can naturally navigate through asset directories organized by campaign, the sales person
by solution, and the photographer by date, all supported by a single normal DAM structure predicated on user access
permissions.

Business value provided:
- Directory navigation of assets catered to users or job functions.  No more need to pick a single storage structure
amongst competing stakeholders.
- Directories automatically generated based on assets and view rules - no need to create or modify views for new assets,
campaigns, solutions, etc.
- Easy configuration of complex, multi-level asset organization rules
- Pluggable framework that allows engineers to add new asset organization rules with limited effort
- New views can be added at any time, and thus applied to an existing DAM
- No asset duplication or added complexity to the DAM structure
- Fully supports asset access permissions


## Modules

The main parts of the project are:

* core: Java bundle containing all core functionality like OSGi services, listeners or schedulers, as well as component-related Java code such as servlets or request filters.
* ui.apps: contains the /apps (and /etc) parts of the project, ie JS&CSS clientlibs, components, templates, runmode specific configs as well as Hobbes-tests
* ui.content: contains sample content using the components from the ui.apps

## How to build

To build all the modules run in the project root directory the following command with Maven 3:

    mvn clean install

If you have a running AEM instance you can build and package the whole project and deploy into AEM with  

    mvn clean install -PautoInstallPackage
    
Or to deploy it to a publish instance, run

    mvn clean install -PautoInstallPackagePublish
    
Or alternatively

    mvn clean install -PautoInstallPackage -Daem.port=4503

Or to deploy only the bundle to the author, run

    mvn clean install -PautoInstallBundle

## Testing

There are three levels of testing contained in the project:

* unit test in core: this show-cases classic unit testing of the code contained in the bundle. To test, execute:

    mvn clean test

* server-side integration tests: this allows to run unit-like tests in the AEM-environment, ie on the AEM server. To test, execute:

    mvn clean verify -PintegrationTests

* client-side Hobbes.js tests: JavaScript-based browser-side tests that verify browser-side behavior. To test:

    in the browser, open the page in 'Developer mode', open the left panel and switch to the 'Tests' tab and find the generated 'MyName Tests' and run them.


## Maven settings

The project comes with the auto-public repository configured. To setup the repository in your Maven settings, refer to:

    http://helpx.adobe.com/experience-manager/kb/SetUpTheAdobeMavenRepository.html

## System Requirements

* Java version: Java 8
* Maven version: 3.3+
* AEM version: AEM 6.4.3+
