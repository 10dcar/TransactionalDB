package org.example;

import java.util.*;

/*
* get tre sa citeasca cea mai recenta valoare a variabilei chiar daca nu s a facut commit ul
* hashmap ok, stiva de hashmap ok
* variabila mutex =>  transcationCount
* */
public class TransactionalDB {
    HashMap<String, String> db = new HashMap<>();
    Stack<HashMap<String, String>> operationsStack = new Stack<>();
    HashMap<String, String> transactionOperation;
    //Map<String, Stack<String>> getMap = new HashMap<>();

    //cum functioneaza in prezent:
    //face un stack si un hashmap
    //de fiecare data cand face un add() adauga in baza de date daca nu e begin() sau in stiva creand un nou hashmap
    //cand da commit baga tot ce e in stiva in bd pana cand intalneste begin
    //rollback sterge tot ce e in stiva pana la begin
    //cautare cauta in stack sau in bd cu complexitate O(n^2)

    //cum vreau sa fac:
    //face un stack si un hashmap
    //de fiecare data cand da add() adauga in baza de date sau intr un hashmap (care se completeaza, adauga in stack daca da commit si trece la altul atunci cand da un nou begin)
    //cand da rollback sterge totul din hashmap ul curent si se pune pe hashmap ul precedent salvat in stack
    //cautare cauta in fiecare hashmap din stiva sau poate folosi o structura de date paralela specializata pe cautare

    //Begins a transaction
    public boolean begin(){
        //operationsStack.size()
        if(this.transactionOperation != null){
            this.operationsStack.add(this.transactionOperation);
        }
        this.transactionOperation = new HashMap<>();
        this.operationsStack.add(this.transactionOperation);
        //this.transactionOperation.put("", "");

        return true;
    }
    //Commits everything that happened after the begin
    public boolean commit(){
        //cand da commit tre sa bage tot ce are in hashmap in bd si sa elimine ultimul hashmap
        //daca nu are nimic in hashmap tre sa bage tot ce are in stiva in baza de date
        if((this.operationsStack != null) && (!this.operationsStack.isEmpty())) {
            HashMap<String, String> keyValue = this.operationsStack.pop();
            //daca are deja o tranzactie in desfasurare si dau commit
            //tre sa bage in tranzactia precedenta sau sa mentina status quoul
            //baga in tranzactia precedenta conform cu chatgpt
            if(keyValue != null) {
                //for (Map.Entry<String, String> entry : keyValue.entrySet()) {
                    //db.put(entry.getKey(), entry.getValue());
                    this.operationsStack.add(keyValue);
                //}
            }

            return true;
        } /*else if(this.transactionOperation != null) {
            for (Map.Entry<String, String> entry : this.transactionOperation.entrySet()) {
                db.put(entry.getKey(), entry.getValue());
            }
        }*/
        return false;
    }
    //Removes everything after the begin
    public boolean rollback(){
        if((this.operationsStack != null) && (!operationsStack.isEmpty())) {
            System.out.println("HM remx {" + this.operationsStack.size() + " : " + this.operationsStack.lastElement() + "}");
            //aici scoate din stiva cu pop()
            HashMap<String, String> keyValue = this.operationsStack.pop();
            if(keyValue != null) {
                for (Map.Entry<String, String> entry : keyValue.entrySet()) {
                    System.out.println("HM rem {" + entry.getKey() + " : " + entry.getValue() + "}");
                    //aici pune in hasmap nu in stiva cu lastElement().put()
                    this.operationsStack.lastElement().put(entry.getKey(), entry.getValue());
                }
                /*for (Map.Entry<String, String> entry : keyValue.entrySet()) {
                    if ((!this.getMap.get(entry.getKey()).isEmpty())) {
                        this.getMap.get(entry.getKey()).pop();
                    }
                }*/
            }
            //la rollback ar trebui sa elimin tot ce e in hashmap ul curent si tot ce e in hashmap ul de cautare
            //dar in hashmap ul de cautare s ar putea sa fie suprascrise valori deci daca elimin pe ultimul nu gasesc precedentul
            return true;
        }
        return false;
    }
    //Gets a value from the database. It can happen during a transaction or outside a transaction.
    public String get(String key){
        String value = "";
        /*if (this.getMap != null) {
            // sa o fac pe asta in o(1) ar fi un hashmap
            //neh: cautarea direct in hashmap se face in O(1)
            if((this.getMap.get(key) != null) && (!getMap.get(key).empty())){
                value = this.getMap.get(key).peek();
                if (!value.isEmpty()) {
                    System.out.println("Get: " + key + "=>" + value);
                    return value;
                }
            }
        }*/
        //get() ul tre sa treaca prin stiva de hashmap si sa ia fiecare hashmap si sa caute cheia
        value = db.get(key);
        System.out.println("Get: " + key + "=>" + value);
        return value;
    }
    //Sets a value in the database. It can happen during a transaction or outside transaction.
    public boolean set(String key, String value) {
        if((this.transactionOperation != null)){
            System.out.println("Add stack: " + key + "=>" + value);
            //baga in arraylist toate operatiile care se fac intr o tranzactie
            //si daca mai dau un begin bag arraylist ul precedent in stiva si fac un nou arraylist
            //ar fi trebuit sa fac asa cu hashmap
            this.transactionOperation.put(key, value);
            this.getMap.computeIfAbsent(key, k ->  new Stack<>()).add(value);

            //trebuie bagate toate dintr o tranzactie intr un hash map si abia acela adaugat in stiva
            //cand dau un nou begin tre sa adaug this.transactionOperation in stiva si sa creez un nou this.
            return true;
        } else {
            System.out.println("Add db: " + key + "=>" + value);
            db.put(key, value);
            return true;
        }
    }
}
