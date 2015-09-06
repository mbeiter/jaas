# JAAS Library Change Log

## 1.0

Initial release

## 2.0

### Defects Fixed

* DbProperties.buildDefault() fails with illegal argument exception bug (#2)
* Properties objects do not support cloning (#3)
* Caching configuration may introduce problems when applications reset their configuration dynamically (#6)
* The password authenticator factory should not use a default authenticator (#10)
* The password validator factory should not use a default authenticator (#11)

### Enhancements

* Upgrade org.beiter.michael.util.db dependency to 1.1 (#1)
* Upgrade build tools to 1.1 enhancement (#4)
* Change the configuration mechanism to use a properties POJO (#5)
* Clean up JAAS configuration parameter names (#7)
* Audit and message queue sample implementations no longer require direct access to both the main configuration POJO
  and the raw configuration as two separate objects (#8)
* Restructure the project structure to allow more intuitive extensions (#9)
* Parsed properties objects should contain a copy of the original properties (#13)
* Audit and message queue specific helpers moved to a dedicated utility class (#14)
* Factories provide methods to retrieve both either singleton or new instances, instead of always returning a singleton
  (#15)
* Operator can configure if the audit subsystem should be instantiated as a singleton (#16)
* Operator can configure if the message queue subsystem should be instantiated as a singleton (#17)
* Operator can configure if the password validator subsystem should be instantiated as a singleton (#18)
* Operator can configure if the password authenticator subsystem should be instantiated as a singleton (#19)
