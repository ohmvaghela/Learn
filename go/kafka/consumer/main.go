package main

import "sync"

func main(){
	var wg sync.WaitGroup

	wg.Add(1)
	spinConumer(&wg)

	wg.Wait()
}