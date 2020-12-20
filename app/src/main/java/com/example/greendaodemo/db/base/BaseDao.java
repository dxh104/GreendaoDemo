package com.example.greendaodemo.db.base;

import android.database.sqlite.SQLiteConstraintException;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;

/**
 * Created by XHD on 2020/12/10
 * 代理模式封装Dao一些常用方法
 */
public class BaseDao<T, K> {
    //<实体类,主键>
    private AbstractDao<T, K> mDao;


    public BaseDao(AbstractDao dao) {
        mDao = dao;
    }

    //将给定的实体插入数据库
    public void save(T item) {
        try {
            mDao.insert(item);
        } catch (SQLiteConstraintException e) {
            e.printStackTrace();
        }
    }

    //使用事务操作，将给定的实体插入数据库
    public void save(T... items) {
        try {
            mDao.insertInTx(items);
        } catch (SQLiteConstraintException e) {
            e.printStackTrace();
        }
    }

    //使用事务操作，将给定的实体集合插入数据库
    public void save(List<T> items) {
        try {
            mDao.insertInTx(items);
        } catch (SQLiteConstraintException e) {
            e.printStackTrace();
        }
    }

    //将给定的实体插入数据库，若此实体类存在，则覆盖
    public void saveOrUpdate(T item) {
        mDao.insertOrReplace(item);
    }

    //使用事务操作，将给定的实体插入数据库，若此实体类存在，则覆盖
    public void saveOrUpdate(T... items) {
        mDao.insertOrReplaceInTx(items);
    }

    //使用事务操作，将给定的实体集合插入数据库，若此实体类存在，则覆盖
    public void saveOrUpdate(List<T> items) {
        mDao.insertOrReplaceInTx(items);
    }

    //从数据库中删除给定Key所对应的实体
    public void deleteByKey(K key) {
        mDao.deleteByKey(key);
    }

    //从数据库中删除给定的实体
    public void delete(T item) {
        mDao.delete(item);
    }

    //使用事务操作删除数据库中给定的实体
    public void delete(T... items) {
        mDao.deleteInTx(items);
    }

    //使用事务操作删除数据库中给定实体集合中的实体
    public void delete(List<T> items) {
        mDao.deleteInTx(items);
    }

    //删除数据库中全部数据
    public void deleteAll() {
        mDao.deleteAll();
    }

    //更新给定的实体
    public void update(T item) {
        mDao.update(item);
    }

    //使用事务操作，更新给定的实体
    public void update(T... items) {
        mDao.updateInTx(items);
    }

    //使用事务操作更新数据库中给定实体集合中的实体
    public void update(List<T> items) {
        mDao.updateInTx(items);
    }

    //加载给定主键的实体
    public T query(K key) {
        return mDao.load(key);
    }

    //加载数据库中所有的实体
    public List<T> queryAll() {
        return mDao.loadAll();
    }

    //根据条件查询集合
    public List<T> query(String where, String... params) {
        return mDao.queryRaw(where, params);
    }

    //返回queryBuilder
    public QueryBuilder<T> queryBuilder() {
        return mDao.queryBuilder();
    }

    //获取数据库中数据的数量
    public long count() {
        return mDao.count();
    }

    //刷新缓存
    public void refresh(T item) {
        mDao.refresh(item);
    }

    //清除缓存
    public void detach(T item) {
        mDao.detach(item);
    }
}
