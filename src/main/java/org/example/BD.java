package org.example;

interface DB {
    public boolean begin();
    public boolean commit();
    public boolean rollback();
    public String get(String key);
    public void set(String key, String value);
}