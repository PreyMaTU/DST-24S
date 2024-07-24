Distributed Systems Technologies
================================

This is my submission for the DST course at TU Wien from the semester 2024S. Checkout the `TheoryQuestions.md` file for my answers on the theory questions each exercise. As it was not strictly necessary to address all the feedback that I received during grading of the tasks in the codebase, some commentary on the deductions received can be found in `Feedback.md`.

[The repository][old_repo] of last years course by [@flofriday] was very helpful, which is why I also want to contribute my own submission to the [VOWI].

Build
-----

Each Maven module has a corresponding profile that contains the build config for the respective module.

> [!IMPORTANT]
> Make sure to use Java 11, especially when working with Apache Flink!

For example, to build the JPA task of assignment 1, run
```bash
    mvn clean install -Pass1-jpa
```
You can add the `-DskipTests` flag to skip JUnit test execution.

There is also a profile `all`, that includes all modules.
You should activate this profile in your IDE's Maven ([IDEA],[Eclipse]) configuration!



[IDEA]: https://www.jetbrains.com/help/idea/maven-support.html
[Eclipse]: http://www.eclipse.org/m2e/documentation/release-notes-15.html#new-maven-profile-management-ui
[old_repo]: https://github.com/flofriday/DST
[@flofriday]: https://github.com/flofriday
[VOWI]: https://vowi.fsinf.at/wiki/TU_Wien:Distributed_Systems_Technologies_VU_(Morichetta)
