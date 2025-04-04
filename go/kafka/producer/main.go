package main

import (
	"log"
	"sync"

	"github.com/confluentinc/confluent-kafka-go/v2/kafka"
)

func main() {
	var wg sync.WaitGroup

	deliveryChan := make(chan kafka.Event)
	quitChan := make(chan bool)


	wg.Add(2) 
	go spinProducer(&wg, deliveryChan, quitChan)
	go ProcessDelivedMessages(&wg, deliveryChan, quitChan)

	wg.Wait() 

	// In main after wg.Wait():
	close(deliveryChan)
	

	log.Println("Main function finished.")

}
