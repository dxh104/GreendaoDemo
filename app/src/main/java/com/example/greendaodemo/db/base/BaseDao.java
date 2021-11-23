package com.example.greendaodemo.db.base;

import android.app.Activity;
import android.database.sqlite.SQLiteConstraintException;

import com.example.greendaodemo.db.DaoThreadPool;

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
    private Activity activity;//传activity就在主线程回调，不传就在子线程回调

    public BaseDao(AbstractDao dao) {
        mDao = dao;
    }

    public BaseDao(AbstractDao dao, Activity activity) {
        this.mDao = dao;
        this.activity = activity;
    }

    //将给定的实体插入数据库
    public long save(T item) {
        try {
            return mDao.insert(item);
        } catch (SQLiteConstraintException e) {
            e.printStackTrace();
        }
        return 0;
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
    public long saveOrUpdate(T item) {
        return mDao.insertOrReplace(item);
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
    public boolean detach(T item) {
        return mDao.detach(item);
    }

    //将给定的实体插入数据库
    public void save(final OnDaoExcuteCallBack<Long> onDaoExcuteCallBack, final T item) {
        executeSingleRunnable(new Runnable() {
            @Override
            public void run() {
                try {
                    final long insert = mDao.insert(item);
                    runUI(new Runnable() {
                        @Override
                        public void run() {
                            if (onDaoExcuteCallBack != null) {
                                if (insert > 0) {
                                    onDaoExcuteCallBack.OnDaoExcuteResult(null, insert);
                                } else {
                                    onDaoExcuteCallBack.OnDaoExcuteResult(new Throwable("添加数据失败"), insert);
                                }
                            }
                        }
                    });
                } catch (final SQLiteConstraintException e) {
                    e.printStackTrace();
                    runUI(new Runnable() {
                        @Override
                        public void run() {
                            if (onDaoExcuteCallBack != null) {
                                onDaoExcuteCallBack.OnDaoExcuteResult(e, (long) 0);
                            }
                        }
                    });
                }
            }
        });
    }

    //使用事务操作，将给定的实体插入数据库
    public void save(final OnDaoExcuteCallBack onDaoExcuteCallBack, final T... items) {
        executeSingleRunnable(new Runnable() {
            @Override
            public void run() {
                try {
                    mDao.insertInTx(items);
                    runUI(new Runnable() {
                        @Override
                        public void run() {
                            if (onDaoExcuteCallBack != null) {
                                onDaoExcuteCallBack.OnDaoExcuteResult(null, null);
                            }
                        }
                    });
                } catch (final SQLiteConstraintException e) {
                    e.printStackTrace();
                    runUI(new Runnable() {
                        @Override
                        public void run() {
                            if (onDaoExcuteCallBack != null) {
                                onDaoExcuteCallBack.OnDaoExcuteResult(e, null);
                            }
                        }
                    });
                }
            }
        });
    }

    //使用事务操作，将给定的实体集合插入数据库
    public void save(final OnDaoExcuteCallBack onDaoExcuteCallBack, final List<T> items) {
        executeSingleRunnable(new Runnable() {
            @Override
            public void run() {
                try {
                    mDao.insertInTx(items);
                    runUI(new Runnable() {
                        @Override
                        public void run() {
                            if (onDaoExcuteCallBack != null) {
                                onDaoExcuteCallBack.OnDaoExcuteResult(null, null);
                            }
                        }
                    });
                } catch (final SQLiteConstraintException e) {
                    e.printStackTrace();
                    runUI(new Runnable() {
                        @Override
                        public void run() {
                            if (onDaoExcuteCallBack != null) {
                                onDaoExcuteCallBack.OnDaoExcuteResult(e, null);
                            }
                        }
                    });
                }
            }
        });
    }

    //将给定的实体插入数据库，若此实体类存在，则覆盖
    public void saveOrUpdate(final OnDaoExcuteCallBack<Long> onDaoExcuteCallBack, final T item) {
        executeSingleRunnable(new Runnable() {
            @Override
            public void run() {
                //数据库操作
                final long insertOrReplace = mDao.insertOrReplace(item);
                //UI
                runUI(new Runnable() {
                    @Override
                    public void run() {
                        if (onDaoExcuteCallBack != null) {
                            onDaoExcuteCallBack.OnDaoExcuteResult(null, insertOrReplace);
                        }
                    }
                });
            }
        });
    }

    //使用事务操作，将给定的实体插入数据库，若此实体类存在，则覆盖
    public void saveOrUpdate(final OnDaoExcuteCallBack onDaoExcuteCallBack, final T... items) {
        executeSingleRunnable(new Runnable() {
            @Override
            public void run() {
                //数据库操作
                mDao.insertOrReplaceInTx(items);
                //UI
                runUI(new Runnable() {
                    @Override
                    public void run() {
                        if (onDaoExcuteCallBack != null) {
                            onDaoExcuteCallBack.OnDaoExcuteResult(null, null);
                        }
                    }
                });
            }
        });
    }

    //使用事务操作，将给定的实体集合插入数据库，若此实体类存在，则覆盖
    public void saveOrUpdate(final OnDaoExcuteCallBack onDaoExcuteCallBack, final List<T> items) {
        executeSingleRunnable(new Runnable() {
            @Override
            public void run() {
                //数据库操作
                mDao.insertOrReplaceInTx(items);
                //UI
                runUI(new Runnable() {
                    @Override
                    public void run() {
                        if (onDaoExcuteCallBack != null) {
                            onDaoExcuteCallBack.OnDaoExcuteResult(null, null);
                        }
                    }
                });
            }
        });
    }

    //从数据库中删除给定Key所对应的实体
    public void deleteByKey(final OnDaoExcuteCallBack onDaoExcuteCallBack, final K key) {
        executeSingleRunnable(new Runnable() {
            @Override
            public void run() {
                //数据库操作
                mDao.deleteByKey(key);
                //UI
                runUI(new Runnable() {
                    @Override
                    public void run() {
                        if (onDaoExcuteCallBack != null) {
                            onDaoExcuteCallBack.OnDaoExcuteResult(null, null);
                        }
                    }
                });
            }
        });
    }

    //从数据库中删除给定的实体
    public void delete(final OnDaoExcuteCallBack onDaoExcuteCallBack, final T item) {
        executeSingleRunnable(new Runnable() {
            @Override
            public void run() {
                //数据库操作
                mDao.delete(item);
                //UI
                runUI(new Runnable() {
                    @Override
                    public void run() {
                        if (onDaoExcuteCallBack != null) {
                            onDaoExcuteCallBack.OnDaoExcuteResult(null, null);
                        }
                    }
                });
            }
        });
    }

    //使用事务操作删除数据库中给定的实体
    public void delete(final OnDaoExcuteCallBack onDaoExcuteCallBack, final T... items) {
        executeSingleRunnable(new Runnable() {
            @Override
            public void run() {
                //数据库操作
                mDao.deleteInTx(items);
                //UI
                runUI(new Runnable() {
                    @Override
                    public void run() {
                        if (onDaoExcuteCallBack != null) {
                            onDaoExcuteCallBack.OnDaoExcuteResult(null, null);
                        }
                    }
                });
            }
        });
    }

    //使用事务操作删除数据库中给定实体集合中的实体
    public void delete(final OnDaoExcuteCallBack onDaoExcuteCallBack, final List<T> items) {
        executeSingleRunnable(new Runnable() {
            @Override
            public void run() {
                //数据库操作
                mDao.deleteInTx(items);
                //UI
                runUI(new Runnable() {
                    @Override
                    public void run() {
                        if (onDaoExcuteCallBack != null) {
                            onDaoExcuteCallBack.OnDaoExcuteResult(null, null);
                        }
                    }
                });
            }
        });
    }

    //删除数据库中全部数据
    public void deleteAll(final OnDaoExcuteCallBack onDaoExcuteCallBack) {
        executeSingleRunnable(new Runnable() {
            @Override
            public void run() {
                //数据库操作
                mDao.deleteAll();
                //UI
                runUI(new Runnable() {
                    @Override
                    public void run() {
                        if (onDaoExcuteCallBack != null) {
                            onDaoExcuteCallBack.OnDaoExcuteResult(null, null);
                        }
                    }
                });
            }
        });
    }

    //更新给定的实体
    public void update(final OnDaoExcuteCallBack onDaoExcuteCallBack, final T item) {
        executeSingleRunnable(new Runnable() {
            @Override
            public void run() {
                //数据库操作
                mDao.update(item);
                //UI
                runUI(new Runnable() {
                    @Override
                    public void run() {
                        if (onDaoExcuteCallBack != null) {
                            onDaoExcuteCallBack.OnDaoExcuteResult(null, null);
                        }
                    }
                });
            }
        });
    }

    //使用事务操作，更新给定的实体
    public void update(final OnDaoExcuteCallBack onDaoExcuteCallBack, final T... items) {
        executeSingleRunnable(new Runnable() {
            @Override
            public void run() {
                //数据库操作
                mDao.updateInTx(items);
                //UI
                runUI(new Runnable() {
                    @Override
                    public void run() {
                        if (onDaoExcuteCallBack != null) {
                            onDaoExcuteCallBack.OnDaoExcuteResult(null, null);
                        }
                    }
                });
            }
        });
    }

    //使用事务操作更新数据库中给定实体集合中的实体
    public void update(final OnDaoExcuteCallBack onDaoExcuteCallBack, final List<T> items) {
        executeSingleRunnable(new Runnable() {
            @Override
            public void run() {
                //数据库操作
                mDao.updateInTx(items);
                //UI
                runUI(new Runnable() {
                    @Override
                    public void run() {
                        if (onDaoExcuteCallBack != null) {
                            onDaoExcuteCallBack.OnDaoExcuteResult(null, null);
                        }
                    }
                });
            }
        });
    }

    //加载给定主键的实体
    public void query(final OnDaoExcuteCallBack<T> onDaoExcuteCallBack, final K key) {
        executeSingleRunnable(new Runnable() {
            @Override
            public void run() {
                //数据库操作
                final T load = mDao.load(key);
                //UI
                runUI(new Runnable() {
                    @Override
                    public void run() {
                        if (onDaoExcuteCallBack != null) {
                            onDaoExcuteCallBack.OnDaoExcuteResult(null, load);
                        }
                    }
                });
            }
        });
    }

    //加载数据库中所有的实体
    public void queryAll(final OnDaoExcuteCallBack<List<T>> onDaoExcuteCallBack) {
        executeSingleRunnable(new Runnable() {
            @Override
            public void run() {
                //数据库操作
                final List<T> loadAll = mDao.loadAll();
                //UI
                runUI(new Runnable() {
                    @Override
                    public void run() {
                        if (onDaoExcuteCallBack != null) {
                            onDaoExcuteCallBack.OnDaoExcuteResult(null, loadAll);
                        }
                    }
                });
            }
        });
    }

    //根据条件查询集合
    public void query(final OnDaoExcuteCallBack<List<T>> onDaoExcuteCallBack, final String where, final String... params) {
        executeSingleRunnable(new Runnable() {
            @Override
            public void run() {
                //数据库操作
                final List<T> queryRaw = mDao.queryRaw(where, params);
                //UI
                runUI(new Runnable() {
                    @Override
                    public void run() {
                        if (onDaoExcuteCallBack != null) {
                            onDaoExcuteCallBack.OnDaoExcuteResult(null, queryRaw);
                        }
                    }
                });
            }
        });
    }

    //获取数据库中数据的数量
    public void count(final OnDaoExcuteCallBack<Long> onDaoExcuteCallBack) {
        executeSingleRunnable(new Runnable() {
            @Override
            public void run() {
                //数据库操作
                final long count = mDao.count();
                //UI
                runUI(new Runnable() {
                    @Override
                    public void run() {
                        if (onDaoExcuteCallBack != null) {
                            onDaoExcuteCallBack.OnDaoExcuteResult(null, count);
                        }
                    }
                });
            }
        });
    }

    //刷新缓存
    public void refresh(final OnDaoExcuteCallBack onDaoExcuteCallBack, final T item) {
        executeSingleRunnable(new Runnable() {
            @Override
            public void run() {
                //数据库操作
                mDao.refresh(item);
                //UI
                runUI(new Runnable() {
                    @Override
                    public void run() {
                        if (onDaoExcuteCallBack != null) {
                            onDaoExcuteCallBack.OnDaoExcuteResult(null, null);
                        }
                    }
                });
            }
        });
    }

    //清除缓存
    public void detach(final OnDaoExcuteCallBack<Boolean> onDaoExcuteCallBack, final T item) {
        executeSingleRunnable(new Runnable() {
            @Override
            public void run() {
                //数据库操作
                final boolean detach = mDao.detach(item);
                //UI
                runUI(new Runnable() {
                    @Override
                    public void run() {
                        if (onDaoExcuteCallBack != null) {
                            if (detach) {
                                onDaoExcuteCallBack.OnDaoExcuteResult(null, detach);
                            } else {
                                onDaoExcuteCallBack.OnDaoExcuteResult(new Throwable("清除缓存失败"), detach);
                            }
                        }
                    }
                });
            }
        });
    }

    private void runUI(Runnable runnable) {
        if (activity != null) {
            activity.runOnUiThread(runnable);//UI线程执行
        } else {
            runnable.run();//当前线程执行
        }
    }

    private void executeSingleRunnable(Runnable runnable) {
        DaoThreadPool.getInstance().executeSingleRunnable(runnable);
    }

    public interface OnDaoExcuteCallBack<Bean> {
        void OnDaoExcuteResult(Throwable throwable, Bean bean);//throwable==null代表成功
    }
}
