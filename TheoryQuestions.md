
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
