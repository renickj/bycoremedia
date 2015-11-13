##Vagrant Box Application Links

We are using [XIP IO](http://www.xip.io) for generic dns resolution of the tenant sites. The xip.io service is free of charge
by the [37signals](http://www.s7signals.com) the founders of Ruby on Rails.

### Logging & monitoring

* psdash : [Process Dashboard](http://localhost:8999)

### Content-Management-Server:

* IOR : http://blueprint-box:41080/coremedia/ior

* JMX : `service:jmx:rmi://blueprint-box:41098/jndi/rmi://blueprint-box:41099/jmxrmi`

### Workflow-Server:

* IOR : http://blueprint-box:43080/workflow/ior

* JMX : `service:jmx:rmi://blueprint-box:43098/jndi/rmi://blueprint-box:43099/jmxrmi`

### Master-Live-Server:

* IOR : http://blueprint-box:42080/coremedia/ior

* JMX : `service:jmx:rmi://blueprint-box:42098/jndi/rmi://blueprint-box:42099/jmxrmi`

### Studio:

* Studio PerfectChef via [xip.io](https://studio-helios.192.168.252.100.xip.io) or by [alias](https://studio-helios.blueprint-box)

* Studio Corporate via [xip.io](https://studio-corporate.192.168.252.100.xip.io) or by [alias](https://studio-corporate.blueprint-box)

* Swing Editor via [xip.io](http://editor.192.168.252.100.xip.io) or by [alias](http://editor.blueprint-box)

* WebDav via [xip.io](https://webdav.192.168.252.100.xip.io) or by [alias](https://webdav.blueprint-box)

* Preview PerfectChef via [xip.io](http://preview-helios.192.168.252.100.xip.io) or by [alias](http://preview-helios.blueprint-box)

* Preview Corporate via [xip.io](http://preview-corporate.192.168.252.100.xip.io) or by [alias](http://preview-corporate.blueprint-box)

* JMX : `service:jmx:rmi://blueprint-box:40098/jndi/rmi://blueprint-box:40099/jmxrmi`

### CAE Feeder:

* JMX (Preview) : `service:jmx:rmi://blueprint-box:46098/jndi/rmi://blueprint-box:46099/jmxrmi`

* JMX (Live) : `service:jmx:rmi://blueprint-box:47098/jndi/rmi://blueprint-box:47099/jmxrmi`

### Delivery:

* Live PerfectChef via [xip.io](http://helios.192.168.252.100.xip.io) or by [alias](http://helios.blueprint-box)

* Live Corporate via [xip.io](http://corporate.192.168.252.100.xip.io) or by [alias](http://corporate.blueprint-box)

* JMX : `service:jmx:rmi://blueprint-box:49098/jndi/rmi://blueprint-box:49099/jmxrmi`

### Solr Master:

* [Admin UI](http://blueprint-box:44080/solr/)

* JMX : `service:jmx:rmi://blueprint-box:44098/jndi/rmi://blueprint-box:44099/jmxrmi`

### Solr Slave:

* [Admin UI](http://blueprint-box:45080/solr/)

* JMX : `service:jmx:rmi://blueprint-box:45098/jndi/rmi://blueprint-box:45099/jmxrmi`

### MongoDB:

* [REST](http://blueprint-box:28017)