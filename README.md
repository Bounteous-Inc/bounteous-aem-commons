# bounteous-aem-commons

This project is a repository of individual AEM packages freely distributed by Bounteous (previously HS2 Solutions)
to help AEM developers work around bugs, implement new features, and otherwise just "Get Sh*t Done!"

## Features

### aem-component-generator
This is a code generator that enables developers to automatically create the base structure of an
AEM component using a JSON configuration file, streamlining implementation for all developers and enabling
frontend developers to create components according to WCM Core standards (including Sling Models) without
the need to code any java. 

This feature is now officially part of
[Adobe Open Source](<https://github.com/adobe/aem-component-generator>).

### aem-dam-views
This is a feature that allows configuration of unlimited DAM folder navigation structures as a set of "views" on top
of the single true asset folder structure, catering to different business users and use cases.

The version hosted here is a demo-level feature for developers only, and is not production ready.

### aem-devops-runbook-template
This is a template to aid in creation of a Dev/Ops Runbook for an AEM platform. A runbook following this template has
been (unofficially) labeled by Adobe Managed Services as a "model runbook." 

### hotfix-msm-lockable-deep-properties
This is a hotfix to address a
[documented AEM OOTB limitation](https://docs.adobe.com/docs/en/aem/6-2/develop/extending/msm.html)
where MSM does not operate with deep properties in regards
to using the cq-msm-lockable attribute to disable inheritance when configuring MSM locks on page properties
(cq:propertyInheritanceCancelled).

### multi-site-demo
This is a demo project used for "Adobe IMMERSE 2017: Multi-Site Platforms - Setting your Codebase Up for Success."
The project demonstrates 15 coding principles for multi-site platforms.  See more details on the
[Adobe community blog](http://blogs.adobe.com/contentmanagement/2017/04/24/aem-multi-site-tips-tricks-preview-immerse-2017).

### on-deploy-scripts-framework
This is a framework to allow developers to create and leverage one-time scripts that run on an AEM server
upon deploy of a code bundle. See the project documentation for use cases and reasons why this can be a huge
time saver for your AEM team!

This feature is now offically part of
[ACS AEM Commons](https://adobe-consulting-services.github.io/acs-aem-commons/features/on-deploy-scripts/index.html).

### cmreactor
A standalone, portable bash script to merge multiple AEM Cloud Service project sources 
into a single-commit git branch with a generated reactor pom.xml file and to force-push to a
Cloud Manager remote git repository.
