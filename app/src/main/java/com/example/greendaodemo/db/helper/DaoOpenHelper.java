package com.example.greendaodemo.db.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;


import com.example.greendaodemo.db.entity.DaoMaster;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.database.Database;

/**
 * Created by XHD on 2020/12/13
 * todo-------DaoMaster类不存在的时候要先构造出一张实体表
 */
public class DaoOpenHelper extends DaoMaster.DevOpenHelper {
    private Class<? extends AbstractDao<?, ?>>[] daoClasses;

    public DaoOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, Class<? extends AbstractDao<?, ?>>... daoClasses) {
        super(context, name, factory);
        this.daoClasses = daoClasses;
    }

    /**
     * 数据库更新
     *
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(Database db, int oldVersion, int newVersion) {
        //迁移数据
        MigrationDaoHelper.migrate(db, daoClasses);
    }

}