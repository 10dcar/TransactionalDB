# In-Memory Transaction DB — Architecture

An in-memory key/value store supporting nested transactions (`begin` / `commit` / `rollback`).
Three implementations explore different trade-offs around the same `DB` interface.

## Class structure

```mermaid
classDiagram
    class DB {
        <<interface>>
        +begin() boolean
        +commit() boolean
        +rollback() boolean
        +get(String key) String
        +set(String key, String value) void
    }

    class TransactionalDB {
        -Stack~HashMap~ operationsStack
        -HashMap cache
    }

    class TransactionalDBCacheStack {
        -Stack~HashMap~ operationsStack
        -Stack~HashMap~ cacheStack
    }

    class TransactionalDBListIndex {
        -List~HashMap~ operationsList
        -HashMap cache
        -HashMap~String,Stack~Integer~~ index
    }

    class Main {
        +main(String[] args)
    }

    DB <|.. TransactionalDB
    DB <|.. TransactionalDBCacheStack
    TransactionalDBListIndex ..|> DB : (informal)
    Main --> TransactionalDB : uses
```

## Data layout — `TransactionalDBListIndex` (latest implementation)

```mermaid
flowchart LR
    subgraph cache["cache: HashMap"]
        c1["a → 1"]
        c2["b → 2"]
        c3["c → 3"]
    end

    subgraph opsList["operationsList (one HashMap per open txn)"]
        op0["[0] {a:1, b:1, c:1}  (base)"]
        op1["[1] {a:2, b:2, c:2}  (txn 1)"]
        op2["[2] {c:3}            (txn 2, nested)"]
    end

    subgraph index["index: key → Stack of opsList indices"]
        ia["a → [0, 1]"]
        ib["b → [0, 1]"]
        ic["c → [0, 1, 2]"]
    end

    cache -. mirrors top-of-stack values .- index
    index -. points into .-> opsList
```

## Operation flow

```mermaid
sequenceDiagram
    actor Caller
    participant DB as TransactionalDBListIndex
    participant Cache as cache
    participant Ops as operationsList
    participant Idx as index

    Caller->>DB: set(k, v)
    DB->>Cache: put(k, v)
    DB->>Idx: ensure stack(k); push(size-1)
    DB->>Ops: last().put(k, v)

    Caller->>DB: get(k)
    DB->>Cache: get(k)
    Cache-->>Caller: value

    Caller->>DB: begin()
    DB->>Ops: add(new HashMap)

    Caller->>DB: commit()
    note over DB: merge top frame into parent frame,<br/>pop one entry from each touched index stack
    DB->>Ops: pop last, write entries into new last
    DB->>Idx: pop top for each key in popped frame

    Caller->>DB: rollback()
    note over DB: discard top frame; restore cache from<br/>previous value via index lookup
    DB->>Idx: pop top for each key in top frame
    DB->>Cache: restore from operationsList[index.peek()] or remove
    DB->>Ops: remove last
```

## State machine — a single key across nested transactions

```mermaid
stateDiagram-v2
    [*] --> Base : set outside txn
    Base --> Txn1 : begin + set
    Txn1 --> Nested : begin + set
    Nested --> Txn1 : commit (merge into parent)
    Txn1 --> Base : commit (merge into base)
    Txn1 --> Base : rollback (restore prev from index)
    Nested --> Txn1Pre : rollback (restore prev from index)
    Txn1Pre --> Txn1 : continue
```
