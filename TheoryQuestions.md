
# Theory Questions

## Task 1 - Theory Questions

### (1.6.1.) Annotation vs. XML Declarations

_In the previous tasks you already gained some experiences using annotations and XML. What are the benefits and drawbacks of each approach? In what situations would you use which one? Hint: Think about maintainability and the different roles usually involved in software projects._

#### JPA Annotations:

- Benefits:
  - __Simplicity:__ Easier to read/write/understand for JAVA developers
  - __Less Configuration Overhead:__ More concise, integrated directly into model classes, no separate configuration files
  - __Type Safety:__ Written in JAVA, compiler might detect more errors

- Drawbacks:
  - __Mixing Concerns:__ DB schema definition mixed with model classes
  - __Less Flexibility:__ Some complex relations cannot be expressed with annotations
  - __Maintainability:__ Can be cumbersome when multiple developers/db designers have to work in class files
  - __Java Experience Required:__ Not every db designer might be comfortable writing a lot of Java

- When to Use:
  - __Rapid Development:__ Small/medium-sized projects, simplicity & rapid development
  - __Simple Schemas:__ Schema is straightforward
  
#### JPA XML

- Benefits:
  - __Separation of Concerns:__ XML configuration files provide separation, better organization, people can work independently on application code and db schema.
  - __No Java__: Simpler syntax, db designers don't need to know Java
  - __Flexibility:__ Greater flexibility, for complex mappings & customizations
  - __External Configuration:__ Config files easily modified without recompiling, no need to touch the application code

- Drawbacks:
  - __Readability:__ Verbose and complex, made for machines
  - __Learning Curve:__ Little documentation and examples, no discoverability of options/functionality (eg view definition)
  - __Developer Experience:__ Annoying syntax, potential errors, less IDE support
  - __Maintainability:__ Manual synchronization between model classes and schema

- When to Use:
  - __Large Projects:__ Large-scale projects, complex schemas, many developers
  - __Customization:__ When Java annotations do not offer all features required


### (1.6.2.) Entity Manager and Entity Lifecycle
_What is the lifecycle of a JPA entity, i.e., what are the different states an entity can be in? What EntityManager operations change the state of an entity? How and when are changes to entities propagated to the database?_

#### Entity States
  1. __New/Transient:__ Not associated with an EntityManager (yet), not persisted to the db
  2. __Managed:__ Tracked by the EntityManager, changes are synced with db
  3. __Detached:__ EntityManager session ended, or object manually detached from EntityManager, changes are not tracked/synced anymore
  4. __Removed:__ Entity was requested to be removed from the db, no changes are tracked, will be deleted from the db

#### State Changes
  - `persist()` Tracks a transient entity: transient -> managed
  - `merge()` Updates the db from a provided entity and returns a new managed one: transient -> managed* (*original entity is still untracked)
  - `detach()` Un-tracks a managed entity: managed -> detached
  - `remove()` Marks an entity to be removed: managed -> removed (ignores transient, throws on detached entities)
  
#### Database Syncing
  - When calling `flush` manually
  - `FlushModeType.AUTO`: Automatic detection when entities are written to, als during transaction
  - `FlushModeType.COMMIT`: Only when transaction is committed
 

### (1.6.3.) Optimistic vs. Pessimistic Locking
_The database systems you have used in this assignment provide different types of concurrency control mechanisms. Redis, for example, provides the concept of optimistic locks. The JPA EntityManager allows one to set a pessimistic read/write lock on individual objects. What are the main differences between these locking mechanisms? In what situations or use cases would you employ them? Think of problems that can arise when using the wrong locking mechanism for these use cases._

#### Optimistic Locking
- __Mechanism:__ Assumes that conflicts between transactions are rare. Updating/Writing transactions:
  - Reads the record's current state
  - Before committing changes, check if the record has been modified by another transaction since reading
  - Eg. comparing version number or timestamp
  - Do the update if no conflicts are found, otherwise abort and retry
  
- __Use Cases:__ Infrequent conflicts, low cost of conflict resolution, for environments with low contention for resources & short transactions

- __Potential Problems:__ Many aborts and retries when record is used in parallel. Lost updates, if transaction is not retried

#### Pessimistic Locking
- __Mechanism:__ Assumes that conflicts are more likely. Ensures that only one transaction can access a resource. Updating/Writing transactions:
  - Tries to acquire lock on the record, or wait
  - Prevents other transactions from accessing/modifying, until lock is released
  - Read locks (shared locks) & Write locks (exclusive locks)

- __Use Cases:__ Common conflicts, high cost of conflict resolution, for environments with high contention for resources & long transactions

- __Potential Problems:__ Less concurrency & more contention due to waiting. Danger of dead-locks

#### Consequences of Wrong Locking Mechanism
- __Bad Performance:__ Pessimistic locking in low-contention environments, unnecessary lock contention & less concurrency

- __Concurrency Problems:__ Optimistic locking in high-contention environments, high abort rates & danger of lost updates

- __Deadlocks:__ Transaction might need each others locked resources


### (1.6.4.) Database Scalability
_How can we address system growth, i.e., increased data volume and query operations, in databases? Hint: vertical vs. horizontal scaling. What methods in particular do MongoDB and Redis provide to support scalability?_

#### Database Scaling
- Vertical Scaling (Scale-Up)
  - Increase capability of a single server
  - More CPU, RAM & storage
  - Practical & Economic limits

- Horizontal Scaling (Scale-Out)
  - Add more servers & distribute workload
  - More cost-effective and flexible
  - Software must be capable of distribution (eg. H2 is not)

#### MongoDB
- Indexes improve query performance as records can be found faster
- Horizontal:
  - Data split across "shards", each shard is an independent db
  - Query router redirects queries to appropriate shard -> Improves read & write performance by distributing the workload over the shards

#### Redis
- Specialized data structures: Lists, Hashmaps, Sets,...
- Horizontal:
  - Replication via replicas for improved read performance, data can be read from primary or replica
  - Partitioning via clusters for improved write performance, data is split over multiple nodes which hold a subset of the data


## Task 2 - Theory Questions

### (2.4.1.) Java Transaction API
_Consider the match method and how you would handle things different if you were to implement it with pessimistic or optimistic locking._

I used pessmitistic locking
- Simpler to impement -> no redoing or error checking necessary
- Getting lock probably easy, only one user accesses their own trip

If I were to use optimistic locking:
- Use JPA optimistic locking mode
- Handle `StaleObjectStateException` or `OptimisticLockException` by retrying -> transaction is rolled back
- Call method again (by proxy to start new transaction) or mark as `@Retryable(StaleStateException.class)`


### (2.4.2.) Remoting Technologies
_Compare gRPC remoting and Web services. When would you use one technology, and when the other? Is one of them strictly superior? How do these technologies relate to other remoting technologies that you might know from other lectures (e.g., Java RMI, or even socket programming)?_

#### gRPC vs Web Services
- gRPC
  - (+) Supports streaming
  - (+) Strongly typed with checks
  - (~) Binary Serialization: efficient, not human readable
  - (-) Requires code generation
  - (-) Additional work to write contracts and keep uptodate
  - (-) More refactoring work


- Web Service
  - (+) Interoperability/Wide spread use (often common denominator)
  - (+) Same technology compared as web server code (eg. frontend stuff)
  - (~) Text encoding: inefficient, human readable
  - (-) No type guarantees
  - (-) More manual un/marshalling work (read data from path, query or body)

Both have their usecases. Web services are simpler, and more widely spread. gRPC is more efficient and allows implementing stronger contracts and more complex features like streaming.

#### Other 
- Java RMI: Only compatible with Java
- Socket programming: Very low level, maximum flexibility, high degree of wheel-re-invention


### (2.4.3.) Class Loading
_Explain the concept of class loading in Java. What different types of class loaders exist and how do they relate to each other? How is a class identified in this process? What are the reasons for developers to write their own class loaders?_

Class loading
- Loads classes into the JVM memory for execution
- Classes are loaded lazily as they are needed
- Locates the class, parses & verifies binary class format, links class together, initializes class fields

There are three types of class loaders
1. __Bootstrap Class Laoder:__ For core JRE libraries during startup located in `jre/lib`
2. __Extension Class Loader:__ For third-party JRE extension libraries
3. __System Class Loader:__ For user classes on the current class path

Classes are located by their fully qualified name
- Package name is converted into a directory path
- JAR files keep the directory structure (just ZIP files)

Custom class loading can be done for:
- __Security:__ Restrict or check access to certain classes
- __Dynamic loading:__ Load classes in different order, eg. more eagerly to prevent latency issues
- __Versioning:__ Allow picking from a specific version of a class as necessary
- __Special Storage:__ Load classes from non-standard locations like databases, network or encrypted storage
- __JIT compilation:__ Load dynamically generated bytecode on-the-fly


### (2.4.4.) Weaving Times in AspectJ
_What happens during weaving in AOP? At what times can weaving happen in AspectJ? Think about advantages and disadvantages of different weaving times._

Weaving integrates the code from the aspect files into the target classes and methods. This can be done during:
- __Compile Time (CTW):__ Rewrites the original class during compile
  - (+) Allows for compiler to optimize code
  - (+) No run-time or load-time overhead
  - (-) Less flexible
  - (-) Application needs to be re-compiled when ascpect changes

- __Load Time (LTW):__ Injects byte code into compiled class files
  - (+) Useful for libraries where source code is not available
  - (+) More flexible: eg. apply aspects based on config files on startup
  - (-) Load-time overhead
  - (-) Additional complexity for the class loader
  - (-) Possibility of weaving errors during startup

- __Run Time (RTW):__ Patch bytecode dynamically or create proxy classes
  - (+) Maximum flexibility
  - (+) Dynamically create/remove aspects
  - (-) Bad performance
  - (-) Not supported by AspectJ

