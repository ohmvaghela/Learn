# Python 

## - Multithreading
- threading.Thread(target, args)
  - Used to create thread
- thread.start()
  - Used to start thread
- thread.join()
  - Execute code till here and wait for thead to complete

```py
import time

from asyncio import wait_for

import threading

def f1(a):
    for i in range(5):
        print(str(a))
        time.sleep(0.5)

if __name__ == "__main__":

    t1 = threading.Thread(target=f1, args=(1,))
    t2 = threading.Thread(target=f1, args=(2,))

    t1.start()
    t2.start()

    print("beafore")

    t1.join()
    t2.join()

    print("after")
```

- print("before") will be exected with Thread and print("after") will wait for execution
- Once thread.join() is completed then after will be printed

## Locks
- Creating lock
  - `my_lock = threading.Lock()`
- Using lock
  - `with my_lock:`
- Full code

```py


import time
import threading

my_lock = threading.Lock()

balance = 1000

def withdraw(amount):
    with my_lock:
        global balance
        balance -= amount
        print("balance left :" + str(balance))

threads = []


if __name__ == "__main__":
    print("")
    for _ in range(100):
        th = threading.Thread(target=withdraw, args=(10,))
        threads.append(th)
    for thread in threads:
        thread.start()

    for thread in threads:
        thread.join()

    print("\nend balance : "+str(balance))
```

## Daemon thread
- By default threads are non-daemon
  - Means program will wait for thred to complete its execution
  - to change it use thread_name.daemon = True

## Reentrant Lock (RLock())
- Say same thread will use a resource multiple times, but once locked cant be released
- Like a recursive function

```py
import threading

lock = threading.Lock()

def fn(val):
    with lock:
        print("In fn with value : "+str(val))
        val -= 1
        if(val > 0):
            fn(val)

if __name__ == "__main__":
    print("")
    fn(3)
```

- This will block the code with Locks
- So we use RLock as shown below

```py
import threading

lock = threading.RLock()

def fn(val):
    with lock:
        print("In fn with value : "+str(val))
        val -= 1
        if(val > 0):
            fn(val)

if __name__ == "__main__":
    print("")
    fn(3)
```

## Conditions in thread
- Say we want to do foo-bar printing question
- We need to wait for one thread to finish release resource and then other resource can start
- Just like condition variable in C++
```py
import time
import threading

condition = threading.Condition()
turn = "foo"

def foo():
    global turn
    with condition:
        while turn != "foo":
            condition.wait()
        print("foo", end=" ")
        turn = "bar"
        time.sleep(1)
        condition.notify()

def bar():
    global turn
    with condition:
        while turn != "bar":
            condition.wait()
        print("bar", end="\n")
        turn = "foo"
        time.sleep(1)
        condition.notify()

if __name__ == "__main__":
    foo_threads = []
    bar_threads = []

    for i in range(5):
        thread = threading.Thread(target=foo)
        foo_threads.append(thread)
    for i in range(5):
        thread = threading.Thread(target=bar)
        bar_threads.append(thread)

    for thread in foo_threads:
        thread.start()
    for thread in bar_threads:
        thread.start()

    for thread in foo_threads:
        thread.join()
    for thread in bar_threads:
        thread.join()
```

## Events
- Say there is a chain in which threads must be executed
- To do the same we use premitive version of locks which is events
- Say process one needs to complete before second process starts

```py
import time
import threading
event = threading.Event()

def first():
    print("First event start")
    time.sleep(2)
    print("First event ends")
    event.set()


def second():
    print("Second event start")
    event.wait()
    print("Second event ends")

if __name__ == "__main__":
    thread1 = threading.Thread(target=first)
    thread2 = threading.Thread(target=second)

    thread1.start()
    thread2.start()

    thread2.join()
    thread1.join()
```

## Reader writer problem 

```py
import time
import threading

readers = 0
writer_active = False
lock = threading.Condition()

class Reader:
    def read(self, id):
        global readers, writer_active
        with lock:
            while writer_active:
                lock.wait()  # Wait if a writer is active
            readers += 1  # Increment reader count
        print(f"Reader {id} started reading")
        time.sleep(2)  # Simulate reading
        with lock:
            readers -= 1  # Decrement reader count
            print(f"Reader {id} finished reading")
            if readers == 0:  # Notify writers if no readers are left
                lock.notify_all()

class Writer:
    def write(self, id):
        global readers, writer_active
        with lock:
            while readers > 0 or writer_active:
                lock.wait()  # Wait if readers are active or another writer is active
            writer_active = True  # Indicate writer is active
        print(f"Writer {id} started writing")
        time.sleep(2)  # Simulate writing
        with lock:
            writer_active = False  # Release writer lock
            print(f"Writer {id} finished writing")
            lock.notify_all()  # Notify readers and writers

if __name__ == "__main__":
    reader_c = Reader()
    writer_c = Writer()

    # Create threads
    reader1 = threading.Thread(target=reader_c.read, args=(1,))
    writer1 = threading.Thread(target=writer_c.write, args=(1,))
    reader2 = threading.Thread(target=reader_c.read, args=(2,))
    reader3 = threading.Thread(target=reader_c.read, args=(3,))
    writer2 = threading.Thread(target=writer_c.write, args=(2,))
    reader4 = threading.Thread(target=reader_c.read, args=(4,))

    # Start threads
    reader1.start()
    writer1.start()
    reader2.start()
    reader3.start()
    writer2.start()
    reader4.start()

    # Wait for threads to complete
    reader1.join()
    writer1.join()
    reader2.join()
    reader3.join()
    writer2.join()
    reader4.join()

```
