how to run/build:
-Since this is a microservices run these services on the mentioned port and please check if these ports are used anywhere and kill the existing if possible(if they aren't vital procces)
    bookmyshow-booking-service (port 8085)
    bookmyshow-payment-service (port 8084)
    bookmyshow-show-service (port 8086)
    bookmyshow-theatre-service (port 8087)
    bookmyshow-movie-service (port 8088)
    bookmyshow-user-service (port 8089)

how to test:
-I don't have test classes currently but please verify the expected output is in accordance with the schema/DTO's mentioned and no exceptions thrown out.

coding conventions:
-leave a line at starting and ending of each method
-Write comments only if necessary for process like 
    calling other service, method(not for curd operations)
    tricky implementation
    optimizations/workarounds

architecture notes
-This is a project focused on microservices that is both resume worthy and interview ready
-Don't break the services and explain every change you make in a interview depth level for deeper understanding + mention the concepts used from an interview point of view
-Currently trying to integrate redis(caching) + retry + rate limiting into this proj on top of existing services as microservice

files or folders to avoid touching
-currently none

review/deployment rules
-push evey git change to this repo:https://github.com/devadharshan-s/SeatFlow
