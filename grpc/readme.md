# GRPC

- [GRPC](#grpc)
  - [RPC (Remote Procedure Call)](#rpc-remote-procedure-call)
  - [Stub](#stub)
  - [Client Procedure Stub](#client-procedure-stub)
  - [Types of RPC](#types-of-rpc)
  - [Protocal buffer (`.proto`)](#protocal-buffer-proto)
  - [Stream](#stream)


## RPC (Remote Procedure Call)
- Allows client to call Procedure(Function) on some other or same machine which may be reomte or local
- Client has client Procedure Stub, which marshals request and sends it to tranport layer
  - Then the request is sent to server
- Server has server procudure stub which unmarshals the request
  - After processing the request, it marshals the response and it may sends response back to client

## Stub
- A function on client that acts a proxy between client and remote function
- It is generated using `.proto` file

## Client Procedure Stub
- A local function on client machine that calls remote function/procdure on server
- Say following is the `.proto` file

  ```proto
  service GreetService {
    rpc SayHello (HelloRequest) returns (HelloResponse)
  }
  ```

- Then the client procedure stub will look like

  ```go
  // in golang
  client.SayHello(context, &HelloRequest{message:"message"})
  ```

## Types of RPC

1. Callback RPC
    - Single request-response, after which connection is closed
2. Broadcast RPC
    - Client Broadcast request to all the servers on network that will process the request
    - The server may or may not send response
3. Batch Mode RPC
    - If there are multiple RPC from client then they will be batched up and will be sent in single connection
  
## Protocal buffer (`.proto`)
- They are written in `Interface Defination Language (IDL)`
- Example `.proto` file

```proto
// define version of proto file
syntax="proto3";

// define go package
option go_package="grpc/helloworld";

// define proto package name
package helloWorld;

message HelloRequest{}

message HelloResponse{}

service HelloService {
  rpc SayHello(HelloRequest) returns (HelloResponse)
}
```

- About fields in protocal buffer
  - A number is assigned to each field in message
  - Like a unique identifier

```proto
message Person {
  string name=1;
  int id=2
}
```

- Nested and Composite message

```proto
// nested message
message Order {
  string id=1;
  message Item {
    string itemName=1;
  }
  Item item=2;
}

// composite message
message Customer{
  stirng name=1;
  Order order=2;
}
```

- Enum in proto file

```proto
enum Status {
  UNKNOWN=0;
  ACTIVE=1;
  INACTIVE=2;
}
```

- Array/List are defined using `repeated` keyword

```proto
message Name {
  repeated string hobbies=1;
}
```

- `Optional` keyword
  - In proto2 it was not by default
  - in proto3 field is optional by default
  - if field is explicity set to optional then its vaule will be `nil`/`null`/`unset`
  - Or it will have a default value like for int is 0, for stirng is `""`

## Stream
- If stream is mentioned in either incomming or outgoing message then it can handle multipe request/reposne depending in a connection

```proto
rpc X(request) returns (stream reponse) // Server streaming
rpc X(stearm request) returns (reponse) // Client streaming
rpc X(stearm request) returns (stream reponse) // bi-direational streaming
```

