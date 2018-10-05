package com.example.linyansen.udacity.DBConnection;

import android.provider.BaseColumns;

/**
 * Created by Lin Yansen on 11/18/2017.
 */

public class DBContract
{
    private DBContract(){}

    public static class UserEntry implements BaseColumns
    {
        public static final String TABLE_NAME = "MsUser";
        public static final String UserID = BaseColumns._ID;
        public static final String USERNAME_COLUMN = "Username";
        public static final String PASSWORD_COLUMN = "Password";
        public static final String FULLNAME_COLUMN = "Fullname";
        public static final String FOLLOWERS_COLUMN = "Followers";
        public static final String FOLLOWING_COLUMN = "Following";
        public static final String RATING_COLUMN = "Rating";
        public static final String STATUS_COLUMN = "Status";
    }

    public static class FollowingEntry implements BaseColumns
    {
        public static final String TABLE_NAME = "MsFollowing";
        public static final String FollowingID = BaseColumns._ID;
        public static final String USER_ID_COLUMN = "UserID";
        public static final String FOLLOWED_USER_ID_COLUMN = "FollowedUserID";
    }

    public static class RatingEntry implements BaseColumns
    {
        public static final String TABLE_NAME = "MsRating";
        public static final String RatingID = BaseColumns._ID;
        public static final String USER_ID_COLUMN = "UserID";
        public static final String RATED_USER_ID_COLUMN = "RatedUserID";
    }

    public static class MessageEntry implements BaseColumns
    {
        public static final String TABLE_NAME = "MsMessage";
        public static final String MessageID = BaseColumns._ID;
        public static final String SENDER_ID_COLUMN = "SenderID";
        public static final String RECEIVER_ID_COLUMN = "ReceiverID";
        public static final String TIME_COLUMN = "Time";
        public static final String READ_COLUMN = "Read";
        public static final String CONTENT_COLUMN = "Content";
    }
}
