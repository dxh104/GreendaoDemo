package com.example.greendaodemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.greendaodemo.db.base.BaseDao;
import com.example.greendaodemo.db.entity.User;
import com.example.greendaodemo.db.entity.UserFriend;
import com.example.greendaodemo.db.manager.DaoManager;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView tv;
    private DaoManager daoManager;
    private BaseDao<User, String> userDao;//用户表
    private BaseDao<UserFriend, Void> userFriendDao;//用户_好友表
    private boolean intercept;
    private Button btnInsertUser;
    private Button btnInsertUserFriend;
    private Button btnDeleteAllUser;
    private Button btnDeleteAllUserFriend;
    private Button btnQueryAllOneOrManny;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        daoManager = DaoManager.getInstance(this);
        userDao = new BaseDao<>(daoManager.getDaoSession().getUserDao());
        userFriendDao = new BaseDao<>(daoManager.getDaoSession().getUserFriendDao());
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (!intercept) {
                    List<User> users = userDao.queryAll();
                    List<UserFriend> userFriends = userFriendDao.queryAll();
                    final StringBuilder data = new StringBuilder();
                    if (users != null) {
                        for (User user : users) {
                            data.append(user.getUserId() + "," + user.getUserName() + "\n");
                        }
                    }
                    if (userFriends != null) {
                        for (UserFriend userFriend : userFriends) {
                            data.append(userFriend.getUserId() + "," + userFriend.getFriendUserId() + "\n");
                        }
                    }
                    tv.post(new Runnable() {
                        @Override
                        public void run() {
                            tv.setText(data);
                        }
                    });
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }


            }
        }).start();
        btnInsertUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<User> users = new ArrayList<>();
                for (int i = 0; i < 10; i++) {
                    User user = new User();
                    user.setUserId(i + "");
                    user.setUserName("小米" + i);
                    users.add(user);
                }
                userDao.save(users);
            }
        });

        btnInsertUserFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserFriend userFriend = new UserFriend();
                userFriend.setUserId("0");
                userFriend.setFriendUserId("1");
                userFriendDao.save(userFriend);

                userFriend = new UserFriend();
                userFriend.setUserId("1");
                userFriend.setFriendUserId("0");
                userFriendDao.save(userFriend);
            }
        });
        btnDeleteAllUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userDao.deleteAll();
            }
        });
        btnDeleteAllUserFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userFriendDao.deleteAll();
            }
        });

        btnQueryAllOneOrManny.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<User> users = userDao.queryAll();
                List<UserFriend> userFriends = userFriendDao.queryAll();
                if (users != null) {
                    for (User user : users) {
                        List<User> friendUsers = user.getFriendUsers();
                        List<UserFriend> userFriends1 = user.getUserFriends();

                        if (userFriends1 != null) {
                            for (UserFriend userFriend : userFriends1) {
                                Log.i(user.getUserId() + "的好友关系------", "user: " + userFriend.getUserId() + "-" + userFriend.getFriendUserId());
                            }
                        }
                        if (friendUsers != null) {
                            for (User friendUser : friendUsers) {
                                Log.i(user.getUserId() + "的好友信息------", "user: " + friendUser.getUserId() + "-" + friendUser.getUserName());
                            }
                        }

                    }
                }
                if(userFriends!=null){
                    for (UserFriend userFriend:userFriends){
                        Log.i( userFriend.getUser().getUserId()+"------"+userFriend.getUser().getUserName(), "userFriends:" + userFriend.getFriendUser().getUserId() + "------" + userFriend.getFriendUser().getUserName());
                    }
                }
            }
        });

    }

    private void initView() {
        tv = (TextView) findViewById(R.id.tv);
        btnInsertUser = (Button) findViewById(R.id.btn_insertUser);
        btnInsertUserFriend = (Button) findViewById(R.id.btn_insertUserFriend);
        btnDeleteAllUser = (Button) findViewById(R.id.btn_deleteAllUser);
        btnDeleteAllUserFriend = (Button) findViewById(R.id.btn_deleteAllUserFriend);
        btnQueryAllOneOrManny = (Button) findViewById(R.id.btn_queryAllOneOrManny);
    }

    @Override
    protected void onDestroy() {
        daoManager.closeDataBase();
        intercept = true;
        super.onDestroy();
    }

}
