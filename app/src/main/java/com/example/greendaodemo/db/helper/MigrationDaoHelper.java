package com.example.greendaodemo.db.helper;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.internal.DaoConfig;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by XHD on 2020/12/13
 * Greendao版本升级的迁移数据帮助类
 * 还能继续优化(下个版本原来某张表用不到了,对应实体类也删除了,这种方式不会删除之前那张存在的表。优化方式:可以在迁移数据结束后，利用SQLITE_MASTER表查出所有的表，只保留有对应实体类的表)
 */
public class MigrationDaoHelper {

    /**
     * 旧表数据迁移到新表中
     *
     * @param db         数据库
     * @param daoClasses dao.class
     */
    public static void migrate(Database db, Class<? extends AbstractDao<?, ?>>... daoClasses) {
        generateTempTables(db, daoClasses);//1.重命名原表当作临时旧表
        createAllTables(db, false, daoClasses);//2.反射调用createTable生成新表
        restoreData(db, daoClasses);//3.临时旧表与新表交集字段的数据写入新表，删除临时旧表
    }


    /**
     * 修改表名当作临时旧表
     *
     * @param db         数据库
     * @param daoClasses dao.class
     */
    private static void generateTempTables(Database db, Class<? extends AbstractDao<?, ?>>... daoClasses) {
        for (int i = 0; i < daoClasses.length; i++) {
            //DaoConfig存储DAOs的基本数据，由AbstractDaoMaster保存。这个类将从DAO类检索所需的信息。
            DaoConfig daoConfig = new DaoConfig(db, daoClasses[i]);
            String tableName = daoConfig.tablename;
            if (!checkTable(db, tableName))//检查表是否存在
                continue;
            String tempTableName = daoConfig.tablename.concat("_TEMP");//连接字符串
            StringBuilder insertTableStringBuilder = new StringBuilder();
            insertTableStringBuilder.append("alter table ")
                    .append(tableName)
                    .append(" rename to ")
                    .append(tempTableName)
                    .append(";");
            //重命名表当作临时旧表
            db.execSQL(insertTableStringBuilder.toString());
        }
    }

    /**
     * 检查表是否存在
     *
     * @param db
     * @param tableName
     */
    private static Boolean checkTable(Database db, String tableName) {
        StringBuilder query = new StringBuilder();
        //SQLite数据库中有一个内置表，名为SQLITE_MASTER，此表中存储着当前数据库中所有表的相关信息，
        // 比如表的名称、用于创建此表的sql语句、索引、索引所属的表、创建索引的sql语句等。
        query.append("SELECT count(*) FROM sqlite_master WHERE type='table' AND name='").append(tableName).append("'");
        Cursor c = db.rawQuery(query.toString(), null);
        if (c.moveToNext()) {
            int count = c.getInt(0);
            if (count > 0) {
                return true;
            }
            return false;
        }
        return false;
    }

    /**
     * 删除所有表(daoClasses)
     *
     * @param db
     * @param ifExists
     * @param daoClasses
     */
    private static void dropAllTables(Database db, boolean ifExists, @NonNull Class<? extends AbstractDao<?, ?>>... daoClasses) {
        reflectMethod(db, "dropTable", ifExists, daoClasses);
    }

    /**
     * 反射调用***Dao中createTable方法创建新表
     *
     * @param db
     * @param ifNotExists
     * @param daoClasses
     */
    private static void createAllTables(Database db, boolean ifNotExists, @NonNull Class<? extends AbstractDao<?, ?>>... daoClasses) {
        reflectMethod(db, "createTable", ifNotExists, daoClasses);//反射调用***Dao中createTable方法创建新表
    }

    /**
     * 反射***Dao方法(创建表/删除表)
     *
     * @param db         参数 数据库
     * @param methodName 方法名
     * @param isExists   参数 是否存在
     * @param daoClasses
     */
    private static void reflectMethod(Database db, String methodName, boolean isExists, @NonNull Class<? extends AbstractDao<?, ?>>... daoClasses) {
        if (daoClasses.length < 1) {
            return;
        }
        try {
            //反射调用daoClasses中的dropTable静态方法 删除表
            for (Class cls : daoClasses) {
                //根据方法名，找到声明的方法
                Method method = cls.getDeclaredMethod(methodName, Database.class, boolean.class);
                method.invoke(null, db, isExists);
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * 临时旧表的数据写入新表
     *
     * @param db
     * @param daoClasses
     */
    private static void restoreData(Database db, Class<? extends AbstractDao<?, ?>>... daoClasses) {
        for (int i = 0; i < daoClasses.length; i++) {
            DaoConfig daoConfig = new DaoConfig(db, daoClasses[i]);
            String tableName = daoConfig.tablename;//新表
            String tempTableName = daoConfig.tablename.concat("_TEMP");//临时旧表
            if (!checkTable(db, tempTableName))//检查临时旧表是否存在
                continue;
            // 获取所有临时旧表列名(表字段)
            List<String> columns = getColumns(db, tempTableName);
            //新表，临时旧表都包含的字段
            ArrayList<String> properties = new ArrayList<>(columns.size());//交集的字段
            for (int j = 0; j < daoConfig.properties.length; j++) {//遍历新表字段
                String columnName = daoConfig.properties[j].columnName;
                if (columns.contains(columnName)) {
                    properties.add(columnName);
                }
            }
            if (properties.size() > 0) {
                final String columnSQL = TextUtils.join(",", properties);

                StringBuilder insertTableStringBuilder = new StringBuilder();//查询临时旧表交集字段的数据，添加到新表中
                insertTableStringBuilder.append("INSERT INTO ").append(tableName).append(" (");
                insertTableStringBuilder.append(columnSQL);
                insertTableStringBuilder.append(") SELECT ");
                insertTableStringBuilder.append(columnSQL);
                insertTableStringBuilder.append(" FROM ").append(tempTableName).append(";");
                db.execSQL(insertTableStringBuilder.toString());//新表追加数据
            }
            StringBuilder dropTableStringBuilder = new StringBuilder();
            dropTableStringBuilder.append("DROP TABLE ").append(tempTableName);
            db.execSQL(dropTableStringBuilder.toString());//删除旧表
        }
    }

    //获取所有临时旧表列名
    private static List<String> getColumns(Database db, String tableName) {
        List<String> columns = null;
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT * FROM " + tableName + " limit 0", null);
            if (null != cursor && cursor.getColumnCount() > 0) {
                columns = Arrays.asList(cursor.getColumnNames());//返回列名
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null)
                cursor.close();
            if (null == columns)
                columns = new ArrayList<>();
        }
        return columns;
    }
}
