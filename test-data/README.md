#CoreMedia Blueprint

##Example Content and Users

This folder is the place where you can put content that should be imported automatically, when using the virtualization
infrastructure with Vagrant and VirtualBox. Place content below the "content" folder and a "users.xml" below the "users"
folder to import predefined users.

When you build the "boxes" module the content of this folder will be packaged into a content-users.zip for automatic
provisioning. You can override the maven property "blueprint.testdata.dir" to an absolute path using either:

* a Maven profile in the project.
* a Maven profile in your Maven settings.xml.
* a Java system property on the command line.

The "boxes" module also creates zip files for the example content of the extensions.  If you deactivate an extension,
you can spare some build time by deleting the according assembly-plugin executions in the "boxes" pom.
