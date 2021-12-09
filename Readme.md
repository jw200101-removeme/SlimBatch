##Slim Batch
This project was created out of a need of a reusable, small, scalable, reliable batch framework with portion processing support, automatic transaction management, a high-volume and high performance batch execution environment with minimal or no dependency and tiny footprint. It predates Java Batch (JSR-352) and Spring Batch, after the early draft of JSR-352, configuration file tags and a couple of class names were changed and some enhancements were made to resemble more close to JSR-352. 

Any of the newer JDKs can be used; it was created using Java 1.4, and compiles with a minimum of JDK 1.4. It doesn’t use any of the new features of modern JDKs. 

Note: The use of StringBuffer is kept just to make it usable with JDK 1.4, since it is only used for debugging XML configuration files, and not referenced while executing jobs. 

It compiles to only 72KB in size.  

The only dependency it has is commons-logging, and you can use any of logging framework.

##Usage		
The configuration file is very similar to JSR-352, have a look at the schema and test examples.

Each job can consist of steps, chunk, or single task, and for each item, a reader, processor and writer are executed within a transaction that manages the step or chunk.  It’s very flexible it permits defining any type of reader, writer, processor and partition mapper. 

