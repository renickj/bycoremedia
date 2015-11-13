watchdog definition

  <!-- ================================================================== -->

  <!-- Probe CoreMedia WebDAV Server -->
  <Component name="ProbeWebDAVServer" startAction="PWDS-HttpRequest" delay="0">
    <Http name="PWDS-HttpRequest" url="http://localhost:8001/webdav/" user="admin" password="admin" timeout="120"/>
  </Component>