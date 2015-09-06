# Configuration Options

## Auditing 

### jaas.audit.isEnabled

Enables or disables auditing.

Allowed values:

| Value             | Setting           |
|-------------------|-------------------|
| `true`            | Auditing enabled  |
| `false` (default) | Auditing disabled |

### jaas.audit.isSingleton

Determines whether the auditing instance is created as a singleton.

Allowed values:

| Value             | Setting                              |
|-------------------|--------------------------------------|
| `true`            | Auditing instance is a singleton     |
| `false` (default) | Auditing instance is not a singleton |

### jaas.audit.class

The audit class to instantiate. Must implement the `org.beiter.michael.authn.jaas.common.audit.Audit` interface.
 
Default: `org.beiter.michael.authn.jaas.common.audit.SampleAuditLogger`

## Message Queues

### jaas.messageq.isEnabled

Enables or disables message queues.

Allowed values:

| Value             | Setting                 |
|-------------------|-------------------------|
| `true`            | Message queue enabled   |
| `false` (default) | Message queue  disabled |

### jaas.messageq.isSingleton

Determines whether the message queue instance is created as a singleton.

Allowed values:

| Value             | Setting                                   |
|-------------------|-------------------------------------------|
| `true`            | Message queue instance is a singleton     |
| `false` (default) | Message queue instance is not a singleton |

### jaas.messageq.class

The message queue class to instantiate. Must implement the `org.beiter.michael.authn.jaas.common.messageq.MessageQ`
interface.
 
Default: `org.beiter.michael.authn.jaas.common.messageq.SampleMessageLogger`

## Password Validation

### jaas.password.validator.isSingleton

Determines whether the password validator instance is created as a singleton.

Allowed values:

| Value             | Setting                                        |
|-------------------|------------------------------------------------|
| `true`            | Password validator instance is a singleton     |
| `false` (default) | Password validator instance is not a singleton |

### jaas.password.validator.class

The password validator class to instantiate. Must implement the 
`org.beiter.michael.authn.jaas.common.validator.PasswordValidator` interface.

For security reasons, this setting has no default, and is **required**. Logins attempts
**will fail** while this setting is not configured.

## Password Based Authentication

 ### jaas.password.authenticator.isSingleton

 Determines whether the password authenticator instance is created as a singleton.

 Allowed values:

 | Value             | Setting                                            |
 |-------------------|----------------------------------------------------|
 | `true`            | Password authenticator instance is a singleton     |
 | `false` (default) | Password authenticator instance is not a singleton |

### jaas.password.authenticator.class

The username / password based authenticator class to instantiate. Must implement the 
`org.beiter.michael.authn.jaas.common.authenticator.PasswordAuthenticator` interface.

For security reasons, this setting has no default, and is **required**. Logins attempts
**will fail** while this setting is not configured.

## JDBC Password Login Module Database Connection

### jaas.jdbc.jndi.name

The JNDI connection name to use when connecting to a database. The JNDI name must reference a SQL DataSource.

If this name is not set, the JDBC configuration parameters are evaluated and a JDBC connection pool is created. In other 
words, the JDBC connection parameters are ignored if the JNDI connection name is set.

### jaas.jdbc.jdbcPool.driver

The JDBC driver to use for the connection pool. 

This parameter is required if a connection pool is used.

### jaas.jdbc.jdbcPool.url

The JDBC database URL to connect to. Must be a valid JDBC database URL, as required by the JDBC driver.

### jaas.jdbc.jdbcPool.username

The username to authenticate with in the JDBC connection.

### jaas.jdbc.jdbcPool.password

The password to authenticate with in the JDBC connection.

### jaas.jdbc.jdbcPool.maxTotal

The maximum number of active connections that can be allocated from this pool at the same time, or negative for no 
limit.

An invalid value is ignored.

See `org.beiter.michael.db.ConnectionPoolSpec` for default.

### jaas.jdbc.jdbcPool.maxIdle

The maximum number of connections that can remain idle in the pool, without extra ones being released, or negative for 
no limit.

An invalid value is ignored.

See `org.beiter.michael.db.ConnectionPoolSpec` for default.

### jaas.jdbc.jdbcPool.minIdle

The minimum number of connections that can remain idle in the pool, without extra ones being created, or zero to create 
none.

An invalid value is ignored.

See `org.beiter.michael.db.ConnectionPoolSpec` for default.

### jaas.jdbc.jdbcPool.maxWaitMillis

The maximum number of milliseconds that the pool will wait (when there are no available connections) for a connection 
to be returned before throwing an exception, or -1 to wait indefinitely.

An invalid value is ignored.

See `org.beiter.michael.db.ConnectionPoolSpec` for default.

### jaas.jdbc.jdbcPool.testOnCreate

The indication of whether a connection will be validated after creation (`true` or `false`). 

See `org.beiter.michael.db.ConnectionPoolSpec` for default.

### jaas.jdbc.jdbcPool.testOnBorrow

The indication of whether connections will be validated before being borrowed from the pool (`true` or `false`).

See `org.beiter.michael.db.ConnectionPoolSpec` for default.

### jaas.jdbc.jdbcPool.testOnReturn

The indication of whether a connection will be validated before being returned to the pool (`true` or `false`).

See `org.beiter.michael.db.ConnectionPoolSpec` for default.

### jaas.jdbc.jdbcPool.testWhileIdle

The indication of whether connections will be validated by the idle object evictor (if any). If an object fails to 
validate, it will be dropped from the pool (`true` or `false`).

See `org.beiter.michael.db.ConnectionPoolSpec` for default.

### jaas.jdbc.jdbcPool.timeBetweenEvictionRuns

The number of milliseconds to sleep between runs of the idle object evictor thread. When non-positive, no idle object 
evictor thread will be run.

See `org.beiter.michael.db.ConnectionPoolSpec` for default.

### jaas.jdbc.jdbcPool.numTestsPerEvictionRun

The number of objects to examine during each run of the idle object evictor thread (if any).

See `org.beiter.michael.db.ConnectionPoolSpec` for default.

### jaas.jdbc.jdbcPool.isLIFO

`True` means that the pool returns the most recently used ("last in") connection in the pool (if there are idle 
connections available). `False` means that the pool behaves as a FIFO queue - connections are taken from the idle 
instance pool in the order that they are returned to the pool.

See `org.beiter.michael.db.ConnectionPoolSpec` for default.

### jaas.jdbc.jdbcPool.autoCommit

The default auto-commit state of connections created by the pool (`true` or `false`).

See `org.beiter.michael.db.ConnectionPoolSpec` for default.

### jaas.jdbc.jdbcPool.readOnly

The default read-only state of connections created by the pool (`true` or `false`).
                                                              
See `org.beiter.michael.db.ConnectionPoolSpec` for default.

### jaas.jdbc.jdbcPool.transactionIsolation

The default transaction isolation state of connections created by this pool.

Possible values are:

- 0: No transaction
- 1: Read committed
- 2: Read uncommitted
- 3: Repeatable read
- 4: Serializable

See `org.beiter.michael.db.ConnectionPoolSpec` for default.

### jaas.jdbc.jdbcPool.validationQuery

The SQL query that will be used to validate connections from the pool before returning them to the caller. If specified, 
this query *MUST* be an SQL SELECT statement that returns at least one row. 

See `org.beiter.michael.db.ConnectionPoolSpec` for default.

### jaas.jdbc.jdbcPool.maxConnLifetime

The maximum lifetime in milliseconds of a connection. After this time is exceeded the connection will fail the next 
activation, passivation or validation test. A value of zero or less means the connection has an infinite lifetime.

See `org.beiter.michael.db.ConnectionPoolSpec` for default.

## JDBC Password Login Module Settings (non-DB connection related settings)

### jaas.jdbc.sql.userQuery

The SQL query used to retrieve a row from the database.  

This SQL query must take two and only two query parameters, the first being the domain, the second being the user name. 
If the domain is not used in the database, the SQL query must be crafted so that the first parameter is irrelevant for
the search result and removed from the query by the DB query parser (e.g. `SELECT userID, credential FROM table WHERE 
? IS NOT NULL AND username = ?`).  

The SQL query must return two columns, the first being the user's ID, the second being the credential against which the 
provided password is to be validated. Note that the user ID and the user name may be identical (e.g. in cases where the  
user name is used as the primary key, like so: `SELECT username, credential FROM table WHERE domain = ? AND 
username = ?`.
