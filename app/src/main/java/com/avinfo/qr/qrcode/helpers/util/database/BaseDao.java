package com.avinfo.qr.qrcode.helpers.util.database;


import java.util.List;

import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Update;
import io.reactivex.Completable;

public interface BaseDao<T> {
    /**
     * Insert a entity in the database. If the entity already exists, replace it.
     *
     * @param entity the entity to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long[] insert(T... entity);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insert(T entity);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long[] insertBulk(List<T> entity);

    /**
     * Update a entity in the database.
     *
     * @param entity the entity to be updated.
     */
    @Update(onConflict = OnConflictStrategy.REPLACE)
    int update(T... entity);

    /**
     * Delete a entity in the database.
     *
     * @param entity the entity to be delete.
     */
    @Delete
    void delete(T... entity);

    @Delete
    int delete(T entity);
}
