# Hotfix for MSM Inheritance Breaking for Deep Properties by Bounteous (previously HS2 Solutions)

This is a hotfix to address a
[documented AEM OOTB limitation](https://docs.adobe.com/docs/en/aem/6-2/develop/extending/msm.html)
where MSM does not operate with deep properties in regards
to using the cq-msm-lockable attribute to disable inheritance when configuring MSM locks on page properties
(cq:propertyInheritanceCancelled).

## Contents

- Node update event listener OSGi service (PropagatePropertyInheritanceCancelled)
- OSGi configuration mapping the service user used by the listener to make required node property updates

## Prerequisites

Because the event listener requires the ability to update JCR nodes, your AEM server will need a service user named
"content-update-service" that has "write" permissions to the /content path in the JCR.

To create a service user:

- Go to http://(your AEM server):4502/crx/explorer/index.jsp
- Log in as `admin`
- Click "User Administration"
- Click "Create System User"
- Set values:
    - UserID: `content-update-service`
    - Intermediate Path: `system`
- Click the green checkmark to create the user
- Close
- Go to http://(your AEM server):4502/useradmin
- Find the `content-update-service` user
- Open the `Permissions` tab and grant `Read|Modify|Create` permissions on `/content`
- Click `Save`

This user must be present before installing the package, so that the event listener can correctly initialize.

If you already have a service user that you wish to use instead of creating a new service user, you can deploy your own
OSGi config mapping the service user, similar to the one included in this package, but instead referencing your own
service user rather than `content-update-service`.

## How to install

NOTE: The current release package is built against AEM 6.0 APIs and tested on AEM 6.1. For alternate AEM versions,
please submit an issue requesting a build for your version of AEM.  Or, feel free to build the project from sources,
changing the dependencies as appropriate.

1. Download the `hotfix-msm-lockable-deep-properties.ui.apps-1.0.0.zip` package under the `releases` folder
1. Upload the package to an AEM author instance via CRX package manager and install
1. There should be no need to deploy this package to a publish instance

## How to install from sources

If you have a running AEM author instance at http://localhost:4502 you can build and deploy into AEM with  

    mvn clean install -PautoInstallPackage
    
There should be no need to deploy this package to a publish instance, but if you really wish to do so then run

    mvn clean install -PautoInstallPackagePublish

## Alternatives to install

As with any code distributed freely under The MIT License (MIT), you are free to copy the event listener OSGi class
(PropagatePropertyInheritanceCancelled) and the associated OSGi configuration directly into your code base, modify as
you see fit, and deploy as part of your own code base.
