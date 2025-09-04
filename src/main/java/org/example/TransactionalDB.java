package org.example;

import java.util.*;

interface DB {
    public boolean begin();
    public boolean commit();
    public boolean rollback();
    public String get(String key);
    public boolean set(String key, String value);
}

public class TransactionalDB implements DB {
    HashMap<String, String> db = new HashMap<>();
    Stack<HashMap<String, String>> operationsStack = new Stack<>();
    HashMap<String, String> fastSearch = new HashMap<>();

    //Begins a transaction
    public boolean begin(){
        operationsStack.add(new HashMap<>());

        return true;
    }
    //Commits everything that happened after the begin
    public boolean commit(){
        if(!operationsStack.isEmpty()) {
            HashMap<String, String> keyValue = operationsStack.pop();
            if(keyValue != null) {
                for (Map.Entry<String, String> entry : keyValue.entrySet()) {
                    //Local class set() method
                    this.set(entry.getKey(), entry.getValue());
                }
                return true;
            }
        }
        return false;
    }
    //Removes everything after the begin
    public boolean rollback(){
        if(!operationsStack.isEmpty()) {
            HashMap<String, String> keyValue = this.operationsStack.pop();
            if(keyValue != null) {
                for (Map.Entry<String, String> entry : keyValue.entrySet()) {
                    fastSearch.remove(entry.getKey());
                    //Search trough stack and db for the value interested
                    boolean found = false;
                    ListIterator<HashMap<String, String>> iterator = operationsStack.listIterator(operationsStack.size());
                    while (iterator.hasPrevious()) {
                        HashMap<String, String> entry_src = iterator.previous();
                        if(entry_src != null && entry_src.containsKey(entry.getKey())) {
                            found = true;
                            fastSearch.put(entry.getKey(), entry_src.get(entry.getKey()));
                        }
                    }
                    if(!found) {
                        fastSearch.put(entry.getKey(), db.get(entry.getKey()));
                    }
                }
            }
            return true;
        }
        return false;
    }
    //Gets a value from the database. It can happen during a transaction or outside a transaction.
    public String get(String key){
        String value = "";
        value = fastSearch.get(key);
        System.out.println("Get non-stack: {" + key + "=>" + value + "}");
        return value;
    }
    //Sets a value in the database. It can happen during a transaction or outside transaction.
    public boolean set(String key, String value) {
        fastSearch.put(key, value);
        if(!operationsStack.isEmpty()){
            operationsStack.lastElement().put(key, value);
            return true;
        } else {
            db.put(key, value);
            return true;
        }
    }
}
