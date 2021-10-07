# Maintain estate hub frontend

This service is responsible for navigating the user to maintain various aspects of their estate registration. It acts as the 'hub' which each journey branches off of.

To run locally using the micro-service provided by the service manager:

***sm --start ESTATES_ALL -r***

If you want to run your local copy, then stop the frontend ran by the service manager and run your local code by using the following (port number is 8828 but is defaulted to that in build.sbt).

`sbt run`

### License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html").
