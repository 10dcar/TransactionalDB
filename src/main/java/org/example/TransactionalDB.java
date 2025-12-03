package org.example;

import java.util.*;

public class TransactionalDB implements DB {
    Stack<HashMap<String, String>> operationsStack = new Stack<>();
    HashMap<String, String> cache = new HashMap<>();

    public TransactionalDB (){
        this.begin();
    }
    //Begins a transaction
    public boolean begin(){
        operationsStack.add(new HashMap<>());

        return true;
    }
    //Commits everything that happened after the begin
    public boolean commit(){
        if(!operationsStack.isEmpty()) {
            for (Map.Entry<String, String> entry : operationsStack.pop().entrySet()) {
                //Local class set() method
                this.set(entry.getKey(), entry.getValue());
            }
            return true;
        }
        return false;
    }
    //Removes everything after the begin
    //ce putem face ca sa nu mai avem for in for?
    //scopul primului for este sa treava prin toate cheile din varful stivei
    //scopul celui de al doilea for este sa caute in toat stack cheia scoasa si sa o puna la loc in cache
    //unul dintre ele trebuie eliminat
    //de vazut programul cu stringuri si lamurit cazurile
    public boolean rollback(){
        //parcurg stiva mai putin ultimul element
        //bag tot ce gasesc in hashmap ul de index
        //apoi parcurg tot cache ul si actualizez valorile cu cele din index
        if(!operationsStack.isEmpty()) {
            //parcurge varful stivei care au fost sterse
            for (Map.Entry<String, String> entry : operationsStack.pop().entrySet()) {
                //doar daca am eliminat cheia tre sa o mai si adaug !!!
                cache.remove(entry.getKey());
                //atata timp cat parcurg elementele din stiva functionalitatea de stiva este tradata
                //(teancul de farfurii) - obervatie importanta si tre agaugata in documentatie

                //parcurge celelalte elemente ale stivei pentru a identifica valorile lor precedente cele mai recente
                ListIterator<HashMap<String, String>> iterator = operationsStack.listIterator(operationsStack.size());
                //parcurg toata stiva si bag intr un hashmap toate valorile gasite
                //apoi parcurg tot cache ul si actualizez valorile cu cele din hashmap
                while (iterator.hasPrevious()) {
                    HashMap<String, String> entry_src = iterator.previous();
                    if(entry_src != null && entry_src.containsKey(entry.getKey())) {
                        cache.put(entry.getKey(), entry_src.get(entry.getKey()));
                        break;
                    }
                }
            }
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
        //in loc sa stocam dubluri perfecte am putea folosi pointeri
        //dar in java toate lucrurile sunt pointeri si deci nu dubleaza memoria pentru stringurile value
        //in java stringul are StringBuilder() rapid in concatenare
        //si String() rapid in toate operatiile mai putin concatenare
        cache.put(key, value);
        operationsStack.lastElement().put(key, value);
    }
}

