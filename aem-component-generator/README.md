# AEM Component Generator by Bounteous

AEM Component Generator is a java project that enables developers to generate the base structure of an
AEM component using a JSON configuration file specifying component and dialog properties and other configuration
options.

Generated code includes:
- `cq:dialog` for component properties
    - `dialogshared`/`dialogglobal` for shared/global component properties
    - Supports all basic field types, multifields, and image upload fields
- Sling Model
    - Includes fully coded interface and implementation classes
    - Follows WCM Core component standards
    - Enables FE-only development for most authorable components
- HTL file for rendering the component
    - Includes an object reference to the Sling Model
    - Includes the default WCM Core placeholder template for when the component is not yet configured
- Stubbed clientlib (JS/CSS) following component client library patterns of WCM Core 

This feature is now officially part of
[Adobe Open Source](<https://github.com/adobe/aem-component-generator>).