
## Assignment 1

The feedback from assignment 1 was incorporated into the codebase.

## Assignment 2

The feedback from assignmnet 2 was ignored due to time limitations. The following
point deductions were made:

- 2.1.1.1_08: -1 (Illegal use of ass1-trip as dependency to import Itrip)

- 2.1.2_01: -1,5 service definition should be correct (Unnecessary boolean in AuthenticationResponse, java_package and package don't match which leads to a build failure. Was fixed to continue grading.)

- 2.3.3_03: -0,5 Plugin callbacks should be executed in separate threads (Either use a new Timer instance for each aspect or utilize threads to ensure that the plugin callbacks are executed in separate threads.)

I do not agree with the last point.
