package com.example.fitzone;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class FitZoneDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "fitzone.db";
    private static final int DATABASE_VERSION = 3;

    public static final String TABLE_USERS = "users";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_PASSWORD = "password";
    public static final String COLUMN_AGE = "age";
    public static final String COLUMN_HEIGHT = "height";
    public static final String COLUMN_WEIGHT = "weight";
    public static final String COLUMN_AVATAR_URI = "avatarUri";

    public static final String TABLE_POSTS = "posts";
    public static final String COLUMN_POST_ID = "id";
    public static final String COLUMN_POST_USER_ID = "userId";
    public static final String COLUMN_POST_TEXT = "text";
    public static final String COLUMN_POST_IMAGE_PATH = "imagePath";
    public static final String COLUMN_POST_DATE = "date";

    public static final String TABLE_POST_LIKES = "post_likes";
    public static final String COLUMN_LIKE_ID = "id";
    public static final String COLUMN_LIKE_POST_ID = "postId";
    public static final String COLUMN_LIKE_USER_ID = "userId";

    public static final String TABLE_POST_COMMENTS = "post_comments";
    public static final String COLUMN_COMMENT_ID = "id";
    public static final String COLUMN_COMMENT_POST_ID = "postId";
    public static final String COLUMN_COMMENT_USER_ID = "userId";
    public static final String COLUMN_COMMENT_TEXT = "comment";
    public static final String COLUMN_COMMENT_DATE = "date";

    private static final String SQL_CREATE_USERS_TABLE =
            "CREATE TABLE " + TABLE_USERS + " ("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_NAME + " TEXT NOT NULL,"
                    + COLUMN_EMAIL + " TEXT NOT NULL UNIQUE,"
                    + COLUMN_PASSWORD + " TEXT NOT NULL,"
                    + COLUMN_AGE + " INTEGER NOT NULL,"
                    + COLUMN_HEIGHT + " REAL NOT NULL,"
                    + COLUMN_WEIGHT + " REAL NOT NULL,"
                    + COLUMN_AVATAR_URI + " TEXT"
                    + ");";

    private static final String SQL_CREATE_POSTS_TABLE =
            "CREATE TABLE " + TABLE_POSTS + " ("
                    + COLUMN_POST_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_POST_USER_ID + " INTEGER NOT NULL,"
                    + COLUMN_POST_TEXT + " TEXT NOT NULL,"
                    + COLUMN_POST_IMAGE_PATH + " TEXT,"
                    + COLUMN_POST_DATE + " TEXT NOT NULL,"
                    + "FOREIGN KEY(" + COLUMN_POST_USER_ID + ") REFERENCES "
                    + TABLE_USERS + "(" + COLUMN_ID + ") ON DELETE CASCADE"
                    + ");";

    private static final String SQL_CREATE_POST_LIKES_TABLE =
            "CREATE TABLE " + TABLE_POST_LIKES + " ("
                    + COLUMN_LIKE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_LIKE_POST_ID + " INTEGER NOT NULL,"
                    + COLUMN_LIKE_USER_ID + " INTEGER NOT NULL,"
                    + "UNIQUE(" + COLUMN_LIKE_POST_ID + ", " + COLUMN_LIKE_USER_ID + "),"
                    + "FOREIGN KEY(" + COLUMN_LIKE_POST_ID + ") REFERENCES " + TABLE_POSTS + "(" + COLUMN_POST_ID + ") ON DELETE CASCADE,"
                    + "FOREIGN KEY(" + COLUMN_LIKE_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_ID + ") ON DELETE CASCADE"
                    + ");";

    private static final String SQL_CREATE_POST_COMMENTS_TABLE =
            "CREATE TABLE " + TABLE_POST_COMMENTS + " ("
                    + COLUMN_COMMENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_COMMENT_POST_ID + " INTEGER NOT NULL,"
                    + COLUMN_COMMENT_USER_ID + " INTEGER NOT NULL,"
                    + COLUMN_COMMENT_TEXT + " TEXT NOT NULL,"
                    + COLUMN_COMMENT_DATE + " TEXT NOT NULL,"
                    + "FOREIGN KEY(" + COLUMN_COMMENT_POST_ID + ") REFERENCES " + TABLE_POSTS + "(" + COLUMN_POST_ID + ") ON DELETE CASCADE,"
                    + "FOREIGN KEY(" + COLUMN_COMMENT_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_ID + ") ON DELETE CASCADE"
                    + ");";

    public FitZoneDbHelper(@NonNull Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_USERS_TABLE);
        db.execSQL(SQL_CREATE_POSTS_TABLE);
        db.execSQL(SQL_CREATE_POST_LIKES_TABLE);
        db.execSQL(SQL_CREATE_POST_COMMENTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_POST_COMMENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_POST_LIKES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_POSTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    public long insertUser(String name, String email, String password, int age, float height, float weight) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_EMAIL, email);
        values.put(COLUMN_PASSWORD, password);
        values.put(COLUMN_AGE, age);
        values.put(COLUMN_HEIGHT, height);
        values.put(COLUMN_WEIGHT, weight);
        values.putNull(COLUMN_AVATAR_URI);
        return db.insert(TABLE_USERS, null, values);
    }

    public long insertPost(long userId, String text, String imagePath, String date) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_POST_USER_ID, userId);
        values.put(COLUMN_POST_TEXT, text);
        if (TextUtils.isEmpty(imagePath)) {
            values.putNull(COLUMN_POST_IMAGE_PATH);
        } else {
            values.put(COLUMN_POST_IMAGE_PATH, imagePath);
        }
        values.put(COLUMN_POST_DATE, date);
        return db.insert(TABLE_POSTS, null, values);
    }

    public List<PostItem> getAllPosts() {
        return getAllPosts(-1L);
    }

    public List<PostItem> getAllPosts(long viewerUserId) {
        SQLiteDatabase db = getReadableDatabase();
        List<PostItem> posts = new ArrayList<>();

        String likedExpression;
        if (viewerUserId > 0L) {
            likedExpression = "CASE WHEN EXISTS (SELECT 1 FROM " + TABLE_POST_LIKES + " pl2"
                    + " WHERE pl2." + COLUMN_LIKE_POST_ID + " = p." + COLUMN_POST_ID
                    + " AND pl2." + COLUMN_LIKE_USER_ID + " = " + viewerUserId + ")"
                    + " THEN 1 ELSE 0 END";
        } else {
            likedExpression = "0";
        }

        String sql = "SELECT p." + COLUMN_POST_ID
                + ", p." + COLUMN_POST_TEXT
                + ", p." + COLUMN_POST_IMAGE_PATH
                + ", p." + COLUMN_POST_DATE
                + ", u." + COLUMN_NAME
                + ", u." + COLUMN_AVATAR_URI
                + ", (SELECT COUNT(*) FROM " + TABLE_POST_LIKES + " pl"
                + " WHERE pl." + COLUMN_LIKE_POST_ID + " = p." + COLUMN_POST_ID + ") AS likeCount"
                + ", (SELECT COUNT(*) FROM " + TABLE_POST_COMMENTS + " pc"
                + " WHERE pc." + COLUMN_COMMENT_POST_ID + " = p." + COLUMN_POST_ID + ") AS commentCount"
                + ", " + likedExpression + " AS likedByMe"
                + " FROM " + TABLE_POSTS + " p"
                + " JOIN " + TABLE_USERS + " u"
                + " ON p." + COLUMN_POST_USER_ID + " = u." + COLUMN_ID
                + " ORDER BY p." + COLUMN_POST_ID + " DESC";

        Cursor cursor = db.rawQuery(sql, null);
        try {
            int idIndex = cursor.getColumnIndexOrThrow(COLUMN_POST_ID);
            int textIndex = cursor.getColumnIndexOrThrow(COLUMN_POST_TEXT);
            int imageIndex = cursor.getColumnIndexOrThrow(COLUMN_POST_IMAGE_PATH);
            int dateIndex = cursor.getColumnIndexOrThrow(COLUMN_POST_DATE);
            int userNameIndex = cursor.getColumnIndexOrThrow(COLUMN_NAME);
            int avatarIndex = cursor.getColumnIndexOrThrow(COLUMN_AVATAR_URI);
            int likeCountIndex = cursor.getColumnIndexOrThrow("likeCount");
            int commentCountIndex = cursor.getColumnIndexOrThrow("commentCount");
            int likedByMeIndex = cursor.getColumnIndexOrThrow("likedByMe");

            while (cursor.moveToNext()) {
                posts.add(new PostItem(
                        cursor.getLong(idIndex),
                        cursor.getString(userNameIndex),
                        cursor.getString(textIndex),
                        cursor.isNull(imageIndex) ? "" : cursor.getString(imageIndex),
                        cursor.getString(dateIndex),
                        cursor.isNull(avatarIndex) ? "" : cursor.getString(avatarIndex),
                        cursor.getInt(likeCountIndex),
                        cursor.getInt(commentCountIndex),
                        cursor.getInt(likedByMeIndex) == 1));
            }
        } finally {
            cursor.close();
        }

        return posts;
    }

    public boolean togglePostLike(long postId, long userId) {
        if (postId <= 0L || userId <= 0L) {
            return false;
        }

        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.query(
                TABLE_POST_LIKES,
                new String[]{COLUMN_LIKE_ID},
                COLUMN_LIKE_POST_ID + "=? AND " + COLUMN_LIKE_USER_ID + "=?",
                new String[]{String.valueOf(postId), String.valueOf(userId)},
                null,
                null,
                null,
                "1");

        try {
            if (cursor.moveToFirst()) {
                db.delete(
                        TABLE_POST_LIKES,
                        COLUMN_LIKE_POST_ID + "=? AND " + COLUMN_LIKE_USER_ID + "=?",
                        new String[]{String.valueOf(postId), String.valueOf(userId)});
                return false;
            }
        } finally {
            cursor.close();
        }

        ContentValues values = new ContentValues();
        values.put(COLUMN_LIKE_POST_ID, postId);
        values.put(COLUMN_LIKE_USER_ID, userId);
        db.insert(TABLE_POST_LIKES, null, values);
        return true;
    }

    public long addPostComment(long postId, long userId, String comment, String date) {
        if (postId <= 0L || userId <= 0L || TextUtils.isEmpty(comment)) {
            return -1L;
        }

        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_COMMENT_POST_ID, postId);
        values.put(COLUMN_COMMENT_USER_ID, userId);
        values.put(COLUMN_COMMENT_TEXT, comment.trim());
        values.put(COLUMN_COMMENT_DATE, date);
        return db.insert(TABLE_POST_COMMENTS, null, values);
    }

    public long getUserIdByEmail(String email) {
        if (TextUtils.isEmpty(email)) {
            return -1L;
        }

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(
                TABLE_USERS,
                new String[]{COLUMN_ID},
                COLUMN_EMAIL + "=?",
                new String[]{email},
                null,
                null,
                null,
                "1");
        try {
            if (cursor.moveToFirst()) {
                return cursor.getLong(0);
            }
            return -1L;
        } finally {
            cursor.close();
        }
    }

    public boolean validateUserCredentials(String email, String password) {
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            return false;
        }

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(
                TABLE_USERS,
                new String[]{COLUMN_ID},
                COLUMN_EMAIL + "=? AND " + COLUMN_PASSWORD + "=?",
                new String[]{email.trim(), password},
                null,
                null,
                null,
                "1");
        try {
            return cursor.moveToFirst();
        } finally {
            cursor.close();
        }
    }

    public boolean isUserRegistered(String email) {
        if (TextUtils.isEmpty(email)) {
            return false;
        }
        return getUserIdByEmail(email.trim()) > 0L;
    }

    public boolean updateUserPasswordByEmail(String email, String newPassword) {
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(newPassword)) {
            return false;
        }

        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PASSWORD, newPassword);

        int rows = db.update(
                TABLE_USERS,
                values,
                COLUMN_EMAIL + "=?",
                new String[]{email.trim()});
        return rows > 0;
    }

    public boolean updateUserAvatar(long userId, String avatarUri) {
        if (userId <= 0L) {
            return false;
        }

        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        if (TextUtils.isEmpty(avatarUri)) {
            values.putNull(COLUMN_AVATAR_URI);
        } else {
            values.put(COLUMN_AVATAR_URI, avatarUri);
        }

        int rows = db.update(
                TABLE_USERS,
                values,
                COLUMN_ID + "=?",
                new String[]{String.valueOf(userId)});
        return rows > 0;
    }

    public String getUserAvatar(long userId) {
        if (userId <= 0L) {
            return "";
        }

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(
                TABLE_USERS,
                new String[]{COLUMN_AVATAR_URI},
                COLUMN_ID + "=?",
                new String[]{String.valueOf(userId)},
                null,
                null,
                null,
                "1");
        try {
            if (cursor.moveToFirst() && !cursor.isNull(0)) {
                return cursor.getString(0);
            }
            return "";
        } finally {
            cursor.close();
        }
    }

    public UserProfile getDefaultUserProfile() {
        long userId = getOrCreateDefaultUserId();
        if (userId == -1L) {
            return null;
        }

        return getUserProfileById(userId);
    }

    public UserProfile getUserProfileById(long userId) {
        if (userId <= 0L) {
            return null;
        }

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(
                TABLE_USERS,
                new String[]{COLUMN_ID, COLUMN_NAME, COLUMN_EMAIL, COLUMN_AGE, COLUMN_HEIGHT, COLUMN_WEIGHT},
                COLUMN_ID + "=?",
                new String[]{String.valueOf(userId)},
                null,
                null,
                null,
                "1");

        try {
            if (!cursor.moveToFirst()) {
                return null;
            }

            return mapUserProfile(cursor);
        } finally {
            cursor.close();
        }
    }

    public boolean updateUserProfile(long userId, String name, int age, float height, float weight) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_AGE, age);
        values.put(COLUMN_HEIGHT, height);
        values.put(COLUMN_WEIGHT, weight);

        int updatedRows = db.update(
                TABLE_USERS,
                values,
                COLUMN_ID + "=?",
                new String[]{String.valueOf(userId)});
        return updatedRows > 0;
    }

    public long getOrCreateDefaultUserId() {
        SQLiteDatabase db = getWritableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT " + COLUMN_ID + " FROM " + TABLE_USERS + " ORDER BY " + COLUMN_ID + " LIMIT 1",
                null);

        try {
            if (cursor.moveToFirst()) {
                return cursor.getLong(0);
            }
        } finally {
            cursor.close();
        }

        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, "FitZone User");
        values.put(COLUMN_EMAIL, "user@fitzone.local");
        values.put(COLUMN_PASSWORD, "fitzone123");
        values.put(COLUMN_AGE, 25);
        values.put(COLUMN_HEIGHT, 170f);
        values.put(COLUMN_WEIGHT, 70f);
        values.putNull(COLUMN_AVATAR_URI);
        return db.insert(TABLE_USERS, null, values);
    }

    private UserProfile mapUserProfile(Cursor cursor) {
        return new UserProfile(
                cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL)),
                cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_AGE)),
                cursor.getFloat(cursor.getColumnIndexOrThrow(COLUMN_HEIGHT)),
                cursor.getFloat(cursor.getColumnIndexOrThrow(COLUMN_WEIGHT)));
    }
}

