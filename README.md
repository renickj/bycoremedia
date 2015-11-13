#CoreMedia Blueprint

## Documentation

[Manual](https://documentation.coremedia.com/cm8/current/manuals/coremedia-en/webhelp/content/ch04.html)

## Structure

The workspace is separated into four major directory hierarchies:

* The `modules` folder contains all library- and application-modules.

* The `packages` folder contains only deployment specific resources and modules. No compilation is done here.

* The `boxes` folder contains the infrastructure specific resources. No application packaging is done here.

* The `test-data` folder contains test content and test user definitions. Currently the test content is packaged by 
the boxes module.  Extensions bring their own content, also in directories named 'test-data'.  The name test-data and
the two subdirectories content and users are fix and essential for our build and deployment processes.

## Vagrant Chef Setup

By moving all required services into a virtual environment, the Vagrant Chef Setup reduces the amount of 
installation prerequisites to start developing to a minimum that should not conflict with any other projects you are 
working on. See the "Getting Started" > "Prerequisites" section of the "LiveContext for IBM WebSphere Manual" in the CoreMedia
[documentation](https://documentation.coremedia.com/dxp8/overview/).

* A short quickstart guide for the Vagrant Chef Setup, is described [here](./VAGRANTSETUP.md)

* An overview of all application links, is available [here](./OVERVIEW.md). 

## Updating the Workspace

CoreMedia provides this dedicated [DXP8 Blueprint GitHub mirror repository](https://github.com/coremedia-contributions/dxp8-blueprint) for customers and partners.

Simply use GitHubs web frontend to visually compare changes between release versions. Each release is aggregated in a [single git commit](https://github.com/coremedia-contributions/dxp8-blueprint/commits/master).

CoreMedia heavily encourages you to use one of the following approaches:

### Updating via Git

Instead of extracting the ZIP archive from the CoreMedia download site, you can simply use Git to fetch updates and merge them with your own customizations.

### Updating via Patch files

Although CoreMedia recommends to use Git, you can keep using your favorite source code management system by applying release changes patch by patch.

For example, DXP8 Release 16 (7.5.16-10) changes are visible in https://github.com/coremedia-contributions/dxp8-blueprint/commit/880ca46804e20e85214f80a7cb4efc19a57f8024

Simply add ``.patch`` to the commit URL to be able to download in patch format (hidden GitHub feature).

* [DXP8 R16 patch](https://github.com/coremedia-contributions/dxp8-blueprint/commit/880ca46804e20e85214f80a7cb4efc19a57f8024.patch)
* DXP8 R17 patch: use ``git format-patch`` (see below)
* [DXP8 R18 patch](https://github.com/coremedia-contributions/dxp8-blueprint/commit/5a9a845380be6a8c363318b6c600f1b20b78d4c1.patch)
* [DXP8 R19 patch](https://github.com/coremedia-contributions/dxp8-blueprint/commit/e20f53c99c919f6a9bbadd6bd46aa58ace36e863.patch)
* [DXP8 R20 patch](https://github.com/coremedia-contributions/dxp8-blueprint/commit/628c1ba8fb9b49412d4f34b57b0dbe83005e2352.patch)
* [DXP8 R21 patch](https://github.com/coremedia-contributions/dxp8-blueprint/commit/c9fdb3a7c9da52a9e96398c85d106d9936bf495c.patch)
* [DXP8 R22 patch](https://github.com/coremedia-contributions/dxp8-blueprint/commit/83972227587a231acad06495c0199e7cd0db6a85.patch)
* [DXP8 R23 patch](https://github.com/coremedia-contributions/dxp8-blueprint/commit/5b9c331948c1c51b724a9e37c9a1979520a78e1d.patch)
* [DXP8 R24 patch](https://github.com/coremedia-contributions/dxp8-blueprint/commit/0c9f72e85d89ac84c406f9b244c0139af0cd9259.patch)

In some cases GitHub won't generate the patch (e.g. `error: too big or took too long to generate` or `Content containing PDF or PS header bytes cannot be rendered from this domain for security reasons.`).

Use ``git format-patch -1 <commit>`` on the command-line as a workaround (<http://git-scm.com/docs/git-format-patch>).

* DXP8 R17 patch: ``git format-patch -1 27bd53c`` generates the file ``0001-7.5.17-8.patch`` (918 MB)

Please contact [support@coremedia.com](mailto:support@coremedia.com) if you need further assistance!
