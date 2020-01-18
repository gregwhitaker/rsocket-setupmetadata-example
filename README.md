# rsocket-setupmetadata-example

## Building the Example
Run the following command to build the example:

    ./gradlew clean build
    
## Running the Example
Follow the steps below to run the example:

1. Run the following command to start the `hello-service`:

        ./gradlew :hello-service:run
        
    If the service has started successfully you will see the following in the terminal:
    
        > Task :hello-service:run
        [main] INFO example.hello.service.HelloService - RSocket server started on port: 7000
        
2. In a new terminal, run the following command to send a request for a hello message with the `hello-client`:

        ./gradlew :hello-client:run --args=Bob
        
   If successful, you will see that the client connected and specified its client identifier in the `hello-service` terminal:
   
        [reactor-tcp-nio-2] INFO example.hello.service.HelloService - Hello client connected: f538925a-c4be-41d5-93cf-f9250e3a8651
        
   and in the `hello-client` terminal you will see that the client id was sent back with the hello message:
   
       > Task :hello-client:run
       [main] INFO example.hello.client.HelloClient - Sending request for 'Bob'
       [main] INFO example.hello.client.HelloClient - Response: Hello, Bob! [clientId: f538925a-c4be-41d5-93cf-f9250e3a8651]

## Bugs and Feedback
For bugs, questions, and discussions please use the [Github Issues](https://github.com/gregwhitaker/rsocket-setupmetadata-example/issues).

## License
MIT License

Copyright (c) 2020 Greg Whitaker

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.