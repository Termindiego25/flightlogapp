package es.diegosr.flightlogapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.io.ByteArrayOutputStream;
import java.sql.Time;

import es.diegosr.flightlogapp.pojos.Booking;
import es.diegosr.flightlogapp.pojos.Flight;
import es.diegosr.flightlogapp.pojos.LandingField;
import es.diegosr.flightlogapp.pojos.Plane;
import es.diegosr.flightlogapp.pojos.User;

public class BDAdapter {
    private DBHelper dbHelper;
    private SQLiteDatabase db;
    private Context context;

    public BDAdapter(Context context) {
        this.context = context;
        dbHelper = new DBHelper(context, "FlightLogApp", null, 1);
    }

    public User login(String username, String password) {
        db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT users.id, users.username, users.password, users.userType, users.landingField, users.name, users.lastname, users.image " +
                "FROM users " +
                "WHERE users.username = ? AND users.password = ?;", new String[]{username, password});
        if(cursor != null && cursor.moveToFirst()) {
            return cursorToUser(cursor);
        }
        else return null;
    }
    public Cursor getRecyclerList(String listaRecycler, String orderBy, String filterBy) {
        db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT localUser.id, localUser.landingField, userTypes.type FROM localUser JOIN userTypes ON localUser.userType = userTypes.id;", null);
        String query;
        if(cursor != null && cursor.moveToFirst()) {
            switch(listaRecycler) {
                case "flights":
                    query = "SELECT flights.id, flights.departureSite, flights.departureDate, flights.arrivalSite, flights.arrivalDate, flights.plane, flights.duration, flights.pilot, flights.monoPilot, flights.dayLandings, flights.nightLandings " +
                            "FROM flights JOIN planes ON flights.plane = planes.id " +
                            "WHERE pilot = " + (filterBy != null ? Integer.toString(cursor.getInt(0)) + " AND " + filterBy : Integer.toString(cursor.getInt(0))) + " " +
                            "ORDER BY " + (orderBy != null ? orderBy : "flights.departureDate DESC") + ";";
                    return db.rawQuery(query, null);
                case "bookings":
                    query = "SELECT bookings.id, bookings.pilot, bookings.plane, bookings.date, bookings.duration " +
                            "FROM bookings JOIN planes ON bookings.plane = planes.id JOIN users ON bookings.pilot = users.id " +
                            "WHERE (planes.owner = " + Integer.toString(cursor.getInt(0)) + " OR planes.landingField = " + Integer.toString(cursor.getInt(1)) + ") AND (planes.owner = " + Integer.toString(cursor.getInt(0)) + " OR 'controller' = '" + (filterBy != null ? cursor.getString(2) + "') AND " + filterBy : cursor.getString(2) + "')") + " " +
                            "ORDER BY " + (orderBy != null ? orderBy : "bookings.date ASC") + ";";
                    return db.rawQuery(query, null);
                default:
                    return null;
            }
        }
        else return null;
    }
    public Flight cursorToFlight(Cursor cursor) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        try {
            return new Flight(cursor.getInt(0),
                    getLandingFieldById(cursor.getInt(1)),
                    dateFormat.parse(cursor.getString(2)),
                    getLandingFieldById(cursor.getInt(3)),
                    dateFormat.parse(cursor.getString(4)),
                    getPlaneById(cursor.getInt(5)),
                    Time.valueOf(cursor.getString(6)),
                    getUserById(cursor.getInt(7)),
                    cursor.getInt(8) == 1,
                    cursor.getInt(9),
                    cursor.getInt(10));
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
    public Booking cursorToBooking(Cursor cursor) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        try {
            return new Booking(cursor.getInt(0),
                    getUserById(cursor.getInt(1)),
                    getPlaneById(cursor.getInt(2)),
                    dateFormat.parse(cursor.getString(3)),
                    Time.valueOf(cursor.getString(4)));
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
    public User cursorToUser(Cursor cursor) {
        if(cursor != null && cursor.moveToFirst()) {
            return new User(cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    getLandingFieldById(cursor.getInt(4)),
                    cursor.getString(5),
                    cursor.getString(6),
                    cursor.getString(7));
        }
        else return null;
    }

    private LandingField getLandingFieldById(int id) {
        db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT name, acronym FROM landingFields WHERE id = ?;", new String[] {Integer.toString(id)});
        LandingField landingField;

        if(cursor != null && cursor.moveToFirst()) {
            landingField = new LandingField(id,
                    cursor.getString(0),
                    cursor.getString(1));
            cursor.close();
            return landingField;
        }
        else return null;
    }
    public LandingField getLandingFieldByName(String name) {
        db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id, name, acronym FROM landingFields WHERE acronym = ?;", new String[] {name});

        if(cursor != null && cursor.moveToFirst()) {
            return new LandingField(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2)
            );
        }
        else {
            cursor = db.rawQuery("SELECT id, name, acronym FROM landingFields WHERE name = ?;", new String[] {name});
            if(cursor != null && cursor.moveToFirst()) {
                return new LandingField(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2)
                );
            }
            else {
                return null;
            }
        }
    }
    public String[] getLandingFields() {
        Cursor cursor;
        db = dbHelper.getReadableDatabase();

        cursor = db.rawQuery("SELECT acronym, name FROM landingFields ORDER BY id ASC;", null);
        String[] landingFields = new String[cursor.getCount()];
        if(cursor != null && cursor.moveToFirst()) {
            do {
                landingFields[cursor.getPosition()] = cursor.getString(0).isEmpty() ? cursor.getString(1) : cursor.getString(0);
            } while(cursor.moveToNext());
        }

        return landingFields;
    }
    private Plane getPlaneById(int id) {
        db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT plate, owner, model FROM planes WHERE id = ?;", new String[] {Integer.toString(id)});
        Plane plane;

        if(cursor != null && cursor.moveToFirst()) {
            plane = new Plane(id,
                    cursor.getString(0),
                    getUserById(cursor.getInt(1)),
                    cursor.getString(2));
            cursor.close();
            return plane;
        }
        else return null;
    }
    public Plane getPlaneByName(String name) {
        db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id, plate, owner, model FROM planes WHERE plate = ?;", new String[] {name.split(" - ")[0]});

        if(cursor != null && cursor.moveToFirst()) {
            return new Plane(
                    cursor.getInt(0),
                    cursor.getString(1),
                    getUserById(cursor.getInt(2)),
                    cursor.getString(3)
            );
        }
        else {
            return null;
        }
    }
    public String[] getPlanes() {
        Cursor cursor;
        Cursor cursorAux = db.rawQuery("SELECT localUser.id, localUser.landingField, userTypes.type FROM localUser JOIN userTypes ON localUser.userType = userTypes.id;", null);
        db = dbHelper.getReadableDatabase();

        if(cursorAux != null && cursorAux.moveToFirst()) {
            cursor = db.rawQuery("SELECT planes.plate, planes.model FROM planes WHERE (planes.owner = ? OR planes.landingField = ?) AND (planes.owner = ? OR 'controller' = ?) ORDER BY planes.id ASC;", new String[]{Integer.toString(cursorAux.getInt(0)), Integer.toString(cursorAux.getInt(1)), Integer.toString(cursorAux.getInt(0)), cursorAux.getString(2)});
            String[] planes = new String[cursor.getCount()];
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    planes[cursor.getPosition()] = cursor.getString(0) + " - " + cursor.getString(1);
                } while (cursor.moveToNext());
            }

            return planes;
        }
        else return null;
    }
    private User getUserById(int id) {
        db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT userTypes.type, users.landingField, users.name, users.lastname, users.image FROM users JOIN userTypes ON users.userType = userTypes.id WHERE users.id = ?;", new String[] {Integer.toString(id)});
        User user;

        if(cursor != null && cursor.moveToFirst()) {
            user =  new User(id,
                    cursor.getString(0),
                    getLandingFieldById(cursor.getInt(1)),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(4));
            cursor.close();
            return user;
        }
        else return null;
    }
    public User getUserByName(String name) {
        db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT users.id, userTypes.type, users.landingField, users.name, users.lastname, users.image FROM users JOIN userTypes ON users.userType = userTypes.id WHERE users.name = ? AND users.lastname = ?;", new String[] {name.split(" ", 2)[0], name.split(" ", 2)[1]});

        if(cursor != null && cursor.moveToFirst()) {
            return new User(
                    cursor.getInt(0),
                    cursor.getString(1),
                    getLandingFieldById(cursor.getInt(2)),
                    cursor.getString(3),
                    cursor.getString(4),
                    cursor.getString(5));
        }
        else {
            return null;
        }
    }
    public String[] getUsers() {
        Cursor cursor;
        db = dbHelper.getReadableDatabase();

        cursor = db.rawQuery("SELECT users.name, users.lastname FROM users, localUser WHERE users.landingField = localUser.landingField ORDER BY users.id ASC;", null);
        String[] users = new String[cursor.getCount()];
        if(cursor != null && cursor.moveToFirst()) {
            do {
                users[cursor.getPosition()] = cursor.getString(0) + " " + cursor.getString(1);
            } while(cursor.moveToNext());
        }

        return users;
    }
    public Cursor getUser() {
        db = dbHelper.getReadableDatabase();
        return db.rawQuery("SELECT * FROM users LIMIT 1;", null);
    }
    public Cursor getLocalUser() {
        db = dbHelper.getReadableDatabase();
        return db.rawQuery("SELECT localUser.image, localUser.name, localUser.lastname, userTypes.type FROM localUser JOIN userTypes ON localUser.userType = userTypes.id;", null);
    }
    public Cursor getProfile() {
        db = dbHelper.getReadableDatabase();
        return db.rawQuery("SELECT localuser.id, localUser.username, localUser.password, userTypes.type, localUser.landingField, localUser.name, localUser.lastname, localUser.image FROM localUser JOIN userTypes ON localUser.userType = userTypes.id;", null);
    }

    public void addLocalUser(User localUser) {
        db = dbHelper.getWritableDatabase();
        if(db != null){
            ContentValues val = new ContentValues();
            val.put("id", localUser.getId());
            val.put("username", localUser.getUsername());
            val.put("password", localUser.getPassword());
            val.put("userType", Integer.parseInt(localUser.getUserType()));
            val.put("landingField", localUser.getLandingField().getId());
            val.put("name", localUser.getName());
            val.put("lastName", localUser.getLastName());
            val.put("image", localUser.getImage());

            db.insert("localUser", null, val);
        }
        db.close();
    }
    public void addFlight(Flight flightData) {
        db = dbHelper.getWritableDatabase();
        SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyy-MM-dd HH:mm:ss");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        if(db != null){
            ContentValues val = new ContentValues();
            val.put("departureSite", flightData.getDepartureSite().getId());
            val.put("departureDate", dateTimeFormat.format(flightData.getDepartureDate()));
            val.put("arrivalSite", flightData.getArrivalSite().getId());
            val.put("arrivalDate", dateTimeFormat.format(flightData.getArrivalDate()));
            val.put("plane", flightData.getPlane().getId());
            val.put("duration", timeFormat.format(flightData.getDuration()));
            val.put("pilot", flightData.getPilot().getId());
            val.put("monoPilot", (flightData.getMonoPilot() ? 1 : 0));
            val.put("dayLandings", flightData.getDayLandings());
            val.put("nightLandings", flightData.getNightLandings());

            db.insert("flights", null, val);
        }
        db.close();
    }
    public void addBooking(Booking bookingData) {
        db = dbHelper.getWritableDatabase();
        SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyy-MM-dd HH:mm:ss");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        if(db != null){
            ContentValues val = new ContentValues();
            val.put("pilot", bookingData.getPilot().getId());
            val.put("plane", bookingData.getPlane().getId());
            val.put("date", dateTimeFormat.format(bookingData.getDate()));
            val.put("duration", timeFormat.format(bookingData.getDuration()));

            db.insert("bookings", null, val);
        }
        db.close();
    }

    public void updateLocalUser(User localUser) {
        ContentValues val = new ContentValues();
        val.put("name", localUser.getName());
        val.put("lastname", localUser.getLastName());
        val.put("image", localUser.getImage());
        val.put("landingField", localUser.getLandingField().getId());
        if(!localUser.getUsername().isEmpty()) val.put("username", localUser.getUsername());
        if(!localUser.getPassword().isEmpty()) val.put("password", localUser.getPassword());
        db.update("localuser", val, "id = ?", new String[] {Integer.toString(localUser.getId())});
        db.update("users", val, "id = ?", new String[] {Integer.toString(localUser.getId())});
    }
    public void updateFlight(Flight flightData) {
        db = dbHelper.getWritableDatabase();
        SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyy-MM-dd HH:mm:ss");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        if(db != null){
            ContentValues val = new ContentValues();
            val.put("departureSite", flightData.getDepartureSite().getId());
            val.put("departureDate", dateTimeFormat.format(flightData.getDepartureDate()));
            val.put("arrivalSite", flightData.getArrivalSite().getId());
            val.put("arrivalDate", dateTimeFormat.format(flightData.getArrivalDate()));
            val.put("plane", flightData.getPlane().getId());
            val.put("duration", timeFormat.format(flightData.getDuration()));
            val.put("pilot", flightData.getPilot().getId());
            val.put("monoPilot", (flightData.getMonoPilot() ? 1 : 0));
            val.put("dayLandings", flightData.getDayLandings());
            val.put("nightLandings", flightData.getNightLandings());

            db.update("flights", val, "id = ?", new String[]{Integer.toString(flightData.getId())});
        }
        db.close();
    }
    public void updateBooking(Booking bookingData) {
        db = dbHelper.getWritableDatabase();
        SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyy-MM-dd HH:mm:ss");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        if(db != null){
            ContentValues val = new ContentValues();
            val.put("pilot", bookingData.getPilot().getId());
            val.put("plane", bookingData.getPlane().getId());
            val.put("date", dateTimeFormat.format(bookingData.getDate()));
            val.put("duration", timeFormat.format(bookingData.getDuration()));

            db.update("bookings", val, "id = ?", new String[]{Integer.toString(bookingData.getId())});
        }
        db.close();
    }

    public void deleteLocalUser() {
        db = dbHelper.getWritableDatabase();
        if(db != null){
            db.execSQL("DELETE FROM localUser;");
        }
        db.close();
    }
    public void deleteFlight(int id) {
        db = dbHelper.getWritableDatabase();
        if(db != null){
            db.delete("flights", "id = ?", new String[]{Integer.toString(id)});
        }
        db.close();
    }
    public void deleteBooking(int id) {
        db = dbHelper.getWritableDatabase();
        if(db != null){
            db.delete("bookings", "id = ?", new String[]{Integer.toString(id)});
        }
        db.close();
    }

    static public Bitmap stringToBitmap(String imagen) {
        byte[] decodedString = Base64.decode(imagen, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }
    static public String bitmapToString(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream);
        byte[] byte_arr = stream.toByteArray();
        return Base64.encodeToString(byte_arr, Base64.DEFAULT);
    }

    public void rellenarDatos() {
        db = dbHelper.getWritableDatabase();
        if(db != null){
            ContentValues val = new ContentValues();
            val.put("id", 1);
            val.put("name", "Alcocer");
            val.put("acronym", "");
            db.insert("landingFields", null, val);

            val = new ContentValues();
            val.put("id", 2);
            val.put("name", "Mutxamiel");
            val.put("acronym", "LEMU");
            db.insert("landingFields", null, val);

            val = new ContentValues();
            val.put("id", 3);
            val.put("name", "Requena");
            val.put("acronym", "LERE");
            db.insert("landingFields", null, val);

            val = new ContentValues();
            val.put("id", 4);
            val.put("name", "Castellon");
            val.put("acronym", "LECN");
            db.insert("landingFields", null, val);


            val = new ContentValues();
            val.put("id", 1);
            val.put("type", "student");
            db.insert("userTypes", null, val);

            val = new ContentValues();
            val.put("id", 2);
            val.put("type", "pilot");
            db.insert("userTypes", null, val);

            val = new ContentValues();
            val.put("id", 3);
            val.put("type", "member");
            db.insert("userTypes", null, val);

            val = new ContentValues();
            val.put("id", 4);
            val.put("type", "teacher");
            db.insert("userTypes", null, val);

            val = new ContentValues();
            val.put("id", 5);
            val.put("type", "controller");
            db.insert("userTypes", null, val);


            val = new ContentValues();
            val.put("id", 1);
            val.put("username", "testStudent");
            val.put("password", "test");
            val.put("userType", 1);
            val.put("landingField", 2);
            val.put("name", "Student");
            val.put("lastname", "Test");
            val.put("image", bitmapToString(BitmapFactory.decodeResource(this.context.getResources(), R.drawable.contact_image)));
            db.insert("users", null, val);

            val = new ContentValues();
            val.put("id", 2);
            val.put("username", "testPilot");
            val.put("password", "test");
            val.put("userType", 2);
            val.put("landingField", 1);
            val.put("name", "Pilot");
            val.put("lastname", "Test");
            val.put("image", bitmapToString(BitmapFactory.decodeResource(this.context.getResources(), R.drawable.contact_image)));
            db.insert("users", null, val);

            val = new ContentValues();
            val.put("id", 3);
            val.put("username", "testMember");
            val.put("password", "test");
            val.put("userType", 3);
            val.put("landingField", 4);
            val.put("name", "Member");
            val.put("lastname", "Test");
            val.put("image", bitmapToString(BitmapFactory.decodeResource(this.context.getResources(), R.drawable.contact_image)));
            db.insert("users", null, val);

            val = new ContentValues();
            val.put("id", 4);
            val.put("username", "testTeacher");
            val.put("password", "test");
            val.put("userType", 4);
            val.put("landingField", 3);
            val.put("name", "teacher");
            val.put("lastname", "Test");
            val.put("image", bitmapToString(BitmapFactory.decodeResource(this.context.getResources(), R.drawable.contact_image)));
            db.insert("users", null, val);

            val = new ContentValues();
            val.put("id", 5);
            val.put("username", "testController");
            val.put("password", "test");
            val.put("userType", 5);
            val.put("landingField", 1);
            val.put("name", "Controller");
            val.put("lastname", "Test");
            val.put("image", bitmapToString(BitmapFactory.decodeResource(this.context.getResources(), R.drawable.contact_image)));
            db.insert("users", null, val);


            val = new ContentValues();
            val.put("id", 1);
            val.put("plate", "ECIOM");
            val.put("owner", 2);
            val.put("model", "Tecnam P90");
            val.put("landingField", 1);
            db.insert("planes", null, val);

            val = new ContentValues();
            val.put("id", 2);
            val.put("plate", "ECTEY");
            val.put("owner", 3);
            val.put("model", "Trike");
            val.put("landingField", 3);
            db.insert("planes", null, val);

            val = new ContentValues();
            val.put("id", 3);
            val.put("plate", "ECLMB");
            val.put("owner", 5);
            val.put("model", "Tecnam P92");
            val.put("landingField", 1);
            db.insert("planes", null, val);

            val = new ContentValues();
            val.put("id", 4);
            val.put("plate", "ECPER");
            val.put("owner", 4);
            val.put("model", "Aeroprakt");
            val.put("landingField", 1);
            db.insert("planes", null, val);


            val = new ContentValues();
            val.put("id", 1);
            val.put("departureSite", 1);
            val.put("departureDate", "2018-03-27 09:00:00");
            val.put("arrivalSite", 3);
            val.put("arrivalDate", "2018-03-27 11:00:00");
            val.put("plane", 4);
            val.put("duration", "01:00:00");
            val.put("pilot", 5);
            val.put("monopilot", 1);
            val.put("dayLandings", 1);
            val.put("nightLandings", 0);
            db.insert("flights", null, val);

            val = new ContentValues();
            val.put("id", 2);
            val.put("departureSite", 3);
            val.put("departureDate", "2018-03-28 12:00:00");
            val.put("arrivalSite", 1);
            val.put("arrivalDate", "2018-03-29 13:00:00");
            val.put("plane", 4);
            val.put("duration", "01:00:00");
            val.put("pilot", 5);
            val.put("monopilot", 1);
            val.put("dayLandings", 1);
            val.put("nightLandings", 0);
            db.insert("flights", null, val);

            val = new ContentValues();
            val.put("id", 3);
            val.put("departureSite", 1);
            val.put("departureDate", "2018-03-30 10:00:00");
            val.put("arrivalSite", 1);
            val.put("arrivalDate", "2018-03-30 11:00:00");
            val.put("plane", 1);
            val.put("duration", "01:00:00");
            val.put("pilot", 5);
            val.put("monopilot", 1);
            val.put("dayLandings", 1);
            val.put("nightLandings", 0);
            db.insert("flights", null, val);


            val = new ContentValues();
            val.put("id", 1);
            val.put("pilot", 2);
            val.put("plane", 2);
            val.put("date", "2018-03-29 10:00:00");
            val.put("duration", "00:30:00");
            db.insert("bookings", null, val);

            val = new ContentValues();
            val.put("id", 2);
            val.put("pilot", 4);
            val.put("plane", 2);
            val.put("date", "2018-03-29 11:00:00");
            val.put("duration", "01:00:00");
            db.insert("bookings", null, val);

            val = new ContentValues();
            val.put("id", 3);
            val.put("pilot", 3);
            val.put("plane", 4);
            val.put("date", "2018-03-30 12:30:00");
            val.put("duration", "00:45:00");
            db.insert("bookings", null, val);


            /*val = new ContentValues();
            val.put("id", 5);
            val.put("username", "testController");
            val.put("password", "test");
            val.put("userType", 5);
            val.put("landingField", 1);
            val.put("name", "Controller");
            val.put("lastname", "Test");
            val.put("image", bitmapToString(BitmapFactory.decodeResource(this.context.getResources(), R.drawable.contact_image)));
            db.insert("localuser", null, val);*/
        }
        db.close();
    }

    static class DBHelper extends SQLiteOpenHelper {
        String queryLandingFields = "CREATE TABLE IF NOT EXISTS landingFields (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                "name TEXT NOT NULL,\n" +
                "acronym TEXT NOT NULL);";
        String queryUserTypes = "CREATE TABLE IF NOT EXISTS userTypes (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                "type TEXT NOT NULL);";
        String queryUsers = "CREATE TABLE IF NOT EXISTS users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                "username TEXT NOT NULL," +
                "password TEXT NOT NULL," +
                "userType INTEGER NOT NULL DEFAULT 1," +
                "landingField INTEGER NOT NULL," +
                "name TEXT NOT NULL," +
                "lastname TEXT NOT NULL," +
                "image TEXT NOT NULL," +
                "FOREIGN KEY (userType) REFERENCES userTypes (id)," +
                "FOREIGN KEY (landingField) REFERENCES landingFields (id));";
        String queryPlanes = "CREATE TABLE IF NOT EXISTS planes (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                "plate TEXT NOT NULL," +
                "owner INTEGER NOT NULL," +
                "model TEXT NOT NULL," +
                "landingField INTEGER NOT NULL," +
                "FOREIGN KEY (owner) REFERENCES users(id)," +
                "FOREIGN KEY (landingField) REFERENCES landingFields (id));";
        String queryBookings = "CREATE TABLE IF NOT EXISTS bookings (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                "pilot INTEGER NOT NULL," +
                "plane INTEGER NOT NULL," +
                "date DATETIME NOT NULL," +
                "duration TEXT NOT NULL," +
                "FOREIGN KEY (plane) REFERENCES planes(id)," +
                "FOREIGN KEY (pilot) REFERENCES users(id));";
        String queryFlights = "CREATE TABLE IF NOT EXISTS flights (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                "departureSite INTEGER NOT NULL," +
                "departureDate DATETIME NOT NULL," +
                "arrivalSite INTEGER NOT NULL," +
                "arrivalDate DATETIME NOT NULL," +
                "plane INTEGER NOT NULL," +
                "duration TIME DEFAULT '00:00:00'," +
                "pilot INTEGER NOT NULL," +
                "monoPilot INTEGER NOT NULL DEFAULT '1'," +
                "dayLandings INTEGER DEFAULT '1'," +
                "nightLandings INTEGER DEFAULT '0'," +
                "FOREIGN KEY (arrivalSite) REFERENCES landingFields(id)," +
                "FOREIGN KEY (departureSite) REFERENCES landingFields(id)," +
                "FOREIGN KEY (pilot) REFERENCES users(id)," +
                "FOREIGN KEY (plane) REFERENCES planes(id));";
        String queryLocalUser = "CREATE TABLE IF NOT EXISTS localUser (" +
                "id INTEGER PRIMARY KEY NOT NULL," +
                "username TEXT NOT NULL," +
                "password TEXT NOT NULL," +
                "userType INTEGER NOT NULL DEFAULT 1," +
                "landingField INTEGER NOT NULL," +
                "name TEXT NOT NULL," +
                "lastname TEXT NOT NULL," +
                "image TEXT NOT NULL," +
                "FOREIGN KEY (userType) REFERENCES userTypes (id)," +
                "FOREIGN KEY (landingField) REFERENCES landingFields (id));";

        DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(queryLandingFields);
            db.execSQL(queryUserTypes);
            db.execSQL(queryUsers);
            db.execSQL(queryPlanes);
            db.execSQL(queryBookings);
            db.execSQL(queryFlights);
            db.execSQL(queryLocalUser);
        }
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }
}