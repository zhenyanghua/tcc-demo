## Try Confirm/Cancel (TCC) Pattern
TCC is a distributed transactions pattern that fits in the stateless REST services. It is originally proposed by Guy Pardon, the creator of Atomikos. 

### Useful References
- [Conference Speech By Guy Pardon](https://www.infoq.com/presentations/Transactions-HTTP-REST)
- [TCC for REST API (API specification)](https://www.atomikos.com/Blog/TransactionManagementAPIForRESTTCC)
- [Spring Cloud TCC Example By Chris](https://github.com/prontera/spring-cloud-rest-tcc)

## About This Repository
This repository is a simplified Java implementation for anyone who is interested in learning the TCC pattern. This repo attempts to trim the most of the common boilerplate, but only preserve the minimal that could be most straightforward to observe this pattern. It could be adapted to use any backend stack. For more advanced and more practical use of TCC, please see the referenced [Spring Cloud TCC Example](https://github.com/prontera/spring-cloud-rest-tcc) for microservices Integration.

TCC should be used in distributed services setting. This repository for most simple demonstration, uses the same application context for different services. In practice, each service mentioned in this demo should have their own application context.

## About the Resource Reservation System 
TCC is a perfect fit for implementing distributed resource reservation system. TCC pattern effectively isolating the actual confirmation of the resource from when the attempt to reserve is created by introducing a pending stage. 

### Business Service Providers and Transactions Coordinator 
This repository contains two business service providers and one transaction service. The business services should conform to TCC interface which implement the try, confirm, and cancel methods. The transaction service acts as a resource coordinator, which attempts to confirm all pending transactions or to cancel all pending transactions. 

```
|-- Business Service
   |-- CarReservationService
   |-- FlightReservationService
|-- Transactions Coordinator
   |-- CoordinatorService
```
### Release Unconfirmed Resources
The key part of TCC is to release the resources that is not confirmed after a period of time pending on the business needs. Upon failure to confirm some of the reservations while the rest of the reservations are conformed, the entire transaction should rollback as an atomic operation. Those that are already confirmed reservations should be attempted to cancel so the resources they are holding could be released back to the pool. When there is failure to cancel them after the system makes an effort to retry the cancellation, a partial confirmation exception should be thrown to raise the attention for a manual fix. Because we are dealing with a distributed system, and service failure is guaranteed to happen at some point, regardless of the type of transactions managers we are using. On the other hand, for all those reservations that are not confirmed in the expected time, a separate scheduled repeatable task service should be responsible to cancel them and release the resources back to the pool. 
```
|-- Auto-cancellation
   |-- CarTask
   |-- FlightTask
```
### Use of REST Verbs
The TCC pattern addresses the confirm and cancel operations should be idempotent. Because the confirm operation is something we use to update the status of a created reservation, the `PUT` is a good use of the REST verbs to represent the idempotent characteristics of this operation. Because the cancel operation is something we use to delete an existing reservation, the `DELETE` is a good use of the REST verbs to represent the idempotent characteristics of this operation. On the other hand, `POST` should be used to create a reservation as with the try operation.
```
try - POST /flight/reservation HTTP/1.1
cancel - DELETE /flight/reservation/q1XA9j HTTP/1.1
confirm - PUT /flight/reservation/q1XA9j HTTP/1.1
```
### Use of Response Status
The TCC pattern addresses three response statuses. When a confirm or cancel operation succeeds, a `HttpStatus.NO_CONTENT` (204) should be returned. When they fail because of the reservation has expired or cancelled, a `HttpStatus.NOT_FOUND` (404) should be returned to indicate the reservation no longer exists. These two types of response status apply to both business service provider and transaction coordinator. When a partial confirmation exception is thrown, a `HttpStatus.CONFLICT` (409) should be returned to indicate there is a problem that might need manual intervention. The transaction coordinator should be responsible for logging such event and provide effectively trouble shooting strategies. It is not specified which status code to return when a try operation succeeds, but from the semantic meaning of this operation, `HttpStatus.CREATED` (201) is used in this repository to indicate a pending reservation has be created.
```
try - HTTP/1.1 201 Created
confirm/cancel succeseds - HTTP/1.1 204 No Content
confirm/cancel fails - HTTP/1.1 404 Not Found
partical confirm - HTTP/1.1 409 Conflict
```
### Participants Link
The confirm and cancel operations reply on the participant links that is returned from the try operation, so that when the transaction coordinator attempts to confirm or cancel all reservations as an atomic operation, all participants of this transaction could be coordinated together. The transaction coordinator is responsible for keeping a collection of the participant links returned from the try operation.

The TCC pattern also enforces the use of expire time as part of the payload because this is the state that is being transferred from one service to another in distributed stateless REST services. The expire time could also be useful in preconditioning check before make confirmation attempt from the transaction coordinator.
```json
{
    "participantLinks": [
        {
           "uri": "http://localhost:8080/flight/reservation/q1XA9j",
            "expireTime": "2019-04-02T16:42:54.774-04:00"
        },
        {
            "uri": "http://localhost:8080/car/reservation/Hz88AX",
            "expireTime": "2019-04-02T16:03:34.795-04:00"
        }
    ]
}
```
## Work Flow Example
There is a work flow example provided in this repository. The steps the example work flow goes through are:
```
=============== 1st Book Attempt =============
Service Provider (flight): Reserve 2 flight seats
Service Provider (car): Reserve 1 car
Service Provider (flight): Cancel flight
Transaction Coordinator: Resource not found: Reservation FqqmR6 has been cancelled or doesn't exist
Transaction Coordinator: Confirm failed - 404 NOT_FOUND
=============== 2nd Book Attempt =============
Service Provider (flight): Reserve 2 flight seats
Service Provider (car): Reserve 1 car
Transaction Coordinator: Confirm succeeded seats
```
