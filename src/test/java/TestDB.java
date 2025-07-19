import org.example.TransactionalDB;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;


public class TestDB {
    TransactionalDB db = new TransactionalDB();

    private void valueCheck(String key, String value) {
        db.set("a", value);           // Operation outside a transaction
        db.set("b", value);           // Operation outside a transaction
        db.set("c", value);           // Operation outside a transaction

        assertEquals("1 added should get "+value, value, db.get("a"));
        assertEquals("1 added should get "+value, value, db.get("b"));
        assertEquals("1 added should get "+value, value, db.get("c"));
        System.out.println("");
    }
    //for value has to be value
    @Test
    public void testNonTransactionalSet() {
        System.out.println("testNonTransactionalSet");
        assertNull("nothing added should get nothing", db.get("a"));
        this.valueCheck("", "1");
    }

    //for value has to be value
    @Test
    public void testTransactionalSet() {
        System.out.println("testTransactionalSet");
        db.begin();                 // Begin a transaction
        this.valueCheck("", "2");
    }

    @Test
    public void testTransactionInTransactionSet() {
        System.out.println("testTransactionInTransactionSet");
        db.begin();                 // Begin a transaction
        this.valueCheck("", "2");

        db.begin();
        db.set("c", "3");           // Operation within a nested transaction

        assertEquals("2 added should get 2", "2", db.get("a"));
        assertEquals("2 added should get 2", "2", db.get("b"));
        assertEquals("2 added should get 2", "3", db.get("c"));
        System.out.println("");
    }

    @Test
    public void testTransactionInTransactionSetWithCommit() {
        System.out.println("testTransactionInTransactionSetWithCommit");
        db.begin();                 // Begin a transaction
        this.valueCheck("", "2");

        db.begin();
        db.set("c", "3");           // Operation within a nested transaction

        assertEquals("2 added should get 2", "2", db.get("a"));
        assertEquals("2 added should get 2", "2", db.get("b"));
        assertEquals("2 added should get 3", "3", db.get("c"));
        System.out.println("");
        db.commit();
        assertEquals("2 added should get 2", "2", db.get("a"));
        assertEquals("2 added should get 2", "2", db.get("b"));
        assertEquals("2 added should get 3", "3", db.get("c"));
        System.out.println("");
    }

    @Test
    public void testTransactionInTransactionSetWithCommitAndRollback() {
        System.out.println("testTransactionInTransactionSetWithCommitAndRollback");
        db.begin();                 // Begin a transaction
        this.valueCheck("", "2");

        db.begin();
        db.set("c", "3");           // Operation within a nested transaction

        assertEquals("2 added should get 2", "2", db.get("a"));
        assertEquals("2 added should get 2", "2", db.get("b"));
        assertEquals("2 added should get 2", "3", db.get("c"));
        System.out.println("");
        db.commit();
        assertEquals("2 added should get 2", "2", db.get("a"));
        assertEquals("2 added should get 2", "2", db.get("b"));
        assertEquals("2 added should get 2", "3", db.get("c"));
        System.out.println("");
        db.rollback();
        assertNull("2 added should get 2", db.get("a"));
        assertNull("2 added should get 2", db.get("b"));
        assertNull("2 added should get 2", db.get("c"));
        System.out.println("");
    }
    @Test
    public void testTransactionInTransactionSetWithCommitAndRollbackOutsideTransaction() {
        System.out.println("testTransactionInTransactionSetWithCommitAndRollbackOutsideTransaction");
        this.valueCheck("", "1");
        db.begin();                 // Begin a transaction
        this.valueCheck("", "2");

        db.begin();
        db.set("c", "3");           // Operation within a nested transaction

        assertEquals("2 added should get 2", "2", db.get("a"));
        assertEquals("2 added should get 2", "2", db.get("b"));
        assertEquals("2 added should get 2", "3", db.get("c"));
        System.out.println("");
        db.commit();
        assertEquals("2 added should get 2", "2", db.get("a"));
        assertEquals("2 added should get 2", "2", db.get("b"));
        assertEquals("2 added should get 2", "3", db.get("c"));
        System.out.println("");
        db.rollback();
        assertEquals("2 added should get 2", "1", db.get("a"));
        assertEquals("2 added should get 2", "1", db.get("b"));
        assertEquals("2 added should get 2", "1", db.get("c"));
        System.out.println("");
    }

    @Test
    public void testTransactionInTransactionSetWithCommitAndRollbackSimple() {
        System.out.println("testTransactionInTransactionSetWithCommitAndRollbackSimple");
        db.begin();
        db.set("c", "1");           // Operation within a nested transaction
        assertEquals("1 added should get 1", "1", db.get("c"));
        db.begin();
        db.set("c", "3");           // Operation within a nested transaction

        assertEquals("3 added should get 3", "3", db.get("c"));

        db.commit();
        assertEquals("3 added should get 3", "3", db.get("c"));

        db.rollback();
        assertNull("null added should get null", db.get("c"));
    }

    @Test
    public void testTransactionInTransactionSetWithRollbackSimple() {
        System.out.println("testTransactionInTransactionSetWithCommitAndRollbackSimple");

        db.begin();
        db.set("c", "1");           // Operation within a nested transaction
        db.begin();
        db.set("c", "3");           // Operation within a nested transaction

        assertEquals("3 added should get 3", "3", db.get("c"));

        db.rollback();
        assertEquals("1 added should get 1", "1", db.get("c"));
    }
}
