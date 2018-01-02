package net.multiform_music.ifeelrun.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.google.android.gms.maps.model.LatLng;

import net.multiform_music.ifeelrun.bean.RunningBean;
import net.multiform_music.ifeelrun.bean.RunningStepBean;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Michel on 30/04/2017.
 *
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "RunningTrackerDB";

    // représente un jog
    private static final String TABLE_NAME_RUNNING = "Running";
    private static final String COLUMN_ID_RUNNING = "id_running";               // INTEGER
    private static final String COLUMN_PLACE_NAME = "place";                    // TEXT
    private static final String COLUMN_CITY= "ville";                           // TEXT
    private static final String COLUMN_COMMENT = "commentaire";                 // TEXT
    private static final String COLUMN_MAP_FILE_PATH = "map_fichier";            // TEXT
    private static final String COLUMN_DATE = "date";                           // TEXT
    private static final String COLUMN_VITESSE_MOYENNE = "vitesse";             // REAL
    private static final String COLUMN_TIME = "temps";                          // TEXT
    private static final String COLUMN_DISTANCE = "distance";                   // REAL
    private static final String COLUMN_DENIVELE = "denivele";                   // REAL
    private static final String COLUMN_CALORY = "calories";                     // REAL
    private static final String COLUMN_NBR_POINTS = "nombre_points";            // REAL
    private static final String COLUMN_ACTIVITY_NAME = "acitivite";             // TEXT
    private static final String COLUMN_VELOCITY_UNIT = "velocity_unit";         // TEXT
    private static final String COLUMN_DISTANCE_UNIT = "distance_unit";         // TEXT
    private static final String COLUMN_ELEVATION_UNIT = "elevation_unit";       // TEXT
    private static final String COLUMN_HASH_PLACE_NAME = "hash_place";          // TEXT
    private static final String COLUMN_HASH_CITY = "hash_city";                 // TEXT
    private static final String COLUMN_HASH_ACTIVITY_NAME = "hash_acitivite";   // TEXT


    // représente les paramètres d'une course
    /*private static final String TABLE_NAME_RUNNING_PARAM = "RunningParam";
    private static final String COLUMN_ID_RUNNING_PARAM = "id_running_param";            // INTEGER
    private static final String COLUMN_VITESSE_MIN_ALARM = "vitesse_min_alarm";          // REAL
    private static final String COLUMN_VITESSE_MAX_ALARM = "vitesse_max_alarm";          // REAL
    private static final String COLUMN_SECONDS_BEFORE_START = "seconds_before_start";    // INTEGER
    */

    // représente les points géolocalisés du jog
    private static final String TABLE_NAME_RUNNING_STEP = "RunningStep";
    private static final String COLUMN_ID_RUNNING_STEP = "id_running_step";        // INTEGER
    private static final String COLUMN_HORODATAGE = "horodatage";                 // REAL
    private static final String COLUMN_LONGITUDE = "longiture";                   // REAL
    private static final String COLUMN_LATITUDE = "latitude";                     // REAL
    private static final String COLUMN_ALTITUDE = "altitude";                     // REAL
    private static final String COLUMN_VITESSE = "vitesse";                       // REAL
    private static final String COLUMN_DISTANCE_BETWEEN = "distance_between";     // REAL
    private static final String COLUMN_TIME_DELAY = "time_delay";                 // REAL
    private static final String COLUMN_ACCURACY = "accuracy";                     // REAL

    private static final int DB_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String sqlRunning = "CREATE TABLE " + TABLE_NAME_RUNNING
                + "(" + COLUMN_ID_RUNNING
                + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_PLACE_NAME + " TEXT, "
                + COLUMN_CITY + " TEXT, "
                + COLUMN_COMMENT + " TEXT, "
                + COLUMN_MAP_FILE_PATH + " TEXT, "
                + COLUMN_DATE + " TEXT, "
                + COLUMN_TIME + " TEXT, "
                + COLUMN_DISTANCE + " REAL, "
                + COLUMN_VITESSE_MOYENNE + " REAL, "
                + COLUMN_DENIVELE + " REAL, "
                + COLUMN_CALORY + " REAL, "
                + COLUMN_NBR_POINTS + " REAL, "
                + COLUMN_ACTIVITY_NAME + " TEXT, "
                + COLUMN_VELOCITY_UNIT + " TEXT, "
                + COLUMN_DISTANCE_UNIT + " TEXT, "
                + COLUMN_ELEVATION_UNIT + " TEXT, "
                + COLUMN_HASH_PLACE_NAME + " TEXT, "
                + COLUMN_HASH_CITY + " TEXT, "
                + COLUMN_HASH_ACTIVITY_NAME + " TEXT "
                + ");";

        db.execSQL(sqlRunning);

        String sqlMapStep = "CREATE TABLE " + TABLE_NAME_RUNNING_STEP
                + "(" + COLUMN_ID_RUNNING_STEP
                + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_LATITUDE + " REAL, "
                + COLUMN_LONGITUDE + " REAL, "
                + COLUMN_ACCURACY + " REAL, "
                + COLUMN_ALTITUDE + " REAL, "
                + COLUMN_DISTANCE_BETWEEN + " REAL, "
                + COLUMN_TIME_DELAY + " REAL, "
                + COLUMN_VITESSE + " REAL, "
                + COLUMN_DATE + " TEXT, "
                + COLUMN_HORODATAGE + " REAL, "
                + COLUMN_ID_RUNNING + " INTEGER "
                + ");";

        db.execSQL(sqlMapStep);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    /**
     * Ajout d'un running en base
     *
     * @param runningBean run
     * @return boolean
     */
    public boolean addRunningBean(RunningBean runningBean) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(COLUMN_PLACE_NAME, runningBean.getPlaceName());
        contentValues.put(COLUMN_ACTIVITY_NAME, runningBean.getActivity());
        contentValues.put(COLUMN_CITY, runningBean.getCity());
        contentValues.put(COLUMN_DATE, runningBean.getDate());
        contentValues.put(COLUMN_TIME, runningBean.getTemps());
        contentValues.put(COLUMN_VITESSE_MOYENNE, runningBean.getVelocity());
        contentValues.put(COLUMN_DISTANCE, runningBean.getDistance());
        contentValues.put(COLUMN_DENIVELE, runningBean.getDenivele());
        contentValues.put(COLUMN_CALORY, runningBean.getCalory());
        contentValues.put(COLUMN_NBR_POINTS, runningBean.getNbrPoints());
        contentValues.put(COLUMN_VELOCITY_UNIT, runningBean.getUnitVelocity());
        contentValues.put(COLUMN_DISTANCE_UNIT, runningBean.getUnitDistance());
        contentValues.put(COLUMN_ELEVATION_UNIT, runningBean.getUnitElevation());
        contentValues.put(COLUMN_HASH_PLACE_NAME, md5(runningBean.getPlaceName()));
        contentValues.put(COLUMN_HASH_ACTIVITY_NAME, md5(runningBean.getActivity()));
        contentValues.put(COLUMN_HASH_CITY, md5(runningBean.getCity()));

        long result = db.insert(TABLE_NAME_RUNNING, null, contentValues);
        db.close();

        return result != -1;
    }

    /**
     *
     * Retrouve l'id du dernier running enregistré
     *
     * @return dernier id enregistré
     */
    public int getLastIdRecordRunning() {

        SQLiteDatabase db = this.getReadableDatabase();

        int id = 0;

        String[] columns = new String[] { COLUMN_ID_RUNNING };
        Cursor cursor = db.query(TABLE_NAME_RUNNING, columns, null, null, null, null, null);

        if (cursor.moveToLast()) {
            id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID_RUNNING));
        }

        if (!cursor.isClosed()) {
            cursor.close();
        }

        return id;
    }

    /**
     *
     * Récupération de la course id running
     *
     * @param idRunning id
     * @return RunningBean
     */
    public RunningBean getRunningBean(Integer idRunning) {

        RunningBean runningBean = new RunningBean();

        String[] columns = new String[] { COLUMN_PLACE_NAME, COLUMN_COMMENT, COLUMN_DATE, COLUMN_TIME, COLUMN_DISTANCE, COLUMN_VITESSE_MOYENNE, COLUMN_DENIVELE,
                                           COLUMN_CITY, COLUMN_VELOCITY_UNIT, COLUMN_DISTANCE_UNIT, COLUMN_ELEVATION_UNIT, COLUMN_CALORY };

        String whereClause = COLUMN_ID_RUNNING + " = ?";
        String[] whereArgs = new String[] { idRunning.toString() };

        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.query(TABLE_NAME_RUNNING, columns, whereClause, whereArgs, null, null, COLUMN_ID_RUNNING + " ASC");

        if (cursor.moveToNext()) {

            runningBean.setPlaceName(cursor.getString(cursor.getColumnIndex(COLUMN_PLACE_NAME)));
            runningBean.setComment(cursor.getString(cursor.getColumnIndex(COLUMN_COMMENT)));
            runningBean.setDate(cursor.getString(cursor.getColumnIndex(COLUMN_DATE)));
            runningBean.setVelocity(cursor.getDouble(cursor.getColumnIndex(COLUMN_VITESSE_MOYENNE)));
            runningBean.setUnitVelocity(cursor.getString(cursor.getColumnIndex(COLUMN_VELOCITY_UNIT)));
            runningBean.setDistance(cursor.getDouble(cursor.getColumnIndex(COLUMN_DISTANCE)));
            runningBean.setUnitDistance(cursor.getString(cursor.getColumnIndex(COLUMN_DISTANCE_UNIT)));
            runningBean.setDenivele(cursor.getDouble(cursor.getColumnIndex(COLUMN_DENIVELE)));
            runningBean.setUnitElevation(cursor.getString(cursor.getColumnIndex(COLUMN_ELEVATION_UNIT)));
            runningBean.setTemps(cursor.getString(cursor.getColumnIndex(COLUMN_TIME)));
            runningBean.setCity(cursor.getString(cursor.getColumnIndex(COLUMN_CITY)));
            runningBean.setCalory(cursor.getInt(cursor.getColumnIndex(COLUMN_CALORY)));
        }

        if (!cursor.isClosed()) {
            cursor.close();
        }

        return runningBean;
    }

    /**
     *
     * Mise à jour du commentaire running
     *
     * @return boolean
     */
    public boolean majRunningComment(int idRunning, String comment) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_COMMENT, comment);

        int result = db.update(TABLE_NAME_RUNNING, values, COLUMN_ID_RUNNING + " = ?", new String[] {Integer.toString(idRunning)});

        return result == 1;
    }

    /**
     *
     * Mise à jour du chemin fichie de la map du running
     *
     * @return boolean
     */
    public boolean majRunningMapFilePath(int idRunning, String mapRunningFilePath) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_MAP_FILE_PATH, mapRunningFilePath);

        int result = db.update(TABLE_NAME_RUNNING, values, COLUMN_ID_RUNNING + " = ?", new String[] {Integer.toString(idRunning)});

        return result == 1;
    }

    /**
     *
     * Retrouve l'altitude max de la course running id
     *
     * @param idCourse id
     * @return altitude max
     */
    public double getMaxAltitude(int idCourse) {

        String selectQuery = "SELECT Max(altitude) as altitude from RunningStep where altitude > 0 and id_running = " + idCourse;
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);

        cursor.moveToFirst();

        Double result = cursor.getDouble(cursor.getColumnIndex("altitude"));

        if (!cursor.isClosed()) {
            cursor.close();
        }

        return result;
    }

    /**
     *
     * Retrouve l'altitude max de la course running id
     *
     * @param idRunning id
     * @return LatLng
     */
    public LatLng getFirstLocation(Integer idRunning) {

        SQLiteDatabase db = this.getReadableDatabase();

        String[] columns = new String[] { COLUMN_LATITUDE, COLUMN_LONGITUDE };
        String whereClause = COLUMN_ID_RUNNING + " = ?";
        String[] whereArgs = new String[] { idRunning.toString() };

        Cursor cursor = db.query(TABLE_NAME_RUNNING_STEP, columns, whereClause, whereArgs, null, null, COLUMN_ID_RUNNING_STEP + " ASC");

        LatLng latLng = null;
        if(cursor.moveToFirst()) {

            double lat = cursor.getDouble(cursor.getColumnIndex(COLUMN_LATITUDE));
            double lon = cursor.getDouble(cursor.getColumnIndex(COLUMN_LONGITUDE));
            latLng = new LatLng(lat, lon);
        }

        if (!cursor.isClosed()) {
            cursor.close();
        }

        return latLng;
    }

    /**
     *
     * Retrouve l'altitude min de la course running id
     *
     * @param idCourse id
     * @return altitude min
     */
    public double getMinAltitude(int idCourse) {

        String selectQuery = "SELECT Min(altitude) as altitude from RunningStep where altitude > 0 and id_running = " + idCourse;
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);

        cursor.moveToFirst();

        double result = cursor.getDouble(cursor.getColumnIndex("altitude"));

        if (!cursor.isClosed()) {
            cursor.close();
        }

        return result;
    }

    /**
     * Ajout d'un running step en base
     *
     * @param runningStepBean bean
     * @return boolean
     */
    public boolean addRunningStepBean(RunningStepBean runningStepBean) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(COLUMN_LATITUDE, runningStepBean.getLatitude());
        contentValues.put(COLUMN_LONGITUDE, runningStepBean.getLongitude());
        contentValues.put(COLUMN_ACCURACY, RunningHelper.roundDouble(runningStepBean.getAccuracy()));
        contentValues.put(COLUMN_ALTITUDE, RunningHelper.roundDouble(runningStepBean.getAltitude()));
        contentValues.put(COLUMN_DISTANCE_BETWEEN, RunningHelper.roundDouble(runningStepBean.getDistance()));
        contentValues.put(COLUMN_TIME_DELAY, runningStepBean.getTimeDelay());
        contentValues.put(COLUMN_VITESSE, RunningHelper.roundDouble(runningStepBean.getVitesse()));
        contentValues.put(COLUMN_DATE, RunningHelper.getDateDuJour());
        contentValues.put(COLUMN_HORODATAGE, runningStepBean.getHorodatage());
        contentValues.put(COLUMN_ID_RUNNING, runningStepBean.getIdRunning());

        long result = db.insert(TABLE_NAME_RUNNING_STEP, null, contentValues);
        db.close();

        return result != -1;
    }

    /**
     *
     * Retourn les coord LtLng de la course passée en paramètre
     *
     *
     * @param idRunning id course
     * @return liste coord
     *
     */
    public List<LatLng> getAllRunningStepLocalisation(Integer idRunning) {

        List<LatLng> runningStepLocalisationList = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();

        String[] columns = new String[] { COLUMN_LATITUDE, COLUMN_LONGITUDE };
        String whereClause = COLUMN_ID_RUNNING + " = ?";
        String[] whereArgs = new String[] { idRunning.toString() };

        Cursor cursor = db.query(TABLE_NAME_RUNNING_STEP, columns, whereClause, whereArgs, null, null, COLUMN_ID_RUNNING_STEP + " ASC");

        if(cursor.getCount() > 0) {

            LatLng latLng;

            while (cursor.moveToNext()) {

                latLng = new LatLng(cursor.getDouble(cursor.getColumnIndex(COLUMN_LATITUDE)), cursor.getDouble(cursor.getColumnIndex(COLUMN_LONGITUDE)));
                runningStepLocalisationList.add(latLng);
            }
        }

        if (!cursor.isClosed()) {
            cursor.close();
        }

        return runningStepLocalisationList;
    }

    /**
     *
     *
     * Hashage string en md5
     *
     * @param s string
     *
     */
    private String md5(String s) {
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest)
                hexString.append(Integer.toHexString(0xFF & aMessageDigest));
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * Query sqlite_sequence table and search for the AUTOINCREMENT value for <code>tableName</code>
     * @param tableName The table name with which the AUTOINCREMENT value is associated.
     *
     * @return The next AUTOINCREMENT value for <code>tableName</code>
     * If an INSERT call was not previously executed on <code>tableName</code>, the value 1 will
     * be returned. Otherwise, the returned value will be equal to the value of the current
     * AUTOINCREMENT + 1.
     */
    public long getNextAutoIncrement(String tableName) {
    /*
     * From the docs:
     * SQLite keeps track of the largest ROWID using an internal table named "sqlite_sequence".
     * The sqlite_sequence table is created and initialized automatically
     * whenever a normal table that contains an AUTOINCREMENT column is created.
     */
        String sqliteSequenceTableName = "sqlite_sequence";
    /*
     * Relevant columns to retrieve from <code>sqliteSequenceTableName</code>
     */
        String[] columns = {"name", "seq"};

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(sqliteSequenceTableName, columns, null, null, null, null, null);

        long autoIncrement = 0;
    /*
     * We can indeed fail here ( If an insert call was not previously executed on <code>tableName</code>),
     * but no worries:
     * <code>autoIncrement + 1</code> will be returned either way.
     */
        if (cursor.getCount() > 0) {
            int indexName = cursor.getColumnIndex(columns[0]);
            int indexSeq = cursor.getColumnIndex(columns[1]);

            while (cursor.moveToNext()) {
            /*
             * Are we currently looking at <code>tableName</code> ?
             */
                if (cursor.getString(indexName).equals(tableName)) {
                /* Great, take the AUTOINCREMENT value and get out */
                    autoIncrement = cursor.getLong(indexSeq);
                    break;
                }
            }
        }

        cursor.close();

        return autoIncrement + 1;
    }
}