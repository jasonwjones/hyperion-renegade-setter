# Hyperion Renegade Setter

This project is a quick proof of concept for updating, removing, and viewing "renegade members" on a Hyperion Essbase database (of ASO type). This project has several dependencies that are not public (such as `essbase-utils`) but could theoretically be modified to run by including the necessary Essbase Jar files, and writing a replacement for the `QuickCubeOp` helper class (this class handles a lot of boilerplate code for connecting to an Essbase cube).

This program has a couple different command line options for updating, removing, and viewing the renegade members on an cube. You can view these options by checking out the `RenegadeCommand` class.

## License

Licensed under Apache Sotware License, version 2