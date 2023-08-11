# ArkDataBase

![GitHub](https://img.shields.io/github/license/LovelyCatEx/ArkDataBase)![Maven Central](https://img.shields.io/maven-central/v/com.lovelycatv.ark/runtime?logo=Apache%20Maven&label=runtime&labelColor=%233a9fff&color=%23ff8aaa&link=https%3A%2F%2Fcentral.sonatype.com%2Fartifact%2Fcom.lovelycatv.ark%2Fruntime)![Maven Central](https://img.shields.io/maven-central/v/com.lovelycatv.ark/compiler?logo=Apache%20Maven&label=compiler&labelColor=%233a9fff&color=%23ff8aaa&link=https%3A%2F%2Fcentral.sonatype.com%2Fartifact%2Fcom.lovelycatv.ark%2Fcompiler)


ArkDatabase is a java library that could generate code automatically by annotations. If you know Mybatis or RoomDatabase, you will know this library is similar to them.

By using of @Database, @Entity and @Dao, the library will generate Impl classes in your project automatically.

This is supported database of Ark.

| Database | Suported |
| :-:| :-: |
| MySQL | √ |
| SQLite | √ |

**Wiki for Ark:** https://wiki.lovelycatv.cn/ark/site/

# Instructions

## 1. Implementation

Maven:

```XML
<dependency>
    <groupId>com.lovelycatv.ark</groupId>
    <artifactId>runtime</artifactId>
    <version>LATEST</version>
</dependency>
<dependency>
    <groupId>com.lovelycatv.ark</groupId>
    <artifactId>compiler</artifactId>
    <version>LATEST</version>
</dependency>
```

Gradle:

```GROOVY
implementation 'com.lovelycatv.ark:runtime:latest.release'
annotationProcessor  'com.lovelycatv.ark:compiler:latest.release'
```

## 2. Prepare database, entities and dao

**1. Enums**
```Java
public enum UserSex {
    UNKNOWN(-1),
    MALE(0),
    FEMALE(1);

    public int id;

    UserSex(int id) {
        this.id = id;
    }

    public static UserSex getSexById(int id) {
        for (UserSex value : values()) {
            if (value.id == id) {
                return value;
            }
        }
        return UNKNOWN;
    }
}
```

**2. User Entity**
```Java
public class User {
    @Column(columnName = "id", primaryKey = true, autoIncrease = true)
    private int id;
    @Column(columnName = "username")
    private String username;
    @Column(columnName = "password")
    private String password;
    @Column(columnName = "sex")
    private UserSex sex;
    @Column(columnName = "birthday")
    private String birthday;
}
```

**3. User DAO**
```Java
@Dao
public interface UserDAO {
    @Insert
    void insert(User... user);
    @Delete
    void delete(User... user);
    @Update
    void update(User... user);
    @Query(sql = "SELECT * FROM `users` WHERE `id` = :id")
    User getUserById(int id);
    @Query(sql = "SELECT * FROM `users`")
    User[] getAllUsers();
    @Query(sql = "TRUNCATE `users`", executeOnly = true)
    void clearTable();
}
```

**4. TypeConverters**

`MySQL cannot recognize the type UserSex, so it should be turned to the type that MySQL could save.`

```Java
public class UserTypeConverters {
    @TypeConverter
    public static int userSex2Int(UserSex userSex) {
        return userSex.id;
    }

    @TypeConverter
    public static UserSex int2UserSex(int sexId) {
        return UserSex.getSexById(sexId);
    }
}
```

**5. Database(MySQL)**
```Java
@Database(dataBaseType = DataBaseType.MYSQL, entities = {User.class}, typeConverters = {UserTypeConverters.class}, version = 1)
public abstract class MyDatabase extends ArkDatabase {
    private static MyDatabase INSTANCE;
    public abstract UserDAO userDAO();
    public static synchronized MyDatabase getInstance() {
        if (INSTANCE == null) {
            INSTANCE = Ark.getRelationalDatabaseBuilder()
                    .mysql("192.168.2.102",3306,"ark","ark","ark")
                    .createDatabase(MyDatabase.class);
        }
        return INSTANCE;
    }
}
```

**5. Database(SQLite)**
```Java
@Database(dataBaseType = DataBaseType.SQLITE, entities = {User.class}, typeConverters = {UserTypeConverters.class}, version = 1)
public abstract class MyDatabase extends ArkDatabase {
    private static MyDatabase INSTANCE;
    public abstract UserDAO userDAO();
    public static synchronized MyDatabase getInstance() {
        if (INSTANCE == null) {
            INSTANCE = Ark.getRelationalDatabaseBuilder()
                    .sqlite("./arkTest.db")
                    .createDatabase(MyDatabase.class);
        }
        return INSTANCE;
    }
}
```

**The difference between MySQL and SQLite is to change `dataBaseType` to SQLite in the `@Database` annotation and change the builder method name `mysql` to `sqlite` when creating your database instance**

## 3. Test your database

```Java
public static void printAllUsers() {
    User[] allUsers = database.userDAO().getAllUsers();
    for (User user : allUsers) {
        System.out.println(user.getId() + " : " + user.getUsername() + " : " + user.getPassword() + " : " + user.getBirthday());
    }
}

public static void insert() {
    User user = new User();
    for (int i = 0; i < 10; i++) {
        user.setUsername(UUID.randomUUID().toString());
        user.setPassword(UUID.randomUUID().toString());
        user.setBirthday(new Date().toString());
        database.userDAO().insert(user);
    }
}

public static void mysql_delete(int userId) {
    User user = new User();
    user.setId(userId);
    database.userDAO().delete(user);
}
```
