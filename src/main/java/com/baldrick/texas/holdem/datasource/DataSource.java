package com.baldrick.texas.holdem.datasource;

public interface DataSource<T> {

    public T getById(String id);

    public void deleteById(String id);

    public void update(String id, T t);

    public void insert(String id, T t);
}
