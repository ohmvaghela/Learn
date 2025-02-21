# Spark Stream

## Stream data
- Continous flow of data generated in real-time
- Unlike batch data, which is collected, processed in chunks and sent.
  - Stream data is sent in real time
- Characteristics
  - Continous
  - Real-time processing
  - Unbounded : No filed size
  - Time sensitive
  - Transient : Data is lost, unless explicitely stored
  - Hetrogeneous : Can come from any source and can be structured, semi-structured, unstructured

| Feature | Stream | Batch |
|---|---|---|
| Data | Continuous | Fixed-size |
| Processing Style | Real-Time (low latency) | Scheduled (high latency) |
| Data Size | Unbounded | Bounded |
| Data Handling | Event-by-event or micro-batch | Full dataset at once |
| Use Cases | Monitoring, alerting, ETL, real-time analytics | Historical analysis, reporting |
| Examples | Kafka streams, sensor data, social media feeds | Daily sales reports, data backups |


