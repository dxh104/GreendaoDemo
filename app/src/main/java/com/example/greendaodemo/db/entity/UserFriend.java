package com.example.greendaodemo.db.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.ToOne;
import org.greenrobot.greendao.DaoException;

/**
 * Created by XHD on 2020/12/20
 * 用户_好友表
 */
@Entity(
        indexes = {@Index(value = "userId, friendUserId", unique = true)}//两个唯一索引，实现联合主键
)
public class UserFriend {
    @NotNull
    private String userId;
    @NotNull
    private String friendUserId;

    //总结:生成查询User方法 条件:实体类(User)中主键=joinProperty值
    @ToOne(joinProperty = "userId")//设置外键关系
    private User user;//User表中的主键(userId)和FriendRelation表中的外键(userId)关联，该注解可以生成查询User表中userId=userId的对象方法
    @ToOne(joinProperty = "friendUserId")//设置外键关系
    private User friendUser;//User表中的主键(userId)和FriendRelation表中的外键(friendUserId)关联，该注解可以生成查询User表中userId=friendUserId的对象方法
    /**
     * Used to resolve relations
     */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;
    /**
     * Used for active entity operations.
     */
    @Generated(hash = 1243526609)
    private transient UserFriendDao myDao;

    @Generated(hash = 1944184570)
    public UserFriend(@NotNull String userId, @NotNull String friendUserId) {
        this.userId = userId;
        this.friendUserId = friendUserId;
    }

    @Generated(hash = 194691203)
    public UserFriend() {
    }

    public String getUserId() {
        return this.userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFriendUserId() {
        return this.friendUserId;
    }

    public void setFriendUserId(String friendUserId) {
        this.friendUserId = friendUserId;
    }

    @Generated(hash = 1867105156)
    private transient String user__resolvedKey;

    /**
     * To-one relationship, resolved on first access.
     */
    @Generated(hash = 538271798)
    public User getUser() {
        String __key = this.userId;
        if (user__resolvedKey == null || user__resolvedKey != __key) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            UserDao targetDao = daoSession.getUserDao();
            User userNew = targetDao.load(__key);
            synchronized (this) {
                user = userNew;
                user__resolvedKey = __key;
            }
        }
        return user;
    }

    /**
     * called by internal mechanisms, do not call yourself.
     */
    @Generated(hash = 1622843587)
    public void setUser(@NotNull User user) {
        if (user == null) {
            throw new DaoException("To-one property 'userId' has not-null constraint; cannot set to-one to null");
        }
        synchronized (this) {
            this.user = user;
            userId = user.getUserId();
            user__resolvedKey = userId;
        }
    }

    @Generated(hash = 333500403)
    private transient String friendUser__resolvedKey;

    /**
     * To-one relationship, resolved on first access.
     */
    @Generated(hash = 1294444447)
    public User getFriendUser() {
        String __key = this.friendUserId;
        if (friendUser__resolvedKey == null || friendUser__resolvedKey != __key) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            UserDao targetDao = daoSession.getUserDao();
            User friendUserNew = targetDao.load(__key);
            synchronized (this) {
                friendUser = friendUserNew;
                friendUser__resolvedKey = __key;
            }
        }
        return friendUser;
    }

    /**
     * called by internal mechanisms, do not call yourself.
     */
    @Generated(hash = 871632222)
    public void setFriendUser(@NotNull User friendUser) {
        if (friendUser == null) {
            throw new DaoException("To-one property 'friendUserId' has not-null constraint; cannot set to-one to null");
        }
        synchronized (this) {
            this.friendUser = friendUser;
            friendUserId = friendUser.getUserId();
            friendUser__resolvedKey = friendUserId;
        }
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#delete(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 128553479)
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.delete(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#refresh(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 1942392019)
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.refresh(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#update(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 713229351)
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.update(this);
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 1616364044)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getUserFriendDao() : null;
    }

}
