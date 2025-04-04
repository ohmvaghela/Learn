package main

import (
	"fmt"
	"log"
	"sync"

	"github.com/confluentinc/confluent-kafka-go/v2/kafka"
)

func getProducer() (*kafka.Producer, error) {
	producer, err := kafka.NewProducer(&kafka.ConfigMap{
		"acks":1,
		"bootstrap.servers": "localhost:9092",
		// "debug": "broker,topic,msg",
	})
	if err != nil {
		return nil, err
	}
	return producer, nil
}

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

func pulbishToTopic(producer *kafka.Producer, message, topic string, deliveryChan chan kafka.Event) {
	err := producer.Produce(&kafka.Message{
		TopicPartition: kafka.TopicPartition{Topic: &topic, Partition: kafka.PartitionAny},
		Value:	  []byte(message),
	}, deliveryChan)
	if err != nil {
		log.Printf("Failed to produce message: %s", err) // changed to Printf
	}
}

func ProcessDelivedMessages(wg *sync.WaitGroup, deliveryChan chan kafka.Event, quitChan chan bool) {
	defer wg.Done()
	defer close(quitChan) // Ensure quitChan is closed when the function exits.
	for {
		select {
		case e := <-deliveryChan:
			handleDeliveryChan(&e)// this wont print non message events
		case <-quitChan:
			log.Println("Delivery message processor shutting down.")
			return // Exit the function
		}
	}
}

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

func spinProducer(wg *sync.WaitGroup, deliveryChan chan kafka.Event, quitChan chan bool) { // added deliveryChan
	defer wg.Done() // moved to the top of the function.
	producer, err := getProducer()
	if err != nil {
		log.Fatalf("Failed to create producer: %s", err)
	}
	defer producer.Close()

	go startKafkaEventCollector(producer)

	topic := "test_topic"
	message := "Hello, Kafka from Golang!6"

	for i := 0; i < 5; i++ {
		pulbishToTopic(producer, message, topic, deliveryChan)
	}

	events_left := producer.Flush(6000)
	fmt.Println("events left : ", events_left)
	fmt.Println("calling quit")
	quitChan <- true

}

