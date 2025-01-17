# SQL python connectivity

- There are two libraries in perticular
  1. mysql.connector : Low Level library for raw database interactor
  2. sqlalchemy : fully functional ORM that can run raw queires as well

## - MySQL connector
> # Import notes from python/readme.md

## - sqlalchemy
- Connecting with DB

```py
from sqlalchemy import create_engine
engine = create_engine('mysql+mysqlconnector://root:root@localhost/nyctaxidb')
query = 'select * from yellowtrip'
df = pd.read_sql(query, engine)
print(df)
```

- JSON
```py
df = pd.read_json('Data Sets/employee.json')
```
