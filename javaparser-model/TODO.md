Todo List
=========

Source Model
------------

OK - resolve types in executables' signatures and variables

- introduce an ErrorTpe to set for unresolved names

- attaching attributes to statements and expressions

- add a phase to check for
  - file name not corresponding to package and class name
  - inherited abstract methods not implemented
  - constructor names not corresponding to class name
  - invalid members (default, static, with body, ...) in a type element
  - final variables not initialized
  - thrown types not extending java.lang.Throwable
  - ...

- expressions' type assignment and inference

Binary Model
------------

- add more tests
- test on class files with method debug names

Annotation Support
------------------

- implementation of annotation data structure extending jx.l.m.AnnotatedConstruct
- add instance in Elem and setters
- read annotations from binary (plus deprecated opcode flag)
- read annotations from source
- handle package sharing and annotations (dig in spec...)

User Helpers
------------

- implementation of Types and Element helpers
- cross-indexing

Finishing Works
------------

- split analysis result data from Classpath in its own class
- add checks at various places according to JLS
