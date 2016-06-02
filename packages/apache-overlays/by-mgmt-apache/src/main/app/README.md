#Apache Configuration

##Directory Structure

Each directory corresponds to an Apache virtual host, with the only exception of the ```global``` directory
which contains some configuration files shared across multiple virtual hosts.


    virtual-host-1
      virtual-host-1.conf
      logging.inc
      rewrite.inc
    virtual-host-1
      virtual-host-2.conf
      logging.inc
      rewrite.inc
    global


##Virtual Hosts


* perfectchef-preview (HTTP and HTTPS)

    PerfectChef site preview (Preview CAE Backend)

* corporate-preview (HTTP only)

    Corporate site preview (Preview CAE Backend)

* commerce-shop-preview (HTTP and HTTPS)

    Preview for WCS rendered pages within the PerfectChef site (WCS Backend)

* editor (HTTP only)

    Site Manager webstart

* studio (HTTP and HTTPS)

    Studio REST server (Studio Tomcat and Preview CAE Backend)

* webdav (HTTP and HTTPS)

    WEBDAV server

* perfectchef-live (HTTP and HTTPS)

    PerfectChef site delivery (Delivery CAE Backend)

* corporate-live (HTTP and HTTPS)

    Corporate site delivery (Delivery CAE Backend)

* commerce-shop-live (HTTP and HTTPS)

    Delivery for WCS rendered pages: Aurora Sites and pages within the PerfectChef site (WCS Backend)

##Common Include Files

* proxy.cae.inc

    Proxy rules to pass requests from the CAE virtual hosts to the Blueprint Webapp within the CAE backend, rewriting the Cookie Path from /blueprint to /

* proxy.commerce-shop.inc

    Proxy rules to pass requests from the WCS virtual hosts to the IBM WCS backend

* proxy.commerce-shop-cae.inc

    Proxy rules to pass requests from the WCS virtual hosts to the CAE backend.



