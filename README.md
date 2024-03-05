Distributed Systems Technologies
================================

Please find details about the template in the assignment document.

Build
-----

Each Maven module has a corresponding profile that contains the build config for the respective module.
For example, to build the JPA task of assignment 1, run

    mvn clean install -Pass1-jpa

You can add the `-DskipTests` flag to skip JUnit test execution.

There is also a profile `all`, that includes all modules.
You should activate this profile in your IDE's Maven ([IDEA],[Eclipse]) configuration!

   [IDEA]: https://www.jetbrains.com/help/idea/maven-support.html
   [Eclipse]: http://www.eclipse.org/m2e/documentation/release-notes-15.html#new-maven-profile-management-ui
