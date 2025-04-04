<h1> Kafka </h1>

<h2> Content </h2>

- [Kafka Event](#kafka-event)
- [TopicPartition](#topicpartition)
- [Producer](#producer)
  - [Publishing message](#publishing-message)
    - [**No delivery Channel Mentioned**](#no-delivery-channel-mentioned)
    - [**delivery Channel Mentioned**](#delivery-channel-mentioned)
    - [**kafka Message**](#kafka-message)

## Kafka Event
- Kafka Event is interface to be implemented to make event human redable  
```go
type Event interface {
	// String returns a human-readable representation of the event
	String() string
}
```

## TopicPartition
- Used when send message or consuming messages
- Defines topic's specific partition to send to 
- Syntax

  ```go
  type TopicPartition struct {
  	Topic       *string 
  	Partition   int32
  	Offset      Offset
  	Metadata    *string
  	Error       error
  	LeaderEpoch *int32 // LeaderEpoch or nil if not available
  }
  func (p TopicPartition) String() string
  ```

## Producer
- Create High Level Producer instance

  ```go
	producer, err := kafka.NewProducer(&kafka.ConfigMap{
		"bootstrap.servers": "localhost:9092",
	})
  if err != nil {return}

	// close producer instance after use
  defer producer.Close()
  ```

- Other important params for kafka.ConfigMap
  - To connect to multiple brokers
    - `"bootstrap.servers":"localhost:9092,localhost:9093"`
  - Security protocal for messages sent
    - Messages are sent via TCP/IP protocal
    - `"security.protocol":"PLAINTEXT"` : default/ message sent a plain text wihtout security
    - `"security.protocol":"SSL"` : SSL/TLS
    - `"security.protocol":"SASL_PLAINTEXT"` : Simple Auth and Security Layer + Plain Text data
    - `"security.protocol":"SASL_SSL"` : Simple Auth and Security Layer + Encrypted Data
    - Furthur `SASL` auth mechanism can be specified
      - `PLAIN`, `SCRAM-SHA-256` etc.
- Other message handling params
  - How many replicas must be informed before mesage is considered successful
  - `"acks"` : 0,1,ALL
    - `0` = Fire and forget (No guarantee)
    - `1` = Leader acknowledgment (Medium reliability)
    - `all` = All replicas acknowledge (High reliability) 
  - `"compression.type"`	
    - Compress messages to optimize performance (`gzip`, `snappy`, `lz4`, `zstd`).
- Monitering and Debugging params
  - `"Debug"` : Enables debugging logs 
    - You can list things to be displayed when debugging in this syntax : `"broker,topic,msg"`
    - Few other : `generic`, `broker`, `topic`, `metadata`, `feature`, `queue`, `msg`, `protocol`, `cgrp`, `security`, `fetch`, `interceptor`, `plugin`, `consumer`, `admin`, `eos`, `mock`, `assignor`, `conf`, `telemetry`, `all`
  - `"log_level"`	Set log level 
    - (0 = No logs, 1 = Fatal, 2 = Error, 3 = Warning, 4 = Info, 5 = Debug).
  - `"statistics.interval.ms"`	
    - Controls how frequently the producer emits statistics (in milliseconds).

### Publishing message
- Producer instance is used to publish message

  - Syntax
 
  ```go
  func (p *Producer) Produce(msg *Message, deliveryChan chan Event) error
  ```

  - Use

  ```go
  err := producer.Produce(&kafka.Message{
		TopicPartition: kafka.TopicPartition{Topic: &topic, Partition: kafka.PartitionAny},
		Value:	  []byte(message),
	}, deliveryChan)
	if err != nil {return}
  ```

- In deliveryChan (delivery channel):
  - If nothing is mentioned then `producer.Events()` takes events and handles them 
  - If delivery channel is mentioned then `producer.Events()` wont handle them

#### **No delivery Channel Mentioned** 

  ```go
  err := producer.Produce(&kafka.Message{
		TopicPartition: kafka.TopicPartition{Topic: &topic, Partition: kafka.PartitionAny},
		Value:	  []byte(message),
	}, deliveryChan)
	if err != nil {return}
  ```

- So we need to use `producer.Events()` to handle events like following

  - Syntax
  
  ```go
  func (p *Producer) Events() chan Event
  ```

  - Use

  ```go
  func startKafkaEventCollector(producer *kafka.Producer) {
  	for event := range producer.Events() {
  		switch ev := event.(type) {
  		case *kafka.Message:
  			if ev.TopicPartition.Error != nil {
  				fmt.Printf("Failed to deliver message: %v\n", ev.TopicPartition)
  			} else {
  				fmt.Printf("Message delivered to %v\n", ev.TopicPartition)
  			}
  		}
  	}
  }
  ```

#### **delivery Channel Mentioned** 

```go
func ProcessDelivedMessages(deliveryChan chan kafka.Event, quitChan chan bool) {
	defer close(quitChan) // Ensure quitChan is closed when the function exits.
	for {
		select {
		case e := <-deliveryChan:
      // helper function mentioned in next code
			handleDeliveryChan(&e)// this wont print non message events
		case <-quitChan:
			log.Println("Delivery message processor shutting down.")
			return // Exit the function
		}
	}
}

// main function
deliveryChan := make(chan kafka.Event)
quitChan := make(chan bool) // can be triggered from anywhere to stop consumer
go ProcessDelivedMessages(deliveryChan, quitChan)
```

- Helper function
```go
// helper function 
func handleDeliveryChan(e *kafka.Event) {
	msg, ok := (*e).(*kafka.Message) // Type assertion and check
	if !ok {
		log.Printf("Received non-message event: %v", e)
		return 
	}
	if msg.TopicPartition.Error != nil {
		log.Printf("Custom Failed to deliver message: %v", msg.TopicPartition.Error)
	} else {
		log.Printf("Custom Message delivered to %v", msg.TopicPartition)
	}
}
```

#### **kafka Message** 
- Kafka Message is implementation of Kafka Event
  - Syntax
  
  ```go
  type Message struct {
  	TopicPartition TopicPartition
  	Value          []byte
  	Key            []byte
  	Timestamp      time.Time
  	TimestampType  TimestampType
  	Opaque         interface{}
  	Headers        []Header
  	LeaderEpoch    *int32 // Deprecated: LeaderEpoch or nil if not available. Use m.TopicPartition.LeaderEpoch instead.
  }
  func (m *Message) String() string
  ```

- Each message must a topic's Partition to send message to defined in `TopicPartition`
- Sometimes headers are used to add more info to message

  ```go
  type Header struct {
  	Key   string // Header name (utf-8 string)
  	Value []byte // Header value (nil, empty, or binary)
  }
  func (h Header) String() string
  ```

