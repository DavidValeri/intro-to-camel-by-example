Intro to Camel By Example
=============================

This project demonstrates a complete Camel application for a simple use case.  There are two flavors of the application: 1) A "simple" implementation that just solves the problem and 2) a "scalable" implementation that shows how little has to change in a Camel route in order to scale an application up.  The applications are written to follow Camel best practices, include unit test examples, and are deployable as a WAR in Tomcat or through the Maven Cargo plug-in.

Requirements:

* Maven 2.2.1 or 3.0 (http://maven.apache.org/)
* Java SE 6
* Apache ActiveMQ 5.5.1+

## The Use Case

### Basic Requirements

* I need to poll a folder for new files
    * When I find a file I need to:
        * parse it,
        * validate it,
        * decompose it,
        * and move it to another folder
    * After I decompose the file I need to:
        * trigger a business process on the decomposed element and store the decomposed element

### Advanced Requirements

* No XML files or records within a file can go unprocessed
* Must scale past a single physical node

## Branches

This example contains two branches.  The "master" branch contains a full implementaiton and will work out-of-the-box.  The "start" branch contains boilerplate code, build configuration files, and implementation instructions for those that wish to explore implementing the example on their own.

## Building

To build

    mvn clean install

## Running from the command line

### Running the "simple" solution

The simple solution is a minimal solution to the use case that does not provide the ability to scale the application beyond a single node.  This solution is representative of the initial release of an application that could be acheived in a very short time period.

To run the simple solution, navigate to

    <EXAMPLE_ROOT>/simple-ingest-app
    
then execute the following command

    mvn package cargo:run
    
An embedded Tomcat will launch with the application automatically deployed.  The application will look for input files in

    ~/camel-by-example/source
    
The application log file output will write to 

    <EXAMPLE_ROOT>/simple-ingest-app/target/cargo/logs/container.log
      
To test the application, copy the files from

    <EXAMPLE_ROOT>/demo-data
    
to

    ~/camel-by-example/source

The application hosts a web enabled database browser that you can use to examine the results of the application execution.  Use a browser to navigate to [http://localhost:8082](http://localhost:8082).  When the login prompt appears, enter "*jdbc:h2:mem:test*" into the JDBC URL field and click the *Connect* button.

### Running the "scalable" solution

The scalable solution is a more robust solution to the use case that does provides the ability to scale the application beyond a single node.  This solution is representative of a second relelease of an application and demonstrates how Camel allows you to modify and scale a route with relative ease.

Before running the scalable solution, start an ActiveMQ broker instance on your machine using the default configuration settings (port 61616).

    <AMQ_HOME>/bin/activemq console

To run the file polling portion of the solution, navigate to

    <EXAMPLE_ROOT>/scalable-ingest-app
    
then execute the following command

    mvn package cargo:run
    
An embedded Tomcat will launch with the application automatically deployed.  The application will look for input files in

    ~/camel-by-example/source
    
The application log file output will write to 

    <EXAMPLE_ROOT>/scalable-ingest-app/target/cargo/logs/container.log
      
To test the application, copy the files from

    <EXAMPLE_ROOT>/demo-data
    
to

    ~/camel-by-example/source
    
At this point, the file polling portion of the solution will have parsed the input files and enqueud messages onto the message broker.  However, you still need to start the message processing portion of the scalable application in order to processe the enqueued messages.

To run the message processing portion of the solution, navigate to

    <EXAMPLE_ROOT>/scalable-process-app
    
then execute the following command

    mvn package cargo:run
    
The application log file output will write to 

    <EXAMPLE_ROOT>/scalable-process-app/target/cargo/logs/container.log

The application hosts a web enabled database browser that you can use to examine the results of the application execution.  Use a browser to navigate to [http://localhost:8082](http://localhost:8082).  When the login prompt appears, enter "*jdbc:h2:mem:test*" into the JDBC URL field and click the *Connect* button.

You can examine the health of the different portions of the application and the ActiveMQ broker by using JMX.  Jconsole, a JMX user interface provided with your JVM is the easiest way to access this information.

To launch JConsole type

    jcsonole
    
You can connect to the local running processes with the following names

* Cargo hosted Tomcat - org.apache.catalina.startup.Bootstrap start
* ActiveMQ - run.jar start

Examine the available MBeans in the deployed applications and ActiveMQ to examine perofrmance and health metrics about the application and the message broker.
