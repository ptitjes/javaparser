Todo List
=========

Source Model
------------

OK - resolve types in executables' signatures and variables

- attaching attributes to statements and expressions

- add a phase to check for
  - inherited abstract methods not implemented
  - invalid members (static, with body, ...) in interface
  - final variables not initialized
  - thrown types not extending java.lang.Throwable
  - ...

- expressions' type assignment and inference

Binary Model
------------

- add tests

User Helpers
------------

- implementation of Types and Element helpers
- cross-indexing

Finishing Works
------------

- add checks at various places according to JLS
