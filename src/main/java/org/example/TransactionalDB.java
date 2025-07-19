package org.example;

import java.util.*;

public class TransactionalDB {
    HashMap<String, String> db = new HashMap<>();
    Stack<HashMap<String, String>> operationsStack = new Stack<>();

    //Begins a transaction
    public boolean begin(){
        this.operationsStack.add(new HashMap<>());

        return true;
    }
    //Commits everything that happened after the begin
    public boolean commit(){
        if(!this.operationsStack.isEmpty()) {
            HashMap<String, String> keyValue = this.operationsStack.pop();
            if(keyValue != null) {
                for (Map.Entry<String, String> entry : keyValue.entrySet()) {
                    //set() repeated here..
                    if(this.operationsStack.lastElement() != null) {
                        this.operationsStack.lastElement().put(entry.getKey(), entry.getValue());
                    } else {
                        db.put(entry.getKey(), entry.getValue());
                    }
                }
                return true;
            }
        }
        return false;
    }
    //Removes everything after the begin
    public boolean rollback(){
        if(!operationsStack.isEmpty()) {
            this.operationsStack.pop();
            return true;
        }
        return false;
    }
    //Gets a value from the database. It can happen during a transaction or outside a transaction.
    public String get(String key){
        String value = "";
        ListIterator<HashMap<String, String>> iterator = this.operationsStack.listIterator(this.operationsStack.size());
        while (iterator.hasPrevious()) {
            HashMap<String, String> entry = iterator.previous();
            if(entry != null && entry.containsKey(key)) {
                System.out.println("Get stack: {" + key + " : " + entry.get(key) + "}");
                return entry.get(key);
            }
        }
        value = db.get(key);
        System.out.println("Get non-stack: {" + key + "=>" + value + "}");
        return value;
    }
    //Sets a value in the database. It can happen during a transaction or outside transaction.
    public boolean set(String key, String value) {
        if(!this.operationsStack.isEmpty()){
            this.operationsStack.lastElement().put(key, value);
            return true;
        } else {
            db.put(key, value);
            return true;
        }
    }
}
