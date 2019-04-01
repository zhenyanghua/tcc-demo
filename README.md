# This is still a working progress

## Try Confirm/Cancel (TCC) Pattern

TCC is a distributed transactions pattern that fits in the stateless REST services. It is originally proposed by Guy Pardon, the creator of Atomikos. 

### Useful References
- [Conference Speech By G.Pardon](https://www.infoq.com/presentations/Transactions-HTTP-REST)
- [TCC for REST API (API specification)](https://www.atomikos.com/Blog/TransactionManagementAPIForRESTTCC)
- [Spring Cloud TCC Example By Chris](https://github.com/prontera/spring-cloud-rest-tcc)

## About This Repository
This repository is a simplified Java implementation for anyone who is interested in learning the TCC pattern. This repo attempts to trim the most of the common boilerplate, but only preserve the minimal that could be most straightforward to observe this pattern. It could be adapted to use any backend stack. For more advanced and more practical use of TCC, please see the referenced [Spring Cloud TCC Example By Chris](https://github.com/prontera/spring-cloud-rest-tcc) for microservices Integration.

TCC should be used in distributed services setting. This repository for most simple demonstration, uses the same application context for different services. In practice, each service mentioned in this demo should have their own application context.
