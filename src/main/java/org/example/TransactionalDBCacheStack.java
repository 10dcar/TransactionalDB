package org.example;

import java.util.*;

public class TransactionalDBCacheStack implements DB {
    Stack<HashMap<String, String>> operationsStack = new Stack<>();
    Stack<HashMap<String, String>> cacheStack = new Stack<>();

    public TransactionalDBCacheStack (){
        operationsStack.add(new HashMap<>());
        cacheStack.add(new HashMap<>());
    }
    //Begins a transaction
    public boolean begin(){
        operationsStack.add(new HashMap<>());
        cacheStack.add((HashMap<String, String>) cacheStack.lastElement().clone());

        return true;
    }
    //Commits everything that happened after the begin
    public boolean commit(){
        if(!operationsStack.isEmpty()) {
            cacheStack.pop();
            for (Map.Entry<String, String> entry : operationsStack.pop().entrySet()) {
                //Local class set() method
                this.set(entry.getKey(), entry.getValue());
            }
            return true;
        }
        return false;
    }
    //Removes everything after the begin
    public boolean rollback(){
        if(!operationsStack.isEmpty()) {
            cacheStack.pop();
            operationsStack.pop();
            return true;
        }
        return false;
    }
    //Gets a value from the database. It can happen during a transaction or outside a transaction.
    public String get(String key){
        String value = cacheStack.lastElement().get(key);

        System.out.println("Get non-stack: {" + key + "=>" + value + "}");
        return value;
    }
    //Sets a value in the database. It can happen during a transaction or outside transaction.
    public void set(String key, String value) {
        cacheStack.lastElement().put(key, value);
        operationsStack.lastElement().put(key, value);
    }
}

