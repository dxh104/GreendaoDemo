package com.example.greendaodemo.db.manager;

import android.content.Context;


import com.example.greendaodemo.db.entity.DaoMaster;
import com.example.greendaodemo.db.entity.DaoSession;
import com.example.greendaodemo.db.entity.UserDao;
import com.example.greendaodemo.db.entity.UserFriend;
import com.example.greendaodemo.db.entity.UserFriendDao;
import com.example.greendaodemo.db.helper.DaoOpenHelper;

import org.greenrobot.greendao.query.QueryBuilder;


/**
 * Created by XHD on 2020/12/10
 */
public class DaoManager {
    //todo------可自定义数据库名称
    public static final String DB_NAME = "greendaodemo_database.db";//数据库文件名称
    //多线程中要被共享的使用volatile关键字修饰
    private volatile static DaoManager mDaoManager;
    private DaoMaster.DevOpenHelper mHelper; //创建SQLite数据库的SQLiteOpenHelper的具体实现（数据库升级时的更新方法在内部）
    private DaoMaster mDaoMaster; //GreenDao的顶级对象，作为数据库对象、用于创建表和删除表
    private DaoSession mDaoSession; //管理所有的***Dao对象，***Dao对象中存在着增删改查等API
    private Context context;

    public DaoManager(Context context) {
        this.context = context;
    }

    /**
     * 使用单例模式获得操作数据库的对象
     *
     * @return
     */
    public static DaoManager getInstance(Context context) {
        if (mDaoManager == null) {
            synchronized (DaoManager.class) {
                if (mDaoManager == null) {
                    mDaoManager = new DaoManager(context.getApplicationContext());
                }
            }
        }
        return mDaoManager;
    }

    /**
     * 获取DaoSession
     *
     * @return
     */
    public synchronized DaoSession getDaoSession() {
        if (null == mDaoSession) {
            mDaoSession = getDaoMaster().newSession();
        }
        return mDaoSession;
    }

    /**
     * 设置debug模式开启或关闭，默认关闭
     * 是否打印 数据库的日志信息 默认不开启
     *
     * @param flag
     */
    public void setDebug(boolean flag) {
        QueryBuilder.LOG_SQL = flag;
        QueryBuilder.LOG_VALUES = flag;
    }


    /**
     * 关闭数据库
     */
    public synchronized void closeDataBase() {
        closeDaoSession();
        recycleDaoMaster();
        closeHelper();
    }

    /**
     * 判断数据库是否存在，如果不存在则创建
     *
     * @return
     */
    private DaoMaster getDaoMaster() {
        if (null == mDaoMaster || null == mHelper) {
            //重点1.DaoOpenHelper重写onUpgrade解决升级数据库数据丢失问题（升级数据库时会自动在新表中添加还未被删除的字段的老数据）
            //todo-----数据库发生变化greendao版本号升级后，如果新增了或删除了表对应的实体类，记得更新DaoOpenHelper中的Dao.class
            mHelper = new DaoOpenHelper(context, DB_NAME, null, UserDao.class, UserFriendDao.class);//重点2.可以重写Context中getDatabasePath方法改变数据库文件默认存储路径(app私有目录外记得申请权限)
            mDaoMaster = new DaoMaster(mHelper.getWritableDb());//打开数据库并获取，再添加到DaoMaster中管理
        }
        return mDaoMaster;
    }

    private void closeDaoSession() {
        if (null != mDaoSession) {
            mDaoSession.clear();//主要去除map对象的引用(缓存过的查询对象)
            mDaoSession = null;
        }
    }

    private void recycleDaoMaster() {
        if (mDaoMaster != null)
            mDaoMaster = null;
    }

    private void closeHelper() {
        if (mHelper != null) {
            mHelper.close();//关闭数据库
            mHelper = null;
        }
    }


    //总结:GreenDao内部Database利用静态代理模式代理了SQLiteDatabase方法，等于增删改查最后都会调用SQLiteDatabase的方法，
    // 例如查询:最终会让SQLiteDatabase执行查询语句,然后生成对象(如果缓存identityScopeLong(弱引用)中不存在,缓存中存在便会提升效率)


}
