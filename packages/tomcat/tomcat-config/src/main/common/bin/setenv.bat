set JAVA_HOME=@CM_JAVA_HOME@
set CATALINA_OPTS=

:::::::::::::::::::::::
:: Memory settings
:::::::::::::::::::::::

::: set heap size
:: Important: Set -Xms = -Xmx
set CATALINA_OPTS=%CATALINA_OPTS% -Xms@TOMCAT_HEAP@ -Xmx@TOMCAT_HEAP@

:: set stack size to 256k, would be 1m per default, which is only necessary for the Content Management Server Production
set CATALINA_OPTS=%CATALINA_OPTS% -Xss256k

:: configure garbage collection
set CATALINA_OPTS=%CATALINA_OPTS% -XX:+UseConcMarkSweepGC -XX:+UseParNewGC

:: configure perm space. 128m is probably much more than we will ever need.
:: Important: Set -XX:PermSize = -XX:MaxPermSize
set CATALINA_OPTS=%CATALINA_OPTS% -XX:PermSize=@TOMCAT_PERM@ -XX:MaxPermSize=@TOMCAT_PERM@

:: tell Tomcat to release the BodyContent buffer when BodyContentImpl.clear() is called.
set CATALINA_OPTS=%CATALINA_OPTS% -Dorg.apache.jasper.runtime.BodyContentImpl.LIMIT_BUFFER=true

:: see http://bugs.sun.com/view_bug.do?bug_id=6822370
:: still not fixed in 1.6.0_24!
:: no sign of this problem on single processor machines, but
:: on a multi-processor-multi-core sparc system JVMs will grind to a halt on a regular basis with Java locking.
:: on architectures where -XX:+UseMembar does not help to avoid such deadlocks, set hox.util.use.java.util.concurrent=false
set CATALINA_OPTS=%CATALINA_OPTS% -XX:+UseMembar -Dhox.util.use.java.util.concurrent=@TOMCAT_USE_JAVA_UTIL_CONCURRENT@
: it supposedly also fixes the problem.

:: Unload classes that are not needed any more from PermSpace.
:: Fixes problem java.lang.OutOfMemoryError: PermGen (especially when running multiple webapps in parallel)
:: Both parameters are needed in Java 5 to work correctly. Java 6 prints to STDOUT that CMSPermGenSweepingEnabled is not needed any more,
:: but I couldn't find any proof of that in the documentation.
set CATALINA_OPTS=%CATALINA_OPTS% -XX:+CMSClassUnloadingEnabled

:::::::::::::::::::::::
:: Debug settings
:::::::::::::::::::::::
:: on OutOfMemoryErrors, you may want to have a heapdump to debug the error
::set CATALINA_OPTS=%CATALINA_OPTS% -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=@LOG_ROOT@/@APPLICATION_NAME@/

:: relevant for threaddumps only, helps with debugging problems
::set CATALINA_OPTS=%CATALINA_OPTS% -XX:+PrintClassHistogram

:: log garbage collection actions, for debugging only, produces LOTS of output!
::set CATALINA_OPTS=%CATALINA_OPTS% -verbose:gc -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -Xloggc:@LOG_ROOT@/@APPLICATION_NAME@/gc.out

:: enable assertions
::set CATALINA_OPTS=%CATALINA_OPTS% -ea:com.coremedia... -ea:hox...

:: successful name lookups from the name service should not be cached
set CATALINA_OPTS=%CATALINA_OPTS% -Dsun.net.inetaddr.ttl=0

:: set remote debug options
::set CATALINA_OPTS=%CATALINA_OPTS% -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=@TOMCAT_DEBUG_PORT@

:: ORB settings
set CATALINA_OPTS=%CATALINA_OPTS% -Dcom.sun.CORBA.transport.ORBUseNIOSelectToWait=false
set CATALINA_OPTS=%CATALINA_OPTS% -Dcom.sun.CORBA.ORBServerHost=@TOMCAT_ORB_HOST@
set CATALINA_OPTS=%CATALINA_OPTS% -Dcom.sun.CORBA.ORBServerPort=@TOMCAT_ORB_PORT@
set CATALINA_OPTS=%CATALINA_OPTS% -Dcom.sun.CORBA.legacy.connection.ORBSocketFactoryClass=@TOMCAT_ORB_SOCKET_FACTORY@

::::::::::::::::::::::::
:: JMX settings
::::::::::::::::::::::::
set CATALINA_OPTS=%CATALINA_OPTS% -Djava.rmi.server.hostname=@TOMCAT_JMX_PUBLIC_SERVER_HOSTNAME@
set CATALINA_OPTS=%CATALINA_OPTS% -Dcom.sun.management.jmxremote.ssl=@TOMCAT_JMX_SSL@
set CATALINA_OPTS=%CATALINA_OPTS% -Dcom.sun.management.jmxremote.authenticate=@TOMCAT_JMX_AUTHENTICATE@
set CATALINA_OPTS=%CATALINA_OPTS% -Dcom.sun.management.jmxremote.password.file=%CATALINA_BASE%/conf/jmx-password.txt
set CATALINA_OPTS=%CATALINA_OPTS% -Dcom.sun.management.jmxremote.access.file=%CATALINA_BASE%/conf/jmx-access.txt


:::::::::::::::::::::::::
:: Extra CATALINA_OPTS for automation with Chef
:: This is a hook to add additional CATALINA_OPTS, it will read only the first line of the file and append it to CATALINA_OPTS
:::::::::::::::::::::::::
if exist %CATALINA_BASE%/bin/catalina_opts (
  set /p CATALINA_DEBUG_OPTS=<%CATALINA_BASE%/bin/catalina_opts
  set CATALINA_OPTS=%CATALINA_OPTS% %CATALINA_DEBUG_OPTS%
)



ECHO %CATALINA_OPTS%

:::::::::::::::::::::::
:: Information
:::::::::::::::::::::::
:: JVM Options explained
:: 1. http://blogs.sun.com/watt/resource/jvm-options-list.html
:: 2. http://www.oracle.com/technetwork/java/javase/tech/vmoptions-jsp-140102.html
::
:: 3. http://www.oracle.com/technetwork/java/javase/tech/index-jsp-136373.html
:: more information about GarbageCollection settings
:: 4. http://www.oracle.com/technetwork/java/gc-tuning-5-138395.html
:: 5. http://java.sun.com/docs/hotspot/gc1.4.2/faq.html
:: 6. http://blog.dynatrace.com/2011/03/24/the-impact-of-garbage-collection-on-java-performance/
:: 7. http://blog.dynatrace.com/2011/05/11/how-garbage-collection-differs-in-the-three-big-jvms/
