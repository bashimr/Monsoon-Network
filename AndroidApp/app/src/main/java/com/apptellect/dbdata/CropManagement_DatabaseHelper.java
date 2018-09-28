package com.apptellect.dbdata;
/**
 * Created by Karthi on 9/21/2018.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.apptellect.utilities.Utils;

import java.util.ArrayList;
import java.util.List;

public class CropManagement_DatabaseHelper extends SQLiteOpenHelper {
    //Database name
    public static final String DATABASE_NAME = "CropManagement.db";

    // Tables Name
    public static final String CROPTABLE_NAME = "CropTable";
    public static final String CROPCOORDINATES_NAME = "CropCoordinates";
    public static final String CROPPHOTOS_NAME = "CropPhotos";

    // Crop Table Columns
    public static final String CROP_ID = "CROP_ID"; // Crop Coordinates and Crop Photos
    public static final String CROP_AREA = "CROP_AREA";
    public static final String CROP_CURRENCY = "CROP_CURRENCY";
    public static final String CROP_SUBTYPE = "CROP_SUBTYPE";
    public static final String FARMER_OWNER = "FARMER_OWNER";
    public static final String HARVEST_DATE = "HARVEST_DATE";
    public static final String MAX_YEILD_VALUE = "MAX_YEILD_VALUE";
    public static final String SEEDING_DATE = "SEEDING_DATE";
    public static final String STATUS = "STATUS";
    public static final String TYPEOFCROP = "TYPEOFCROP";

    // Crop Coordinates Table Column
    public static final String CROP_COORID = "CROP_COORID";
    public static final String CROP_COOR_LATTITUDE = "CROP_COOR_LATTITUDE";
    public static final String CROP_COOR_LONGITUDE = "CROP_COOR_LONGITUDE";

    // Crop Photos Table Column
    public static final String CROP_PHOTOID = "CROP_PHOTOID";
    public static final String CROP_PHOTO = "CROP_PHOTO";
    public static final String CROP_PHOTO_DATE = "CROP_PHOTO_DATE";
    public static final String CROP_PHOTO_LATTITUDE = "CROP_PHOTO_LATTITUDE";
    public static final String CROP_PHOTO_LONGITUDE = "CROP_PHOTO_LONGITUDE";
    private final Utils mUtils;

    public CropManagement_DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
        mUtils = new Utils(context);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create Table CropTable,CropCoordinates,CropPhotos
        db.execSQL("create table " + CROPTABLE_NAME + " (CROP_ID TEXT,CROP_AREA TEXT,CROP_CURRENCY TEXT,CROP_SUBTYPE TEXT,FARMER_OWNER TEXT,HARVEST_DATE TEXT,MAX_YEILD_VALUE TEXT,SEEDING_DATE TEXT,STATUS TEXT,TYPEOFCROP TEXT)");
        db.execSQL("create table " + CROPCOORDINATES_NAME + " (CROP_COORID TEXT,CROP_ID TEXT,CROP_COOR_LATTITUDE TEXT,CROP_COOR_LONGITUDE TEXT)");
        db.execSQL("create table " + CROPPHOTOS_NAME + " (CROP_PHOTOID TEXT,CROP_PHOTO blob,CROP_PHOTO_DATE TEXT,CROP_ID TEXT,CROP_PHOTO_LATTITUDE TEXT,CROP_PHOTO_LONGITUDE TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + CROPTABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + CROPCOORDINATES_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + CROPPHOTOS_NAME);
        onCreate(db);
    }

    //Insert Crop Table Data
    public boolean insertCropTableData(CropTable cropTable_pojo) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(CROP_ID, cropTable_pojo.getCropid());
        contentValues.put(CROP_AREA, cropTable_pojo.getCroparea());
        contentValues.put(CROP_CURRENCY, cropTable_pojo.getCropcurrency());
        contentValues.put(CROP_SUBTYPE, cropTable_pojo.getCropsubtype());
        contentValues.put(FARMER_OWNER, cropTable_pojo.getFarmerowner());
        contentValues.put(HARVEST_DATE, cropTable_pojo.getHarvestdate());
        contentValues.put(MAX_YEILD_VALUE, cropTable_pojo.getMaxyeildvalue());
        contentValues.put(SEEDING_DATE, cropTable_pojo.getSeedingdate());
        contentValues.put(STATUS, cropTable_pojo.getStatue());
        contentValues.put(TYPEOFCROP, cropTable_pojo.getTypeofcrop());
        long result = db.insert(CROPTABLE_NAME, null, contentValues);
        if (result == -1)
            return false;
        else
            return true;
    }

    //Insert Crop Co-ordinates Table Data
    public boolean insertCropCoorTableData(Crop_Coordinate_module crop_coordinate_pojo) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(CROP_ID, crop_coordinate_pojo.getCropid());
        contentValues.put(CROP_COORID, crop_coordinate_pojo.getCropcoorid());
        contentValues.put(CROP_COOR_LATTITUDE, crop_coordinate_pojo.getCroplatitude());
        contentValues.put(CROP_COOR_LONGITUDE, crop_coordinate_pojo.getCroplongitude());
        long result = db.insert(CROPCOORDINATES_NAME, null, contentValues);
        if (result == -1)
            return false;
        else
            return true;
    }

    //Insert Crop Photo Table Data
    public boolean insertCropPhotoTableData(Crop_Photo_module crop_photo_pojo) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(CROP_PHOTOID, crop_photo_pojo.getCropphotoid());
        contentValues.put(CROP_ID, crop_photo_pojo.getCropid());
        contentValues.put(CROP_PHOTO_LATTITUDE, crop_photo_pojo.getPhotolatitude());
        contentValues.put(CROP_PHOTO_LONGITUDE, crop_photo_pojo.getPhotolongitude());
        contentValues.put(CROP_PHOTO, mUtils.getPictureByteOfArray(crop_photo_pojo.getBitmap()));
        long result = db.insert(CROPPHOTOS_NAME, null, contentValues);
        if (result == -1)
            return false;
        else
            return true;
    }

    //Insert List of Crop Co-ordinates Table Data
    public void insertListOfCropCoorTableData(List<Crop_Coordinate_module> listcrop_coordinate_pojo) {
        for (int i = 0; i < listcrop_coordinate_pojo.size(); i++) {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(CROP_ID, listcrop_coordinate_pojo.get(i).getCropid());
            contentValues.put(CROP_COORID, listcrop_coordinate_pojo.get(i).getCropcoorid());
            contentValues.put(CROP_COOR_LATTITUDE, listcrop_coordinate_pojo.get(i).getCroplatitude());
            contentValues.put(CROP_COOR_LONGITUDE, listcrop_coordinate_pojo.get(i).getCroplongitude());
            long result = db.insert(CROPCOORDINATES_NAME, null, contentValues);
        }
    }

    //Insert List of Crop Photo Table Data
    public void insertListOfCropPhotoTableData(List<Crop_Photo_module> listcrop_photo_pojo) {
        for (int j = 0; j < listcrop_photo_pojo.size(); j++) {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(CROP_PHOTOID, listcrop_photo_pojo.get(j).getCropphotoid());
            contentValues.put(CROP_ID, listcrop_photo_pojo.get(j).getCropid());
            contentValues.put(CROP_PHOTO_LATTITUDE, listcrop_photo_pojo.get(j).getPhotolatitude());
            contentValues.put(CROP_PHOTO_LONGITUDE, listcrop_photo_pojo.get(j).getPhotolongitude());
            contentValues.put(CROP_PHOTO_DATE, listcrop_photo_pojo.get(j).getDate());
            contentValues.put(CROP_PHOTO, mUtils.getPictureByteOfArray(listcrop_photo_pojo.get(j).getBitmap()));
            long result = db.insert(CROPPHOTOS_NAME, null, contentValues);
        }
    }


    //Get List of Crop Table Data
    public List<CropTable> getAllCropTableData() {
        List<CropTable> list_croptable = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from " + CROPTABLE_NAME, null);
        if (cursor.moveToFirst()) {
            do {
                CropTable croppojo = new CropTable();
                croppojo.setCropid(cursor.getString(cursor.getColumnIndex(CropManagement_DatabaseHelper.CROP_ID)));
                croppojo.setCroparea(cursor.getString(cursor.getColumnIndex(CropManagement_DatabaseHelper.CROP_AREA)));
                croppojo.setCropcurrency(cursor.getString(cursor.getColumnIndex(CropManagement_DatabaseHelper.CROP_CURRENCY)));
                croppojo.setCropsubtype(cursor.getString(cursor.getColumnIndex(CropManagement_DatabaseHelper.CROP_SUBTYPE)));
                croppojo.setFarmerowner(cursor.getString(cursor.getColumnIndex(CropManagement_DatabaseHelper.FARMER_OWNER)));
                croppojo.setHarvestdate(cursor.getString(cursor.getColumnIndex(CropManagement_DatabaseHelper.HARVEST_DATE)));
                croppojo.setMaxyeildvalue(cursor.getString(cursor.getColumnIndex(CropManagement_DatabaseHelper.MAX_YEILD_VALUE)));
                croppojo.setSeedingdate(cursor.getString(cursor.getColumnIndex(CropManagement_DatabaseHelper.SEEDING_DATE)));
                croppojo.setStatue(cursor.getString(cursor.getColumnIndex(CropManagement_DatabaseHelper.STATUS)));
                croppojo.setTypeofcrop(cursor.getString(cursor.getColumnIndex(CropManagement_DatabaseHelper.TYPEOFCROP)));
                list_croptable.add(croppojo);

            } while (cursor.moveToNext());
        }
        return list_croptable;
    }

    //Get List of Crop Co-ordinates Table Data
    public List<Crop_Coordinate_module> getAllCropCoorTableData() {
        List<Crop_Coordinate_module> list_cropcoortable = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from " + CROPCOORDINATES_NAME, null);
        if (cursor.moveToFirst()) {
            do {
                Crop_Coordinate_module cropcoorpojo = new Crop_Coordinate_module();
                cropcoorpojo.setCropid(cursor.getString(cursor.getColumnIndex(CropManagement_DatabaseHelper.CROP_ID)));
                cropcoorpojo.setCropcoorid(cursor.getString(cursor.getColumnIndex(CropManagement_DatabaseHelper.CROP_COORID)));
                cropcoorpojo.setCroplatitude(cursor.getString(cursor.getColumnIndex(CropManagement_DatabaseHelper.CROP_COOR_LATTITUDE)));
                cropcoorpojo.setCroplongitude(cursor.getString(cursor.getColumnIndex(CropManagement_DatabaseHelper.CROP_COOR_LONGITUDE)));
                list_cropcoortable.add(cropcoorpojo);

            } while (cursor.moveToNext());
        }
        return list_cropcoortable;
    }

    //Get List of Crop Photo Table Data
    public List<Crop_Photo_module> getAllCropPhotoTableData(String cropID) {
        List<Crop_Photo_module> list_cropphototable = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from " + CROPPHOTOS_NAME + " WHERE " + CROP_ID + " = '" + cropID + "'", null);
        if (cursor.moveToFirst()) {
            do {
                Crop_Photo_module cropphotopojo = new Crop_Photo_module();
                cropphotopojo.setCropid(cursor.getString(cursor.getColumnIndex(CropManagement_DatabaseHelper.CROP_ID)));
                cropphotopojo.setCropphotoid(cursor.getString(cursor.getColumnIndex(CropManagement_DatabaseHelper.CROP_PHOTOID)));
                cropphotopojo.setDate(cursor.getString(cursor.getColumnIndex(CropManagement_DatabaseHelper.CROP_PHOTO_DATE)));
                cropphotopojo.setBitmap(mUtils.byteToBitmap(cursor));
                cropphotopojo.setPhotolatitude(cursor.getString(cursor.getColumnIndex(CropManagement_DatabaseHelper.CROP_PHOTO_LATTITUDE)));
                cropphotopojo.setPhotolongitude(cursor.getString(cursor.getColumnIndex(CropManagement_DatabaseHelper.CROP_PHOTO_LONGITUDE)));
                list_cropphototable.add(cropphotopojo);

            } while (cursor.moveToNext());
        }
        return list_cropphototable;
    }

    //Update Crop Table Data by using Cropid
    public boolean updateCropTableData(CropTable cropTable_pojo) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(CROP_AREA, cropTable_pojo.getCroparea());
        contentValues.put(CROP_CURRENCY, cropTable_pojo.getCropcurrency());
        contentValues.put(CROP_SUBTYPE, cropTable_pojo.getCropsubtype());
        contentValues.put(FARMER_OWNER, cropTable_pojo.getFarmerowner());
        contentValues.put(HARVEST_DATE, cropTable_pojo.getHarvestdate());
        contentValues.put(MAX_YEILD_VALUE, cropTable_pojo.getMaxyeildvalue());
        contentValues.put(SEEDING_DATE, cropTable_pojo.getSeedingdate());
        contentValues.put(STATUS, cropTable_pojo.getStatue());
        contentValues.put(TYPEOFCROP, cropTable_pojo.getTypeofcrop());
        db.update(CROPTABLE_NAME, contentValues, "ID = ?", new String[]{cropTable_pojo.getCropid()});
        return true;
    }

    //Update Crop Table Data by using Cropcoorid
    public boolean updateCropCoorTableData(Crop_Coordinate_module crop_coordinate_pojo) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(CROP_ID, crop_coordinate_pojo.getCropid());
        contentValues.put(CROP_COORID, crop_coordinate_pojo.getCropcoorid());
        contentValues.put(CROP_COOR_LATTITUDE, crop_coordinate_pojo.getCroplatitude());
        contentValues.put(CROP_COOR_LONGITUDE, crop_coordinate_pojo.getCroplongitude());
        db.update(CROPCOORDINATES_NAME, contentValues, "ID = ?", new String[]{crop_coordinate_pojo.getCropcoorid()});
        return true;
    }

    //Update Crop Table Data by using Cropphotoid
    public boolean updateCropPhotoTableData(Crop_Photo_module crop_photo_pojo) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(CROP_PHOTOID, crop_photo_pojo.getCropphotoid());
        contentValues.put(CROP_ID, crop_photo_pojo.getCropid());
        contentValues.put(CROP_PHOTO_LATTITUDE, crop_photo_pojo.getPhotolatitude());
        contentValues.put(CROP_PHOTO_LONGITUDE, crop_photo_pojo.getPhotolongitude());
        db.update(CROPPHOTOS_NAME, contentValues, "ID = ?", new String[]{crop_photo_pojo.getCropphotoid()});
        return true;
    }

    //Delete Crop Table Data by cropid
    public Integer deleteCropTableData(String cropid) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(CROPTABLE_NAME, "ID = ?", new String[]{cropid});
    }

    //Delete Crop Table Data by cropcoorid
    public Integer deleteCropCoorTableData(String cropcoorid) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(CROPCOORDINATES_NAME, "ID = ?", new String[]{cropcoorid});
    }

    //Delete Crop Table Data by cropphotoid
    public Integer deleteCropPhotoTableData(String cropphotoid) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(CROPPHOTOS_NAME, "ID = ?", new String[]{cropphotoid});
    }
}