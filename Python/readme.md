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

## Numpy

> ### - Array size in numpy is not mutable 
> ### - But we can reshape it

- Creating basic numpy array

```py
import numpy as np

arr = [1,2,3]

np_arr = np.array(arr)
np_arr = np.array([1,2,3]) # Other way
```

- Defining data type

```py
np_arr = np.array(arr, dtype='i')
# other way
np_arr = np.array(arr)
np_arr.astype(int)
```

- Making copy
  - If we directly assign array like `arr1 = arr` then same memory block will be assigned
  - so we need to use copy keyword `arr1 = arr.copy()`

```py
np_arr = np.array([1,2,3])
np_arr1 = np_arr # now np_arr1 will be reference to np_arr 
np_arr1[0] = 100
print(np_arr[0])# this will result 100

# hence we use copy
np_arr1 = np_arr.copy()
```

- View
  - we can create a new object of np_arr with same underlying memory
  - But can have differnet arributed shape and strides
  - Both have differnt object but same memory
  - `np_arr1 = np_arr.view()`

- Reshaping
  - Say I created an array of size 6
  - To reshape it to 2,3 matrix we can use reshape
  - to make it flat we use -1

```py
np_arr = np.array([0,1,2,3,4,5])
np_arr.reshape(2,3) # matrix 2,3
np_arr.reshape(-1) # flat array 
```  

- Joining array

  - [1,2,3] + [4,5,6] = [1,2,3,4,5,6]
  ```py
  arr = np.concatinate((arr1,arr2))
  arr = np.hstack((arr1,arr2))
  ```

  - [1,2,3] + [4,5,6] = [[1,2,3],[4,5,6]]
  ```py
  arr = np.vstack((arr1,arr2))
  ```

  - [1,2,3] + [4,5,6] = [[1,4],[2,5],[3,6]]
  ```py
  arr = np.dstack((arr1,arr2))
  ```

- Split
  - Divided `arrays` into `n arrays of array`
 
  ```py
  arr = np.array([0,1,2,3,4,5])
  print(np.split(arr,3))
  [array([0, 1]), array([2, 3]), array([4, 5])]
  ```

  - Search
    - Will return the values in form of array if exist

    ```py
    arr1 = [1,2,3,4,5]
    print(np.where(arr1%2 == 0)) # ((array([2,4,6])))
    print(np.where(arr1%2 == 0))[0] # [2,4,6]
    ```
  - Filter
    - Its like a mask of same size and same shape
    - Like if array is (1,4) then mask should be of (1,4) shape and size
   
    ```py
    arr1 = np.array([1,2,3,4,5,6])
    filter1 = [True,False,True,False,True,False]
    print(arr1[filter1]) # 2,4,6
    ```

# Database connectivity
- import lib `import mysql.connector`
- load connector

```py
mydb = mysql.connector.connect(
    host="localhost",
    user="root",
    password="root"
    # Optionally we can also add DB
    # database="temp" 
)
```

- Executing command
  - We have to create cursor instance
  - And using it we can execute query (any DDL, DML, DQL, TCL)
  ```py
  cursor = mydb.cursor()
  cursor.execute(" select * from table_name")
  for row in cursor.fetchall():
      print(row)
  ```

- We need to delete cursor instance once used
- Also we need to handle error and rollback if it is a transaction
- Hence we use `with` keyword so cursor is released once used 

```py
with mydb.cursor() as cursor:
    try: 
        cursor.execute("show databases;",multi=False)
        print(cursor.fetchall())
        mydb.commit()
    except Exception as e:
        print(e)
        mydb.rollback()
```

# Pandas
- Used for analysing data
- First we import data, then we clean it and analyse by plotting and correlation
- Mostly used for tabular data, where as numpy is mostly used for numerical data and homogenous data

## - Series
- Using for holding 1D array
- `pandas_series = pd.Series(arr)`
- To name the rows we can use index
- `pandas_series = pd.Series(a, index=['a','b'])`

## - Dataframe
- Used for holding 2D array and above
- to name row and column we can use 2 ways

```py
arr = [
    [[2,34,321],[3,32,2423]],
    [[2,34,321],[3,32,2423]]
]

df = pd.DataFrame(arr, index=["a","b"], columns=["b","d"])
print(df)

              b              d
a  [2, 34, 321]  [3, 32, 2423]
b  [2, 34, 321]  [3, 32, 2423]

dict = {
    "a" : [1,2,3],
    "b" : [4,5,6]
}
df = pd.DataFrame(dict,index=["a","b","C"])
print(df)

   a  b
a  1  4
b  2  5
C  3  6
```

- In pandas rows are vertical, and indices are horizontal
- To locate row use `df.loc[row_number]`
- to locate multiple rows `df.loc[[row1,row2,row3]]`
- Or use `df.loc[row1:rown]`
- To see top n rows use `df.head(number_of_rows)`
  - By default it is set to 10
- To see last n rows use `df.tail(number_of_rows)`

- ## Loc (Label Based Selection)
  - `df.loc[row_label, column_label]`
  - So for this df.loc[1:10, ['column_1','column_2','column_3']]
    - This will display row 1 to 10 and column names as specified
- ## iloc (Position based selection)
  - `df.iloc[row_position, column_position]`
  - example : `df.iloc[1:4,3:6]`
  - This will select row 1 to 4 and column 3 to 6
- Say we want all the rows where atleast one column is null
- `df.loc[df.isnull().any(axix=1)]`
  - df.isnull() -> boolean filer that will be applied to each cell (row,column)
  - .any(axis=1) -> check if any entry in the row has true? if yes then true for that row
  - ```py
    df.isnull().any(axis=1)
    0       True
    1      False
    2       True
    3      False
    4       True
           ...  
    886     True
    887    False
    888     True
    889    False
    890     True
    Length: 891, dtype: bool
    ```
  - .any(axis=0) -> Will check if any entry in the column is true is then assign that column as true
  - ```py
    df.isnull().any(axis=0)
    PassengerId    False
    Survived       False
    Pclass         False
    Name           False
    Sex            False
    Age             True
    SibSp          False
    Parch          False
    Ticket         False
    Fare           False
    Cabin           True
    Embarked        True
    dtype: bool
    ```
  - As `df.isnull().any(axis=1)` will return a boolean array of rows, which will work as filer for row indices
  - And these row indices will be displayed by loc
- Other examples are like
  - Create a boolean array of size of number_of_rows which has age filer
    - df['Age'] > 30 # Age is greter the 30
    - df['Age'].isin([25,30])
- ## Renameing
  - df.rename(columns={old_name:new_name , old_name:new_name}, index={old_name:new_name, old_name:new_name}, inplace=False)
  - Inplace is false by default, it means in place changes. If false then returns new data frame
    - if true then change the df and does not return anything
- ## Replace
  - Replace the value of entry
  - `df.replace(to_replace, value=None, inplace=False)`
  - Replace multiple value
    - `df_replaced_multiple = df.replace({'City': {'New York': 'NYC', 'Chicago': 'Chi-town'}})`
  - Replace perticular column
    - `df_replaced_column = df['City'].replace('Los Angeles', 'LA')`
- ## dropna
  - `df.dropna(axis=0, how='any', inplace=False)`
  - axis=0 -> drop row
  - how='any' -> drop row if any value is null
  - how='all' -> drop row if all values are null
- ## fillna
  - `df.fillna(value=None, method=None, inplace=False)`
  - value -> value to replace NaN with
    - it can be int, string or can be column wise like `{'Age': 30, 'City': 'Unknown'}`
  - method='ffill' -> forward fill, fill it with last value
  - method='ffill' -> forward fill, fill it with next value

- ## sort_index
  - Sort by indices like row label and column lables
  - `df.sort_index(axis=0, ascending=True, inplace=False)`
- ## sort_values
  - Sort by values
  - `df.sort_values(by, axis=0, ascending=True, inplace=False)`
  - `df.sort_values(by=['Age', 'Name'])`
  - `df.sort_values(by='Age')`


- ## Data cleaning
  - It is required beacaue there can be bad data like
    - Empty cell
    - Wrong format
    - Wrong data
    - Duplicate Data
   
  - Empty cell
    - Print empty cell
