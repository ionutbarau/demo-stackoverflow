# demo-stackoverflow
The question on stackoverflow is "How to secure microservice websocket endpoint with Spring Cloud Gateway?".
More specifically how to implement csrf for websocket connect endpoint in gateway. I have not found any way to do that.
This example works, as I have added csrf for websockets in the downstream service, but this is not what I want.

During my analysis I have found 2 issues:
1. CSRF cookie and header do not match for websockets Connect endpoint, which was solved by using CsrfChannelInterceptor. See https://stackoverflow.com/questions/78666526/how-to-secure-microservice-websocket-endpoint-with-spring-cloud-gateway?noredirect=1#comment138726439_78666526
2. Not sure is this was intended but is confusing. For http calls, Spring will skip CSRF validation if Bearer token is found, while for websockets it does not, even ig the initial connect message is a http call.

The client can be found in : api-gateway/src/test/resources/ws-queue.html