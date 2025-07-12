package org.example;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        TransactionalDB db = new TransactionalDB();

        db.set("a", "1");          // Operation outside a transaction
        db.set("b", "1");          // Operation outside a transaction
        db.set("c", "1");          // Operation outside a transaction
        db.get("a");
        db.get("b");
        db.get("c");
        System.out.println("No transaction");
        db.begin();                // Begin a transaction
        db.set("a", "2");
        db.set("b", "2");          // Operation within a transaction
        db.set("c", "2");
        db.get("a");
        db.get("b");
        db.get("c");
        System.out.println("Transaction not committed");
        db.begin();                // Begin a nested transaction
        //daca da acum commit la 3 si dupa da din nou commit la tranzactia precedenta
        //tre sa ramana ultima valoare adica 3
        //daca da rollback la 2 tre sa dea rollback si la 3 chiar daca a dat commit la 3
        //deoarece 3 este o tranzactie in interiorul tranzactiei 2
        db.set("c", "3");          // Operation within a nested transaction
        db.get("a");
        db.get("b");
        db.get("c");
        System.out.println("First&Second transaction not committed");
        //db.begin();
        db.commit();              // Commit the nested transaction into the parent transaction
        //db.set("b", "3");
        db.get("a");
        db.get("b");
        db.get("c");               // Returns "3" (Inside parent-child transaction was committed)
        System.out.println("Second transaction committed");
        db.rollback();             // Rollback the parent transaction, discarding changes from current and nested transaction
        db.get("a");               // Returns "1"
        db.get("b");               // Returns "1" (as the transaction was rolled back)
        db.get("c");               // Returns "1" (as the transaction was rolled back)
        System.out.println("First transaction rolled back");
        //cel mai simplu test
        //am la dispozitie set(), get(), begin(), commit(), rollback()
/*        db.set("a", "1");
        db.set("b", "1");
        db.set("c", "1");
        db.get("a");
        db.get("b");
        db.get("c");
        //db.begin();
*/
 /*       db.set("a", "1");          // Operation outside a transaction
        db.set("b", "1");          // Operation outside a transaction
        db.set("c", "1");          // Operation outside a transaction
        db.get("a");
        db.get("b");
        db.get("c");
        db.begin();                // Begin a transaction
        db.set("b", "2");          // Operation within a transaction
        db.get("a");
        db.get("b");
        db.get("c");
        db.begin();                // Begin a nested transaction
        db.set("c", "3");          // Operation within a nested transaction
        db.commit();               // Commit the nested transaction into the parent transaction
        db.get("a");
        db.get("b");
        db.get("c");
        db.commit();               // Commit the nested transaction into the parent transaction
        db.get("a");
        db.get("b");
        db.get("c");
  */
   /*   db.get("c");               // Returns "3" (Inside parent-child transaction was committed)
        db.rollback();             // Rollback the parent transaction, discarding changes from current and nested transaction
        db.get("a");               // Returns "1"
        db.get("b");               // Returns "1" (as the transaction was rolled back)
        db.get("c");               // Returns "1" (as the transaction was rolled back)

        TransactionalDB db = new TransactionalDB();
        db.set("a", "a");         // // Operation outside a transaction
        db.begin();
        db.set("b", "b");         // // Operation outside a transaction
        db.rollback();
        db.set("c", "c") ;        // // Operation outside a transaction

        db.commit();
        db.get("c");
        //db.set("c", "d") ;
        //db.set("a", "x") ;
        //db.commit();
        db.get("c");
        db.get("b");
        db.get("a");
        db.begin();             // // Begin a transaction
        db.set("b", "2");         // // Operation within a transaction
        db.begin();               // // Begin a nested transaction
        db.set("c", "3");         // // Operation within a nested transaction
        db.commit();              // // Commit the nested transaction into the parent transaction
        db.get("c");              // // Returns "3" (Inside parent-child transaction was committed)
        db.rollback();            // // Rollback the parent transaction, discarding changes from current and nested transaction
        db.get("a");              // // Returns "1"
        db.get("b");              // // Returns "1" (as the transaction was rolled back)
        db.get("c");              // // Returns "1" (as the transaction was rolled back)*/
    }
}
