# Generator for Comments, Users, Complaints, Likes, Ratings and Blacklist entries

Start the demodata generator webapp with:

```
mvn tomcat7:run
```

Generate comments, user, complaints, likes, ratings and Blacklist entries for a tenant, e.g. 'media', with:

```
http://media.localhost:40088/es-demodata-generator-webapp/servlet/generate
```

The following parameters are available:

`interval` (optional): Defines frequency of data generation in seconds. Default interval is 30 seconds. Can be applied
only on startup or after restart.

`stop` (optional): Stops the demodata generator for the given tenant.

`tenant` (optional): Starts the demodata generator for the given tenant. The tenant will be registered, if unknown.


