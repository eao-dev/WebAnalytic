package com.webAnalytic.Domains.DAO;

import java.util.List;

public interface DAO<T> {

    String notImplementedExceptionMessage = "This is method is not supported!";

    // Read
    default T getById(long id) throws Exception{throw new Exception(notImplementedExceptionMessage);}
    default T getByObject(T object) throws Exception{throw new Exception(notImplementedExceptionMessage);}

    default List<T> list() throws Exception{throw new Exception(notImplementedExceptionMessage);}
    default List<T> listByObject(Object object) throws Exception{throw new Exception(notImplementedExceptionMessage);}

    // Create
    default boolean create(T object) throws Exception{throw new Exception(notImplementedExceptionMessage);}

    // Update
    default boolean update(T object) throws Exception{throw new Exception(notImplementedExceptionMessage);}

    // Delete
    default boolean deleteByObject(T object) throws Exception{throw new Exception(notImplementedExceptionMessage);}
    default boolean deleteById(long id) throws Exception{throw new Exception(notImplementedExceptionMessage);}

}