<h1> Kafka </h1>

<h2> Content </h2>

- [Kafka Event](#kafka-event)
- [TopicPartition](#topicpartition)
- [Producer](#producer)
  - [Publishing message](#publishing-message)
    - [**No delivery Channel Mentioned**](#no-delivery-channel-mentioned)
    - [**delivery Channel Mentioned**](#delivery-channel-mentioned)
    - [**kafka Message**](#kafka-message)
  - [Produce Flush](#produce-flush)
- [Consumer](#consumer)
  - [Creating Consumer](#creating-consumer)
  - [Subscribing Consumer](#subscribing-consumer)
  - [Listening to messages](#listening-to-messages)
  - [consumer.Seek](#consumerseek)
  - [Kafka Consumer Group Rebalancing](#kafka-consumer-group-rebalancing)

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

> [!NOTE]
> - When using `asks` as 0 the output may look like
> 
> ```
> 2025/04/05 00:54:05 Custom Message delivered to test_topic[0]@unset
> 2025/04/05 00:54:05 Custom Message delivered to test_topic[0]@stored
> 2025/04/05 00:54:05 Custom Message delivered to test_topic[0]@-999
> 2025/04/05 00:54:05 Custom Message delivered to test_topic[0]@-998
> 2025/04/05 00:54:05 Custom Message delivered to test_topic[0]@-997
> ```
> 
> - Reason for these messages
>   - @unset	No offset was assigned (no ack received).
>   - @stored	Message was placed in the internal buffer, not yet acknowledged.
>   - @-999, @-998...	Internal librdkafka placeholders for not-yet-acknowledged messages when acks=0.
> - When using `asks` as 0 the output look like
>
> ```
> 2025/04/05 00:56:20 Custom Message delivered to test_topic[0]@5
> 2025/04/05 00:56:20 Custom Message delivered to test_topic[0]@6
> 2025/04/05 00:56:20 Custom Message delivered to test_topic[0]@7
> 2025/04/05 00:56:20 Custom Message delivered to test_topic[0]@8
> 2025/04/05 00:56:20 Custom Message delivered to test_topic[0]@9
> ```
>
> - This offset confirms that the leader has written the message to its log and acknowledged the producer.


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


### Produce Flush 
- Waits for all the message to be delivered before stopping
  - And returns number of events left to be processed
- Syntax

  ```go
  func (p *Producer) Flush(timeoutMs int) int
  ```

- Use

  ```go
  events_left := producer.Flush(2000)
	fmt.Println("events left : ", events_left)
  ```


## Consumer

### Creating Consumer

```go
consumer, err := kafka.NewConsumer(&kafka.ConfigMap{
  "bootstrap.servers": "localhost:9092",
  "group.id":          "my_group",
  "auto.offset.reset": "beginning",
})
if err != nil {return nil, err}
defer consumer.Close()
```

### Subscribing Consumer
- Syntax

  ```go
  func (c *Consumer) Subscribe(topic string, rebalanceCb RebalanceCb) error
  func (c *Consumer) SubscribeTopics(topics []string, rebalanceCb RebalanceCb) (err error)
  // To get topics subscribed to 
  func (c *Consumer) Subscription() (topics []string, err error)
  ```

```go
err := consumer.Subscribe(topic, nil)
if err != nil {
  log.Fatalf("Failed to subscribe to topic: %s", err)
}
fmt.Println("Consumer started... Listening for messages...")
```

### Listening to messages

- Syntax
  - `timeout` : Time to wait for before closing consumer 
    - Consumer closes by throwing error
    - `timeout = -1` : Wait indeinately
    - `timeout = 5*time.Second` : Wait for time **5 sec**
  - Once timeout occurs it throws `kafka.ErrorCode` : `kafka.ErrMsgTimedOut`
  ```go
  func (c *Consumer) ReadMessage(timeout time.Duration) (*Message, error)
  ```

- Use

  ```go
  for {
  // msg, err := consumer.ReadMessage(-1) // Timeout -1 means wait indefinitely
    msg, err := consumer.ReadMessage(5*time.Second) // Timeout -1 means wait indefinitely
    if err == nil {
  	  fmt.Printf("Received message: %s from %s\n", string(msg.Value), msg.TopicPartition)
  	} else {
  		fmt.Printf("Consumer error: %v\n", err)
  		break
  	}
  }
  ```

> [!NOTE]
> - Kafka-go maintains an internal buffer (queue) to store messages as they arrive
> - Kafka-go dont clear them by default we need to clear them manually to do it
>   - `consumer.CommitMessage(msg)` : Do this after message is processed, or `msg` may be deleted and might not be usable

### consumer.Seek
- Say once consumer is created and we want to offset for particular partition-consumerGroup pair we use Seek
- Seek sets offset for a consumer

  ```go
  consumer.Seek(
    kafka.TopicPartition{
      Topic: &topic, 
      Partition: 0, 
      Offset: kafka.OffsetEnd, // int64
    }, 
    -1,
  )
  ```

- Once offset is done then we can listen to topic

```go
consumer, err := createConsumer()
if err != nil {log.Fatalf("Failed to create consumer : %s", err)}
defer consumer.Close()

topic := "test_topic"
SubscribeToTopic(consumer, topic)

tp := kafka.TopicPartition{
  Topic: &topic, 
  Partition: 0, 
  Offset: kafka.OffsetEnd
}

err := consumer.Seek(tp, -1)
if err != nil {log.Printf("Failed to seek to end of topic %s: %v", topic, err)} 
else {fmt.Printf("Consumer seeked to the end of topic %s, partition %d\n", topic, 0)}

ListenConsumer(consumer, wg)
```

### Kafka Consumer Group Rebalancing
- **Trigger:** Rebalancing happens when consumers join or leave a group, or when topic partitions increase, causing a change in partition assignments to consumers.
- **Workload Shift:** If a topic has multiple partitions and a new consumer joins the group, Kafka rebalances to distribute partitions more evenly among the consumers.
- **Partition Handoff:** When a partition is reassigned, the new consumer starts processing from the last **committed offset** for that partition. Processed and committed messages are skipped. Uncommitted messages might be re-processed ("at-least-once" delivery).
- We can provide custom rebalance logic while subscribing topic

  ```go
  err := consumer.Subscribe(topic, rebalanceCallback)
  ```

- Callback sample

  ```go
  func rebalanceCallback(c *kafka.Consumer, ev kafka.Event) error {
      switch e := ev.(type) {
      case kafka.AssignedPartitions:
          fmt.Println("Partitions assigned:", e.Partitions)
          c.Assign(e.Partitions) // Accept assigned partitions
      case kafka.RevokedPartitions:
          fmt.Println("Partitions revoked:", e.Partitions)
          c.Unassign() // Remove assigned partitions
      default:
          fmt.Println("Unknown event:", ev)
      }
      return nil
  }
  ```

> [!NOTE]
> - While performing partition handoff, records can be left unprocessed, or multiple times processed, or can result in error
> - To handle this there are some industry standard ways
>   - it's crucial to ensure that all **processed messages** for those partitions have their **offsets committed** to Kafka
>     - Or else some messages will be processed multiple times
>   - If consumer maintains in memory buffer then ensure that is flushed
>   - To cross check progress while rebalacing the current state of consumer can be saved and can be used for comparision later
>   - If it has db connections it should be gracefully shutdown  