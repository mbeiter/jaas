# JAAS Library

This JAAS library provides an extendable framework to easily create JAAS modules with auditing and message queue
support.

Extension points in the library include:
- Custom auditing
- Custom message queues
- Custom credential validation
- Custom authenticators

The library includes a sample "Plain text password validator" (which should not be used in production deployments) as
an example on how to implement credential validators.

The library also includes a production ready authenticator for JDBC backends using either a JDBC connection pool or a 
JNDI connection (which may or may not be pooled, depending on the JNDI configuration).

## Useful Links

- [Mike's Blog](http://www.michael.beiter.org)
- [Project home](http://mbeiter.github.io/jaas/)
- [Build instructions](BUILD.md)
- [GitHub Issue Tracker](https://github.com/mbeiter/jaas/issues)
- [Contribute](CONTRIBUTING.md) - Some pointers for contributing
- [Configuration instructions](CONFIG.md)

## License

Copyright (c) 2014, Michael Beiter (<michael@beiter.org>)

All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the 
following conditions are met:

- Redistributions of source code must retain the above copyright notice, this list of conditions and the following 
  disclaimer.
- Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following 
  disclaimer in the documentation and/or other materials provided with the distribution.
- Neither the name of the copyright holder nor the names of the contributors may be used to endorse or promote products 
  derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, 
INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE 
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, 
OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, 
DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT 
LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF 
ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
