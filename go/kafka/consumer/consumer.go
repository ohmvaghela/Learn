package main

import (
	"fmt"
	"log"
	"sync"
	"time"

	"github.com/confluentinc/confluent-kafka-go/v2/kafka"
)

func createConsumer()(*kafka.Consumer, error){
	consumer, err := kafka.NewConsumer(&kafka.ConfigMap{
		"bootstrap.servers": "localhost:9092",
		"group.id":          "my_group",
		"auto.offset.reset": "beginning",
	})
	if err != nil {return nil, err}
	return consumer,err
}

func SubscribeToTopic(consumer *kafka.Consumer, topic string){
	err := consumer.Subscribe(topic, nil)
	if err != nil {
		log.Fatalf("Failed to subscribe to topic: %s", err)
	}
	fmt.Println("Consumer started... Listening for messages...")
}

func ListenConsumer(consumer *kafka.Consumer, wg *sync.WaitGroup){
  for {
		// msg, err := consumer.ReadMessage(-1) // Timeout -1 means wait indefinitely
			msg, err := consumer.ReadMessage(5*time.Second) // Timeout -1 means wait indefinitely

			if err == nil {
				fmt.Printf("Received message: %s from %s\n", string(msg.Value), msg.TopicPartition)
				consumer.CommitMessage(msg) 
			} else {
				fmt.Printf("Consumer error: %v\n", err)
				break
			}
		}
		defer wg.Done()
}

func spinConumer(wg *sync.WaitGroup){
	consumer, err := createConsumer()
	if err != nil {log.Fatal("Failed to create consumer : %s", err)}
	defer consumer.Close()
	topic := "test_topic"
	SubscribeToTopic(consumer,topic)

	ListenConsumer(consumer, wg)
}