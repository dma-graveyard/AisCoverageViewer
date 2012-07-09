# e-Navigation Shore Display ESD #

## Introduction ##
   
ESD (e-Navigation Shore Display) is an VST
application for demonstrating possible e-Navigation services.
   
The application is in Java and uses OpenMap(tm) for presenting geospatial
information, and as a JavaBeans(tm) component framework.

For detailed description see Wiki.

## Prerequisites ##

* JDK 1.6+ (http://www.oracle.com/technetwork/java/javase/)
* Apache Ant 1.7+ (http://ant.apache.org) or Eclipse IDE (http://eclipse.org)

## ANT targets ##

### Building ###

	ant

### Running ###

	ant run
	
### Javadoc ###

	ant javadoc
	
### Clean ###

	ant clean
	
### Dist clean ###

	ant distclean
	
Will remove the `dist` folder described below, erasing existing settings.

## Quick start ##

To do
	
## Project structure ###

	|-- build
	|-- dist
	|-- extlib
	`-- src
	    `-- main
	        |-- java
	        `-- resources

* `build` - generated directory with compiled class files
* `extlib` - third party jar files
* `src/main/java` - source root
* `src/main/resources` - Resources like images, default settings, etc.
* `dist` - a generated directory with a compiled distributable version of the application.
  The application is run from within this directory.   

## Versioning ##

The version is controlled in `build.xml` as a property. The convention is to
use the format `<major>.<minor>-<dev version>` for non-final versions, and 
`<major>.<minor>` for final releases. E.g.

	<property name="version" value="2.0-PRE1" />
	
for first pre-version of 2.0 and

	<property name="version" value="2.0" />
	
for the final version. 

Minor versions are for fixes and small improvements, while a major version is
for the introduction of new functionality. Major and minor versions are
reflected in the branching of the project. Branching should be done in the 
following way.

    -|-- * -- * -- * 2.x  (master branch version 2.x)
     |             |
     |             `-- 2.y (branch for fix or small improvement)  
     |
	 `-- * -- * -- 3.0 (branch for new major version)
         |
	     `-- dev (branch for individual task in new version)
 

## Eclipse development ##

To use Eclipse as IDE just import project. Eclipse `.project` and settings files
are included.

Launch configuration `EeINS.launch` is included, so it is possible to run as Java 
application from Eclipse. You will need to do a manual build before running.

## Contribution ##

Fork the project and make pull requests. 

Try to use the component architecture as much as possible. Implement components and 
hook up to other components with the `findAndInit` method rather than hard-wiring.
Try to follow the coding standards already used in the project and document within
the code with Javadoc comments. For more extensive documentation use the Wiki.
