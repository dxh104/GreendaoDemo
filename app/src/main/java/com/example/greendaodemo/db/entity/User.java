package com.example.greendaodemo.db.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.JoinEntity;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.ToMany;

import java.util.List;

import org.greenrobot.greendao.DaoException;

/**
 * Created by XHD on 2020/12/20
 * 用户表
 */
@Entity
public class User {
    @Id
    private String userId;
    @NotNull
    private String userName;

    //一对多 利用主键值查询其他表     条件:referencedJoinProperty这个参数值的字段=User主键值
    @ToMany(referencedJoinProperty = "userId")//userId代表UserFriend中的字段
    //总结:生成查询好友关系集合方法 条件:referencedJoinProperty中的属性字段=实体类(User)主键值。 查询UserFriend表,返回List<UserFriend>  好友关系
    private List<UserFriend> userFriends;//生成查询Friend表方法(userId(UserFriend表)=userId(User表主键值))

    //多对多 利用主键值，使用内连接，多表查询   条件:userId(UserFriend)=User主键值   friendUserId(UserFriend)+User主键:参与内连接的字段
    @ToMany
    //@JoinEntity注解：entity 中间表；sourceProperty 中间表查询条件的实体属性；targetProperty 中间表外链实体属性
    @JoinEntity(//如果两个实体是多对多的关系，那么需要第三张表（表示两个实体关系的表)
            entity = UserFriend.class,  //对join实体类的引用，该类保存源和目标属性
            sourceProperty = "userId",  //包含源（当前）实体id的联接实体内的属性的名称
            targetProperty = "friendUserId" //包含目标实体id的联接实体内的属性的名称
    )
    //总结:生成查询好友信息集合方法 条件: sourceProperty中的属性字段=实体类(User)中的主键值，targetProperty和实体类(User)主键参与内连接。利用多表查询，返回List<User>  好友信息
    private List<User> friendUsers;

    /**
     * Used to resolve relations
     */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;
    /**
     * Used for active entity operations.
     */
    @Generated(hash = 1507654846)
    private transient UserDao myDao;

    @Generated(hash = 1842429419)
    public User(String userId, @NotNull String userName) {
        this.userId = userId;
        this.userName = userName;
    }

    @Generated(hash = 586692638)
    public User() {
    }

    public String getUserId() {
        return this.userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return this.userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 1503720756)
    public List<UserFriend> getUserFriends() {
        if (userFriends == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            UserFriendDao targetDao = daoSession.getUserFriendDao();
            List<UserFriend> userFriendsNew = targetDao._queryUser_UserFriends(userId);
            synchronized (this) {
                if (userFriends == null) {
                    userFriends = userFriendsNew;
                }
            }
        }
        return userFriends;
    }

    /**
     * Resets a to-many relationship, making the next get call to query for a fresh result.
     */
    @Generated(hash = 344805556)
    public synchronized void resetUserFriends() {
        userFriends = null;
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 1403625722)
    public List<User> getFriendUsers() {
        if (friendUsers == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            UserDao targetDao = daoSession.getUserDao();
            List<User> friendUsersNew = targetDao._queryUser_FriendUsers(userId);
            synchronized (this) {
                if (friendUsers == null) {
                    friendUsers = friendUsersNew;
                }
            }
        }
        return friendUsers;
    }

    /**
     * Resets a to-many relationship, making the next get call to query for a fresh result.
     */
    @Generated(hash = 2101927086)
    public synchronized void resetFriendUsers() {
        friendUsers = null;
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
    @Generated(hash = 2059241980)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getUserDao() : null;
    }


}
