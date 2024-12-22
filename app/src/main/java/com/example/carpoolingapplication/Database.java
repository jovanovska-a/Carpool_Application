package com.example.carpoolingapplication;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class Database extends SQLiteOpenHelper {
    public Database(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createUsersTable = "CREATE TABLE users (" +
                "user_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "username TEXT NOT NULL, " +
                "email TEXT NOT NULL, " +
                "password TEXT NOT NULL, " +
                "user_type TEXT NOT NULL, " +
                "rating REAL NOT NULL, " +
                "rated INTEGER NOT NULL, " +
                "UNIQUE(username, user_type) ON CONFLICT REPLACE" +
                ")";
        db.execSQL(createUsersTable);

        String createUserInfoTable = "CREATE TABLE user_info (" +
                "user_info_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_id INTEGER NOT NULL, " +
                "user_first_name TEXT NOT NULL, " +
                "user_last_name TEXT NOT NULL, " +
                "user_birth_date TEXT NOT NULL, " +
                "user_address TEXT NOT NULL," +
                "FOREIGN KEY(user_id) REFERENCES users(user_id)," +
                "UNIQUE(user_id)"+
                ")";
        db.execSQL(createUserInfoTable);

        String createDriverDetailsTable = "CREATE TABLE driver_details (" +
                "details_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "driver_id INTEGER NOT NULL, " +
                "driver_username TEXT NOT NULL, " +
                "vehicle_brand TEXT NOT NULL, " +
                "vehicle_model TEXT NOT NULL, " +
                "licence_plate TEXT NOT NULL," +
                "price REAL NOT NULL, " +
                "FOREIGN KEY(driver_id) REFERENCES users(user_id)," +
                "UNIQUE(driver_id)"+
                ")";
        db.execSQL(createDriverDetailsTable);

        String createDriverIntervalsTable = "CREATE TABLE driver_intervals (" +
                "interval_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "driver_id INTEGER NOT NULL, " +
                "start_interval TEXT NOT NULL, " +
                "end_interval TEXT NOT NULL, " +
                "FOREIGN KEY(driver_id) REFERENCES users(user_id)" +
                ")";
        db.execSQL(createDriverIntervalsTable);

        String createAvailableDriversTable = "CREATE TABLE available_drivers (" +
                "available_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "driver_id INTEGER NOT NULL, " +
                "driver_username TEXT NOT NULL,"+
                "vehicle_brand TEXT NOT NULL,"+
                "vehicle_model TEXT NOT NULL,"+
                "licence_plate TEXT NOT NULL,"+
                "price REAL NOT NULL,"+
                "rating REAL NOT NULL,"+
                "rated REAL NOT NULL,"+
                "FOREIGN KEY(driver_id) REFERENCES users(user_id) " +
                ")";
        db.execSQL(createAvailableDriversTable);
        String createTakenRidesTable = "CREATE TABLE taken_rides (" +
                "ride_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "driver_id INTEGER NOT NULL, " +
                "driver_username INTEGER NOT NULL, " +
                "passenger_id INTEGER NOT NULL, " +
                "passenger_username INTEGER NOT NULL, " +
                "start_location TEXT NOT NULL, " +
                "end_location TEXT NOT NULL, " +
                "date TEXT NOT NULL, " +
                "price REAL NOT NULL,"+
                "FOREIGN KEY(driver_id) REFERENCES users(user_id), " +
                "FOREIGN KEY(passenger_id) REFERENCES users(user_id)" +
                ")";
        db.execSQL(createTakenRidesTable);

        String createRatingTable = "CREATE TABLE rating (" +
                "rating_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "ride_id INTEGER NOT NULL, " +
                "user_who_rates_id INTEGER NOT NULL, " +
                "rated_user_id INTEGER NOT NULL, " +
                "rating REAL CHECK(rating BETWEEN 1 AND 5), " +
                "FOREIGN KEY(ride_id) REFERENCES taken_rides(ride_id), " +
                "FOREIGN KEY(user_who_rates_id) REFERENCES users(user_id), " +
                "FOREIGN KEY(rated_user_id) REFERENCES users(user_id)" +
                ")";
        db.execSQL(createRatingTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS users");
        db.execSQL("DROP TABLE IF EXISTS user_info");
        db.execSQL("DROP TABLE IF EXISTS driver_details");
        db.execSQL("DROP TABLE IF EXISTS driver_intervals");
        db.execSQL("DROP TABLE IF EXISTS ride");
        db.execSQL("DROP TABLE IF EXISTS rating");
        db.execSQL("DROP TABLE IF EXISTS available_drivers");
        db.execSQL("DROP TABLE IF EXISTS taken_rides");
        db.execSQL("DROP TABLE IF EXISTS rating");
        onCreate(db);
    }

    public void register(String username, String email, String password, String user_type) {
        ContentValues cv = new ContentValues();
        double rating = 0.0;
        cv.put("username", username);
        cv.put("email", email);
        cv.put("password", password);
        cv.put("user_type", user_type);
        cv.put("rating", rating);
        cv.put("rated", 0);
        SQLiteDatabase db = getWritableDatabase();
        db.insert("users",null,cv);
        db.close();
    }

    public int login(String username, String password, String user_type) {
        int result = 0;
        String str[] = new String[3];
        str[0] = username;
        str[1] = password;
        str[2] = user_type;
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("select * from users where username=? and password=? and user_type=?", str);
        if(c != null && c.moveToFirst()) {
            result = 1;
        }
        if (c != null) {
            c.close();
        }
        db.close();
        return result;
    }

    public int getUserIdByUsername(String username, String user_type) {
        int userId = -1;
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT user_id FROM users WHERE username = ? and user_type =?";
        Cursor cursor = db.rawQuery(query, new String[]{username,user_type});

        if (cursor.moveToFirst()) {
            userId = cursor.getInt(0);
        }

        cursor.close();
        db.close();
        return userId;
    }

    public String getUserTypeById(int userId) {
        String userType = null;
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT user_type FROM users WHERE user_id = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

        if (cursor.moveToFirst()) {
            userType = cursor.getString(0);
        }

        cursor.close();
        db.close();
        return userType;
    }

    public boolean isDriverDetailsExist(int driverId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM driver_details WHERE driver_id = ?", new String[]{String.valueOf(driverId)});
        boolean exists = cursor.moveToFirst();
        cursor.close();
        db.close();
        return exists;
    }

    public boolean ifUserInfoExist(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM user_info WHERE user_id = ?", new String[]{String.valueOf(userId)});
        boolean exists = cursor.moveToFirst();
        cursor.close();
        db.close();
        return exists;
    }

    public void insertDriverDetails(int driverId, String username, String vehicle_brand, String vehicle_model, String licence_plate, double price) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("driver_id", driverId);
        contentValues.put("driver_username", username);
        contentValues.put("vehicle_brand", vehicle_brand);
        contentValues.put("vehicle_model", vehicle_model);
        contentValues.put("licence_plate", licence_plate);
        contentValues.put("price", price);
        db.insert("driver_details", null, contentValues);
    }

    public void insertUserInfo(int userId, String first_name, String last_name, String birth_date, String address) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("user_id", userId);
        contentValues.put("user_first_name", first_name);
        contentValues.put("user_last_name", last_name);
        contentValues.put("user_birth_date", birth_date);
        contentValues.put("user_address", address);
        db.insert("user_info", null, contentValues);
    }

    public boolean hasDriverIntervals(int driverId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM driver_intervals WHERE driver_id = ?",
                new String[]{String.valueOf(driverId)});
        boolean hasIntervals = cursor.getCount() > 0;
        cursor.close();
        return hasIntervals;
    }

    public void addDriverInterval(int driverId, String startInterval, String endInterval) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("driver_id", driverId);
        values.put("start_interval", startInterval);
        values.put("end_interval", endInterval);
        db.insert("driver_intervals", null, values);
        db.close();
    }
    public ArrayList<String> getDriverTimeIntervals(int driverId) {
        ArrayList<String> intervals = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT start_interval, end_interval FROM driver_intervals WHERE driver_id = ?", new String[]{String.valueOf(driverId)});

        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") String startInterval = cursor.getString(cursor.getColumnIndex("start_interval"));
                @SuppressLint("Range") String endInterval = cursor.getString(cursor.getColumnIndex("end_interval"));
                intervals.add(startInterval + " - " + endInterval);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return intervals;
    }

    public ArrayList<String> getDriverStartIntervals(int driverId) {
        ArrayList<String> intervals = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT start_interval FROM driver_intervals WHERE driver_id = ?", new String[]{String.valueOf(driverId)});

        if (cursor.moveToFirst()) {
            do {
                intervals.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return intervals;
    }

    public ArrayList<String> getDriverEndIntervals(int driverId) {
        ArrayList<String> intervals = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT end_interval FROM driver_intervals WHERE driver_id = ?", new String[]{String.valueOf(driverId)});

        if (cursor.moveToFirst()) {
            do {
                intervals.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return intervals;
    }

    public void updateAvailableDrivers(String currentHour, double distance) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM available_drivers");
        String query = "SELECT DISTINCT di.driver_id, dd.driver_username, dd.vehicle_brand, dd.vehicle_model, dd.licence_plate, dd.price, u.rating, u.rated " +
                "FROM driver_intervals di " +
                "JOIN driver_details dd ON di.driver_id = dd.driver_id " +
                "JOIN users u ON u.user_id = di.driver_id " +
                "WHERE ? BETWEEN CAST(di.start_interval AS INTEGER) AND CAST(di.end_interval AS INTEGER)";
        Cursor cursor = db.rawQuery(query, new String[]{currentHour});

        db.beginTransaction();
        try {
            while (cursor.moveToNext()) {
                int driverId = cursor.getInt(0);
                String driverUsername = cursor.getString(1);
                String vehicle_brand = cursor.getString(2);
                String vehicle_model = cursor.getString(3);
                String licence_plate = cursor.getString(4);
                double price = cursor.getDouble(5);
                double newPrice = distance * price;
                double rating = cursor.getDouble(6);
                int rated = cursor.getInt(7);

                ContentValues contentValues = new ContentValues();
                contentValues.put("driver_id", driverId);
                contentValues.put("driver_username", driverUsername);
                contentValues.put("vehicle_brand", vehicle_brand);
                contentValues.put("vehicle_model", vehicle_model);
                contentValues.put("licence_plate", licence_plate);
                contentValues.put("price", newPrice);
                contentValues.put("rating", rating);
                contentValues.put("rated", rated);
                db.insert("available_drivers", null, contentValues);
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
            cursor.close();
            db.close();
        }
    }

    public Cursor getDriversData() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT * FROM available_drivers", null);
        } catch (Exception e) {
            Log.e("Database", "Error fetching data", e);
        }
        return cursor;
    }

    public Cursor getRidesData(int id, String activity) {
        String userType = this.getUserTypeById(id);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            if(activity.equals("ViewRideActivity")){
                if(userType.compareTo("Driver") == 0) {
                    cursor = db.rawQuery("SELECT ride_id, driver_id, passenger_id, start_location, end_location, date, price FROM taken_rides WHERE driver_id = ?", new String[]{String.valueOf(id)});
                }
                else if(userType.compareTo("Passenger") == 0) {
                    cursor = db.rawQuery("SELECT ride_id, driver_id, passenger_id, start_location, end_location, date, price FROM taken_rides WHERE passenger_id = ?", new String[]{String.valueOf(id)});
                }
            } else if(activity.equals("UnratedRidesActivity")) {
                cursor = db.rawQuery(
                        "SELECT tr.ride_id, tr.driver_id, tr.passenger_id, tr.start_location, tr.end_location, tr.date, tr.price " +
                                "FROM taken_rides tr " +
                                "WHERE tr.driver_id = ? " +
                                "AND NOT EXISTS (" +
                                "    SELECT 1 " +
                                "    FROM rating r " +
                                "    WHERE r.ride_id = tr.ride_id " +
                                "    AND r.user_who_rates_id = ? " +
                                ")",
                        new String[]{String.valueOf(id), String.valueOf(id)}
                );
            }

        } catch (Exception e) {
            Log.e("Database", "Error fetching data", e);
        }
        return cursor;
    }

    public long addRide(int selectedDriverId, int userId, String selectedDriverUsername, String passengerUsername, String startLocation, String endLocation, String date, double selectedPrice) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("driver_id", selectedDriverId);
        contentValues.put("driver_username", selectedDriverUsername);
        contentValues.put("passenger_id", userId);
        contentValues.put("passenger_username", passengerUsername);
        contentValues.put("start_location", startLocation);
        contentValues.put("end_location", endLocation);
        contentValues.put("price", selectedPrice);
        contentValues.put("date", date);
        long rideId = db.insert("taken_rides", null, contentValues);
        return rideId;
    }

    public void addRating(int rideId, int user_who_rates, int rated_user, float rating) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("ride_id", rideId);
        contentValues.put("user_who_rates_id", user_who_rates);
        contentValues.put("rated_user_id", rated_user);
        contentValues.put("rating", rating);
        db.insert("rating", null, contentValues);
        db.close();
        updateRating(rated_user);
    }


    public void updateRating(int rated_user) {
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT SUM(rating) AS total_rating, COUNT(rating) AS rating_count " +
                "FROM rating WHERE rated_user_id = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(rated_user)});

        if (cursor.moveToFirst()) {
            @SuppressLint("Range") int totalRating = cursor.getInt(cursor.getColumnIndex("total_rating"));
            @SuppressLint("Range") int ratingCount = cursor.getInt(cursor.getColumnIndex("rating_count"));

            double averageRating = ratingCount > 0 ? (double) totalRating / ratingCount : 0.0;
            averageRating = Math.round(averageRating * 100.0) / 100.0;

            ContentValues contentValues = new ContentValues();
            contentValues.put("rating", averageRating);
            contentValues.put("rated", 1);

            SQLiteDatabase writableDb = this.getWritableDatabase();
            writableDb.update("users", contentValues, "user_id = ?", new String[]{String.valueOf(rated_user)});
            writableDb.close();
        }

        cursor.close();
        db.close();
    }
    public void deleteDriverInterval(int driverId, String startInterval, String endInterval) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("driver_intervals",
                "driver_id = ? AND start_interval = ? AND end_interval = ?",
                new String[]{String.valueOf(driverId), startInterval, endInterval});
        db.close();
    }

    public Cursor getUserData(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT u.username, u.email, ui.user_first_name, ui.user_last_name, ui.user_birth_date, ui.user_address, u.rating " +
                "FROM users u " +
                "JOIN user_info ui ON u.user_id = ui.user_id " +
                "WHERE u.user_id = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});
        return cursor;
    }
}
