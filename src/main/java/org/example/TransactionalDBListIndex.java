package org.example;

import java.util.*;

//1 vreau sa iau codul si sa ma uit peste el sa spun exact ce face
//2 vreau sa iau problema si sa aflu cerinta exacta

//a o lista cu operatii fiecare operatie tinuta intr un hash map
//b un cache
//c un hasmap in care sa tin cate un stack

//begin -> adauga un hasmap in operatii
//get -> cauta in cache
//set -> pune in cache, pune in index dupa o anumita cheie creaind un stack (eventual creaza un nou stack daca cheia nu exista deja) si in operatii la ultima
//commit ->
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
            // ma duc in operation list la ultima pozitie si iau hash ul si parcurg fiecare elemnent al hash ului
            //ma duc si il caut index pentru ca urmeaza sa sterg ultimul hash din operation list il sterg si din index
            if(operationsList.size() < 2) {
                return false;
            }
            // ia toate valorile care sunt comise
            HashMap<String, String> hashMapLast = operationsList.get(operationsList.size() - 1);
            operationsList.remove(operationsList.size() - 1);
            // cicleaza prin valorile comise scoase mai sus
            for (Map.Entry<String, String> entry : hashMapLast.entrySet()) {
                Stack<Integer> stackEntry = index.get(entry.getKey());
                if ((stackEntry != null)) { //&& (stackEntry.peek() >= operationsList.size() - 1)
                    //sar peste prima valoare care este defapt pentru stiva cu valoarea curenta size() - 1
                    stackEntry.pop();
                    //Local class set() method !! tre sa faca set in operations list precedent nu in curent
                    //this.set(entry.getKey(), entry.getValue());

                    /*if (!index.containsKey(entry.getKey())) {
                        index.put(entry.getKey(), new Stack<>());
                    }*/
                    //pun aici unde se afla in stiva ultima valoare a cheii
                    //index.get(entry.getKey()).push(operationsList.size() - 1);
                    //operationsList.get(operationsList.size() - 1).remove(entry.getKey()); //scoate valoarea precedenta adica comisa
                    operationsList.get(operationsList.size() - 1).put(entry.getKey(), entry.getValue()); //adauga valoarea care a fost comisa
                /* // potential fix
                //cache.remove(entry.getKey());
                cache.put(entry.getKey(), entry.getValue());
                operationsList.get(operationsList.size() - 2).put(entry.getKey(), entry.getValue());
                * */
                } /*else {
                cache.remove(entry.getKey());
            }*/
            }
            return true;
        }
        return false;
    }

    //DE FACUT INDEX
    //Removes everything after the begin
    // ROLLBACK should: remove all the first values in the stacks of indexes that are present in the last hash map of the operations list, then for each of the keys in the last hash map of the operations list, it should check in the stack of index where is the next value for that key and put it back in cache
    public boolean rollback(){
        //tin un index pentru stack in care spun in ce hasmap al stackului gasesc ultima sau penultima valoare a unei chei

        //DECI CE FAC: IAU ULTIMUL HASH MAP DIN OPERATIONS LIST SI IL PARCURG
        //ELIMIN TOATE PRIMELE ELEMENTE DIN STACKURILE DE INDEX CARE SUNT PENTRU CHEILE DIN HASH MAPUL ULTIMULUI ELEMENT DIN OPERATIONS LIST
        //APOI SCOT DIN INDEX UNDE SUNT IN OPERATIONS LIST URMATOARELE VALORI SI PUN VALORILE IN CACHE
        //OR acc to CLAUDE
        /*
         i do not want it explained with examples for now. just pick the last element in operations, move through it and remove all from index stacks, then get the previous in operations list and add to cache or so

● What rollback does to the data structures

  1. Take the last HashMap from operationsList. This is the inner transaction's write-set — the list of keys to revert. You don't modify it; it's just the to-do list.
  2. Walk that HashMap's keys. For each key:
    2. a. Go to index[key] — a stack of transaction indices that wrote this key.
    2. b. Pop the top entry of that stack. The top is the inner transaction's index (the one being rolled back). Removing it means "this transaction is no longer claiming this key."
    2. c. Peek the new top. This is the index of the most recent outer transaction that wrote the key.
        - If the stack is now empty: no outer tx ever wrote this key → cache.remove(key).
        - Otherwise: look up operationsList[newTop].get(key) → that's the previous value → cache.put(key, that value).
  3. Remove the inner HashMap from operationsList. The transaction is gone.

  That's the whole flow. Per data structure:
  - operationsList: read the last HashMap, then drop it. Outer HashMaps are read-only references for restoring values.
  - index: one pop per key in the inner HashMap. Empty stacks stay empty.
  - cache: one put (restore) or remove (nothing to restore) per key in the inner HashMap. Keys not in the inner HashMap are untouched.
        * */
        if(operationsList.size() < 1) {
            return false;
        }
        if(!operationsList.isEmpty()) {
            HashMap<String, String> hashMapLast = operationsList.get(operationsList.size() - 1);
            for (Map.Entry<String, String> entry : hashMapLast.entrySet()) {
                //cache.remove(entry.getKey());
                //assert daca nu sunt sigur ca e bun if ul
                //sar peste prima valoare care este defapt pentru stiva cu valoarea curenta size() - 1
                String operationsKeyForTransaction = entry.getKey();
                Stack<Integer> stackEntry = index.get(operationsKeyForTransaction); //ma duc in hasmap ul de index si citesc pentru fiecare cheie unde se mai afla in opList
                stackEntry.pop();

                //ce tre sa faca daca da rolback?
                //cache.remove(entry.getKey());
                // PUN CHEIA PE CARE AM STERS O IN CACHE
                //VALOAREA EI O IAU DIN OPERTAIONS LIST, MA DUC IN INDEX SI CAUT ACOLO OPERATIONS LIST IN CARE SE GASESTE,
                //SELECTEZ ACEL OPERATIONS LIST SI IAU HASH MAPUL DE LA EL ACOLO IN ACEL HASHMAP CAUT VALOAREA
                //O IAU SI O PUN IN CACHE
                //try
                if(stackEntry.isEmpty()) {
                    cache.put(operationsKeyForTransaction, operationsList.get(stackEntry.peek()).get(operationsKeyForTransaction));
                } else {
                //catch (EmptyStackException e) {
                    cache.remove(entry.getKey());
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
