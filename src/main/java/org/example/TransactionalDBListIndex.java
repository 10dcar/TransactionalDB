package org.example;

import java.util.*;

public class TransactionalDBListIndex {
    List<HashMap<String, String>> operationsList = new ArrayList<>(); // la fiecare begin salveaza cate un hash map in array
    HashMap<String, String> cache = new HashMap<>();
    HashMap<String, Stack<Integer>> index = new HashMap<>(); //tine un hasmap cu toate cheile si la fiecare cheie un stack in care prima valoare, cea mai noua, tine minte unde se afla in lista de mai sus operationList

    public TransactionalDBListIndex (){
        this.begin();
    }
    //Begins a transaction
    public boolean begin(){
        operationsList.add(new HashMap<>());

        return true;
    }
    //Commits everything that happened after the begin
    public boolean commit(){
        if(!operationsList.isEmpty()) {
            for (Map.Entry<String, String> entry : operationsList.get(operationsList.size() - 1).entrySet()) {
                Stack<Integer> stackEntry = index.get(entry.getKey());
                if((stackEntry != null) && (stackEntry.peek() >= operationsList.size() - 1)) {
                    //sar peste prima valoare care este defapt pentru stiva cu valoarea curenta size() - 1
                    stackEntry.pop();
                }
                //Local class set() method
                this.set(entry.getKey(), entry.getValue());
                stackEntry = index.get(entry.getKey());
                if((stackEntry != null) && (stackEntry.peek() >= operationsList.size() - 1)) {
                    //sar peste prima valoare care este defapt pentru stiva cu valoarea curenta size() - 1
                    stackEntry.pop();
                }
            }
            operationsList.remove(operationsList.size() - 1);

            return true;
        }
        return false;
    }

    //DE FACUT INDEX
    //Removes everything after the begin
    public boolean rollback(){
        //tin un index pentru stack in care spun in ce hasmap al stackului gasesc ultima sau penultima valoare a unei chei
        if(!operationsList.isEmpty()) {
            for (Map.Entry<String, String> entry : operationsList.get(operationsList.size() - 1).entrySet()) {
                cache.remove(entry.getKey());
                if(operationsList != null && index != null && entry != null && index.get(entry.getKey()) != null) {
                    //sar peste prima valoare care este defapt pentru stiva cu valoarea curenta size() - 1
                    Stack<Integer> stackEntry = index.get(entry.getKey()); //ma duc in hasmap ul de index si citesc pentru fiecare cheie unde se mai afla in opList
                    if(stackEntry != null) {
                        if((!stackEntry.isEmpty()) && (stackEntry.peek() >= operationsList.size() - 1)) {
                            stackEntry.pop();
                        }
                        if ((!stackEntry.isEmpty()) && (operationsList.size() - 1 > stackEntry.peek())) {
                            HashMap<String, String> tempHM = operationsList.get(stackEntry.peek());
                            cache.put(entry.getKey(), tempHM.get(entry.getKey()));
                        }
                    }
                }
            }
            operationsList.remove(operationsList.size() - 1);
            return true;
        }
        return false;
    }
    //Gets a value from the database. It can happen during a transaction or outside a transaction.
    public String get(String key){
        String value = cache.get(key);

        System.out.println("Get non-stack: {" + key + "=>" + value + "}");
        return value;
    }
    //Sets a value in the database. It can happen during a transaction or outside transaction.
    public void set(String key, String value) {
        cache.put(key, value);
        if (!index.containsKey(key)) {
            index.put(key, new Stack<>());
        }
        //pun aici unde se afla in stiva ultima valoare a cheii
        index.get(key).push(operationsList.size() - 1);
        //cand face commit aici n ar trebui sa fie size -2?
        operationsList.get(operationsList.size() - 1).put(key, value);
    }
}
