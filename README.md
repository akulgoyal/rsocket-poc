# rSocket POC

A simple demo to understand and get a feel of different communication styles using rSocket. Also, to check how imperative HTTP endpoints can be mixed with rSocket communication in services.

## Application structure

rSocket-client exposes HTTP endpoints to trigger rSocket interactions between client and server. This is to demonstrate that an APP or Web can call backend services using REST APIs while the service-to-service intercations occur on rSocket-TCP using CBOR for serialisation and de-serialisation.
