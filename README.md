# demo-stackoverflow
The  question on stackoverflow is "How to secure microservice websocket endpoint with Spring Cloud Gateway?".
More specifically how to implement csrf for websocket connect endpoint in gateway. I have not found any way to do that.
This example works, as I have added csrf for websockets in the downstream service, but this is not what I want.

During my analysis I have found 2 additional issues/questions:
1. CSRF cookie and header do not match for websockets Connect endpoint, which was solved by using CsrfChannelInterceptor. See https://stackoverflow.com/questions/78666526/how-to-secure-microservice-websocket-endpoint-with-spring-cloud-gateway?noredirect=1#comment138726439_78666526
2. Not sure is this was intended but is confusing. For http calls, Spring will skip CSRF validation if Bearer token is found, while for websockets it does not, even ig the initial connect message is a http call.


**How to use**

The client can be found in : api-gateway/src/test/resources/ws-queue.html. I have started it from Intellij hence port 63342


1. Please start auth-server first and then start api-gateway and notification-service
2. Point your browser to http://localhost:9990/notification/push-notification/ and you will be redirected to login page. 
3. Log in with Use the user "test" with password "test".
4. Start the client(ws-queue.html) in the same browser window from Intellij to match the port
5. Connect to websocket endpoint
6. Send test notification, which will be listed in Notifications via websockets