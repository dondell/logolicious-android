package com.olav.logolicious.customize.datamodel;

import android.media.ExifInterface;
import android.os.Build;
import android.text.format.DateUtils;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Dondell A. Batac on 8/9/2017.
 */

public class ImageExif {

    public static final String TAG = "ImageExif";
    public static String TAG_DATETIME;
    public static String TAG_FLASH;
    public static String TAG_FOCAL_LENGTH;
    public static String TAG_FLASH2;
    public static String TAG_GPS_LATITUDE;
    public static String TAG_GPS_LATITUDE_REF;
    public static String TAG_GPS_LONGITUDE;
    public static String TAG_GPS_LONGITUDE_REF;
    public static String TAG_GPS_PROCESSING_METHOD;
    public static String TAG_GPS_TIMESTAMP;
    public static String TAG_IMAGE_LENGTH;
    public static String TAG_IMAGE_WIDTH;
    public static String TAG_MAKE;
    public static String TAG_MODEL;
    public static String TAG_ORIENTATION;
    public static String TAG_WHITE_BALANCE;
    public static String TAG_APERTURE;
    public static String TAG_SHUTTER_SPEED_VALUE;
    public static String TAG_METERING_MODE;
    public static String TAG_ISO;
    public static String TAG_ISO_SPEED_RATINGS;
    public static String TAG_RW2_ISO;
    public static String TAG_COPYRIGHT;
    public static String TAG_SUBSEC_TIME;
    public static String TAG_APERTURE_VALUE;
    public static String TAG_SATURATION;
    public static String TAG_SHARPNESS;
    public static String TAG_CONTRAST;
    public static String TAG_USER_COMMENT;
    public static String TAG_ARTIST;
    public static String TAG_BITS_PER_SAMPLE;
    public static String TAG_BRIGHTNESS_VALUE;
    public static String TAG_CFA_PATTERN;
    public static String TAG_COLOR_SPACE;
    public static String TAG_COMPONENTS_CONFIGURATION;
    public static String TAG_COMPRESSED_BITS_PER_PIXEL;
    public static String TAG_COMPRESSION;
    public static String TAG_CUSTOM_RENDERED;
    public static String TAG_DATETIME_DIGITIZED;
    public static String TAG_DATETIME_ORIGINAL;
    public static String TAG_DEFAULT_CROP_SIZE;
    public static String TAG_DEVICE_SETTING_DESCRIPTION;
    public static String TAG_DIGITAL_ZOOM_RATIO;
    public static String TAG_DNG_VERSION;
    public static String TAG_EXIF_VERSION;
    public static String TAG_EXPOSURE_BIAS_VALUE;
    public static String TAG_EXPOSURE_INDEX;
    public static String TAG_EXPOSURE_MODE;
    public static String TAG_EXPOSURE_PROGRAM;
    public static String TAG_EXPOSURE_TIME;
    public static String TAG_FILE_SOURCE;
    public static String TAG_FLASHPIX_VERSION;
    public static String TAG_FLASH_ENERGY;
    public static String TAG_F_NUMBER;
    public static String TAG_FOCAL_LENGTH_IN_35MM_FILM;
    public static String TAG_FOCAL_PLANE_RESOLUTION_UNIT;
    public static String TAG_FOCAL_PLANE_X_RESOLUTION;
    public static String TAG_FOCAL_PLANE_Y_RESOLUTION;
    public static String TAG_GAIN_CONTROL;
    public static String TAG_GPS_ALTITUDE;
    public static String TAG_GPS_ALTITUDE_REF;
    public static String TAG_GPS_AREA_INFORMATION;
    public static String TAG_GPS_DATESTAMP;
    public static String TAG_GPS_DEST_BEARING;
    public static String TAG_GPS_DEST_BEARING_REF;
    public static String TAG_GPS_DEST_DISTANCE_REF;
    public static String TAG_GPS_DEST_LATITUDE;
    public static String TAG_GPS_DEST_LATITUDE_REF;
    public static String TAG_GPS_DEST_LONGITUDE;
    public static String TAG_GPS_DEST_LONGITUDE_REF;
    public static String TAG_GPS_DIFFERENTIAL;
    public static String TAG_GPS_DOP;
    public static String TAG_GPS_IMG_DIRECTION;
    public static String TAG_GPS_IMG_DIRECTION_REF;
    public static String TAG_GPS_MAP_DATUM;
    public static String TAG_GPS_MEASURE_MODE;
    public static String TAG_GPS_SATELLITES;
    public static String TAG_GPS_SPEED;
    public static String TAG_GPS_SPEED_REF;
    public static String TAG_GPS_STATUS;
    public static String TAG_GPS_TRACK;
    public static String TAG_GPS_TRACK_REF;
    public static String TAG_GPS_VERSION_ID;
    public static String TAG_IMAGE_DESCRIPTION;
    public static String TAG_IMAGE_UNIQUE_ID;
    public static String TAG_INTEROPERABILITY_INDEX;
    public static String TAG_JPEG_INTERCHANGE_FORMAT;
    public static String TAG_JPEG_INTERCHANGE_FORMAT_LENGTH;
    public static String TAG_LIGHT_SOURCE;
    public static String TAG_MAKER_NOTE;
    public static String TAG_MAX_APERTURE_VALUE;
    public static String TAG_NEW_SUBFILE_TYPE;
    public static String TAG_OECF;
    public static String TAG_ORF_ASPECT_FRAME;
    public static String TAG_ORF_PREVIEW_IMAGE_LENGTH;
    public static String TAG_ORF_PREVIEW_IMAGE_START;
    public static String TAG_ORF_THUMBNAIL_IMAGE;
    public static String TAG_PHOTOMETRIC_INTERPRETATION;
    public static String TAG_PIXEL_X_DIMENSION;
    public static String TAG_PIXEL_Y_DIMENSION;
    public static String TAG_PLANAR_CONFIGURATION;
    public static String TAG_PRIMARY_CHROMATICITIES;
    public static String TAG_REFERENCE_BLACK_WHITE;
    public static String TAG_RELATED_SOUND_FILE;
    public static String TAG_RESOLUTION_UNIT;
    public static String TAG_ROWS_PER_STRIP;
    public static String TAG_RW2_JPG_FROM_RAW;
    public static String TAG_RW2_SENSOR_BOTTOM_BORDER;
    public static String TAG_RW2_SENSOR_LEFT_BORDER;
    public static String TAG_RW2_SENSOR_RIGHT_BORDER;
    public static String TAG_RW2_SENSOR_TOP_BORDER;
    public static String TAG_SAMPLES_PER_PIXEL;
    public static String TAG_SCENE_CAPTURE_TYPE;
    public static String TAG_SCENE_TYPE;
    public static String TAG_SENSING_METHOD;
    public static String TAG_SOFTWARE;
    public static String TAG_SPATIAL_FREQUENCY_RESPONSE;
    public static String TAG_SPECTRAL_SENSITIVITY;
    public static String TAG_STRIP_BYTE_COUNTS;
    public static String TAG_STRIP_OFFSETS;
    public static String TAG_SUBFILE_TYPE;
    public static String TAG_SUBJECT_AREA;
    public static String TAG_SUBJECT_DISTANCE;
    public static String TAG_SUBJECT_DISTANCE_RANGE;
    public static String TAG_SUBJECT_LOCATION;
    public static String TAG_SUBSEC_TIME_DIGITIZED;
    public static String TAG_SUBSEC_TIME_ORIGINAL;
    public static String TAG_THUMBNAIL_IMAGE_LENGTH;
    public static String TAG_THUMBNAIL_IMAGE_WIDTH;
    public static String TAG_TRANSFER_FUNCTION;
    public static String TAG_WHITE_POINT;
    public static String TAG_X_RESOLUTION;
    public static String TAG_Y_CB_CR_COEFFICIENTS;
    public static String TAG_Y_CB_CR_POSITIONING;
    public static String TAG_Y_CB_CR_SUB_SAMPLING;
    public static String TAG_Y_RESOLUTION;

    public void parse(String filepath) {

        File file = new File(filepath);
        Date lastModDate = new Date(file.lastModified());
        System.out.println("File last modified @ : " + lastModDate.toString());

        ExifInterface exif;
        try {
            exif = new ExifInterface(filepath);
            TAG_ORIENTATION = getExifTag(exif, ExifInterface.TAG_ORIENTATION);
            TAG_DATETIME = getExifTag(exif, ExifInterface.TAG_DATETIME);
            TAG_FLASH = getExifTag(exif, ExifInterface.TAG_FLASH);
            TAG_FOCAL_LENGTH = getExifTag(exif, ExifInterface.TAG_FOCAL_LENGTH);
            TAG_FLASH2 = getExifTag(exif, ExifInterface.TAG_FLASH);
            TAG_GPS_LATITUDE = getExifTag(exif, ExifInterface.TAG_GPS_LATITUDE);
            TAG_GPS_LATITUDE_REF = getExifTag(exif, ExifInterface.TAG_GPS_LATITUDE_REF);
            TAG_GPS_LONGITUDE = getExifTag(exif, ExifInterface.TAG_GPS_LONGITUDE);
            TAG_GPS_LONGITUDE_REF = getExifTag(exif, ExifInterface.TAG_GPS_LONGITUDE_REF);
            TAG_GPS_PROCESSING_METHOD = getExifTag(exif, ExifInterface.TAG_GPS_PROCESSING_METHOD);
            TAG_GPS_TIMESTAMP = getExifTag(exif, ExifInterface.TAG_GPS_TIMESTAMP);
            TAG_IMAGE_LENGTH = getExifTag(exif, ExifInterface.TAG_IMAGE_LENGTH);
            TAG_IMAGE_WIDTH = getExifTag(exif, ExifInterface.TAG_IMAGE_WIDTH);
            TAG_MAKE = getExifTag(exif, ExifInterface.TAG_MAKE);
            TAG_MODEL = getExifTag(exif, ExifInterface.TAG_MODEL);
            TAG_WHITE_BALANCE = getExifTag(exif, ExifInterface.TAG_WHITE_BALANCE);
            TAG_APERTURE = getExifTag(exif, ExifInterface.TAG_APERTURE);
            TAG_ISO = getExifTag(exif, ExifInterface.TAG_ISO);
            TAG_EXPOSURE_TIME = getExifTag(exif, ExifInterface.TAG_EXPOSURE_TIME);
            TAG_GPS_ALTITUDE = getExifTag(exif, ExifInterface.TAG_GPS_ALTITUDE);
            TAG_GPS_ALTITUDE_REF = getExifTag(exif, ExifInterface.TAG_GPS_ALTITUDE_REF);
            TAG_GPS_DATESTAMP = getExifTag(exif, ExifInterface.TAG_GPS_DATESTAMP);
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            TAG_SHUTTER_SPEED_VALUE = getExifTag(exif, ExifInterface.TAG_SHUTTER_SPEED_VALUE);
            TAG_METERING_MODE = getExifTag(exif, ExifInterface.TAG_METERING_MODE);
            TAG_ISO_SPEED_RATINGS = getExifTag(exif, ExifInterface.TAG_ISO_SPEED_RATINGS);
            TAG_COPYRIGHT = getExifTag(exif, ExifInterface.TAG_COPYRIGHT);
            TAG_SUBSEC_TIME = getExifTag(exif, ExifInterface.TAG_SUBSEC_TIME);
            TAG_APERTURE_VALUE = getExifTag(exif, ExifInterface.TAG_APERTURE_VALUE);
            TAG_SATURATION = getExifTag(exif, ExifInterface.TAG_SATURATION);
            TAG_SHARPNESS = getExifTag(exif, ExifInterface.TAG_SHARPNESS);
            TAG_CONTRAST = getExifTag(exif, ExifInterface.TAG_CONTRAST);
            TAG_USER_COMMENT = getExifTag(exif, ExifInterface.TAG_USER_COMMENT);
            TAG_ARTIST = getExifTag(exif, ExifInterface.TAG_ARTIST);
            TAG_BITS_PER_SAMPLE = getExifTag(exif, ExifInterface.TAG_BITS_PER_SAMPLE);
            TAG_BRIGHTNESS_VALUE = getExifTag(exif, ExifInterface.TAG_BRIGHTNESS_VALUE);
            TAG_CFA_PATTERN = getExifTag(exif, ExifInterface.TAG_CFA_PATTERN);
            TAG_COLOR_SPACE = getExifTag(exif, ExifInterface.TAG_COLOR_SPACE);
            TAG_COMPONENTS_CONFIGURATION = getExifTag(exif, ExifInterface.TAG_COMPONENTS_CONFIGURATION);
            TAG_COMPRESSED_BITS_PER_PIXEL = getExifTag(exif, ExifInterface.TAG_COMPRESSED_BITS_PER_PIXEL);
            TAG_COMPRESSION = getExifTag(exif, ExifInterface.TAG_COMPRESSION);
            TAG_CUSTOM_RENDERED = getExifTag(exif, ExifInterface.TAG_CUSTOM_RENDERED);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                TAG_DATETIME_DIGITIZED = getExifTag(exif, ExifInterface.TAG_DATETIME_DIGITIZED);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                TAG_DATETIME_ORIGINAL = getExifTag(exif, ExifInterface.TAG_DATETIME_ORIGINAL);
                TAG_DEVICE_SETTING_DESCRIPTION = getExifTag(exif, ExifInterface.TAG_DEVICE_SETTING_DESCRIPTION);
                TAG_DIGITAL_ZOOM_RATIO = getExifTag(exif, ExifInterface.TAG_DIGITAL_ZOOM_RATIO);
                TAG_EXIF_VERSION = getExifTag(exif, ExifInterface.TAG_EXIF_VERSION);
                TAG_EXPOSURE_BIAS_VALUE = getExifTag(exif, ExifInterface.TAG_EXPOSURE_BIAS_VALUE);
                TAG_EXPOSURE_INDEX = getExifTag(exif, ExifInterface.TAG_EXPOSURE_INDEX);
                TAG_EXPOSURE_MODE = getExifTag(exif, ExifInterface.TAG_EXPOSURE_MODE);
                TAG_EXPOSURE_PROGRAM = getExifTag(exif, ExifInterface.TAG_EXPOSURE_PROGRAM);
                TAG_FILE_SOURCE = getExifTag(exif, ExifInterface.TAG_FILE_SOURCE);
                TAG_FLASHPIX_VERSION = getExifTag(exif, ExifInterface.TAG_FLASHPIX_VERSION);
                TAG_FLASH_ENERGY = getExifTag(exif, ExifInterface.TAG_FLASH_ENERGY);
                TAG_F_NUMBER = getExifTag(exif, ExifInterface.TAG_F_NUMBER);
                TAG_FOCAL_LENGTH_IN_35MM_FILM = getExifTag(exif, ExifInterface.TAG_FOCAL_LENGTH_IN_35MM_FILM);
                TAG_FOCAL_PLANE_RESOLUTION_UNIT = getExifTag(exif, ExifInterface.TAG_FOCAL_PLANE_RESOLUTION_UNIT);
                TAG_FOCAL_PLANE_X_RESOLUTION = getExifTag(exif, ExifInterface.TAG_FOCAL_PLANE_X_RESOLUTION);
                TAG_FOCAL_PLANE_Y_RESOLUTION = getExifTag(exif, ExifInterface.TAG_FOCAL_PLANE_Y_RESOLUTION);
                TAG_GAIN_CONTROL = getExifTag(exif, ExifInterface.TAG_GAIN_CONTROL);
                TAG_GPS_AREA_INFORMATION = getExifTag(exif, ExifInterface.TAG_GPS_AREA_INFORMATION);
                TAG_GPS_DEST_BEARING = getExifTag(exif, ExifInterface.TAG_GPS_DEST_BEARING);
                TAG_GPS_DEST_BEARING_REF = getExifTag(exif, ExifInterface.TAG_GPS_DEST_BEARING_REF);
                TAG_GPS_DEST_BEARING_REF = getExifTag(exif, ExifInterface.TAG_GPS_DEST_BEARING_REF);
                TAG_GPS_DEST_DISTANCE_REF = getExifTag(exif, ExifInterface.TAG_GPS_DEST_DISTANCE_REF);
                TAG_GPS_DEST_LATITUDE = getExifTag(exif, ExifInterface.TAG_GPS_DEST_LATITUDE);
                TAG_GPS_DEST_LATITUDE_REF = getExifTag(exif, ExifInterface.TAG_GPS_DEST_LATITUDE_REF);
                TAG_GPS_DEST_LONGITUDE = getExifTag(exif, ExifInterface.TAG_GPS_DEST_LONGITUDE);
                TAG_GPS_DEST_LONGITUDE_REF = getExifTag(exif, ExifInterface.TAG_GPS_DEST_LONGITUDE_REF);
                TAG_GPS_DIFFERENTIAL = getExifTag(exif, ExifInterface.TAG_GPS_DIFFERENTIAL);
                TAG_GPS_DOP = getExifTag(exif, ExifInterface.TAG_GPS_DOP);
                TAG_GPS_IMG_DIRECTION = getExifTag(exif, ExifInterface.TAG_GPS_IMG_DIRECTION);
                TAG_GPS_IMG_DIRECTION_REF = getExifTag(exif, ExifInterface.TAG_GPS_IMG_DIRECTION_REF);
                TAG_GPS_MAP_DATUM = getExifTag(exif, ExifInterface.TAG_GPS_MAP_DATUM);
                TAG_GPS_MEASURE_MODE = getExifTag(exif, ExifInterface.TAG_GPS_MEASURE_MODE);
                TAG_GPS_SATELLITES = getExifTag(exif, ExifInterface.TAG_GPS_SATELLITES);
                TAG_GPS_SPEED = getExifTag(exif, ExifInterface.TAG_GPS_SPEED);
                TAG_GPS_SPEED_REF = getExifTag(exif, ExifInterface.TAG_GPS_SPEED_REF);
                TAG_GPS_STATUS = getExifTag(exif, ExifInterface.TAG_GPS_STATUS);
                TAG_GPS_TRACK = getExifTag(exif, ExifInterface.TAG_GPS_TRACK);
                TAG_GPS_TRACK_REF = getExifTag(exif, ExifInterface.TAG_GPS_TRACK_REF);
                TAG_GPS_VERSION_ID = getExifTag(exif, ExifInterface.TAG_GPS_VERSION_ID);
                TAG_IMAGE_DESCRIPTION = getExifTag(exif, ExifInterface.TAG_IMAGE_DESCRIPTION);
                TAG_IMAGE_UNIQUE_ID = getExifTag(exif, ExifInterface.TAG_IMAGE_UNIQUE_ID);
                TAG_INTEROPERABILITY_INDEX = getExifTag(exif, ExifInterface.TAG_INTEROPERABILITY_INDEX);
                TAG_JPEG_INTERCHANGE_FORMAT = getExifTag(exif, ExifInterface.TAG_JPEG_INTERCHANGE_FORMAT);
                TAG_JPEG_INTERCHANGE_FORMAT_LENGTH = getExifTag(exif, ExifInterface.TAG_JPEG_INTERCHANGE_FORMAT_LENGTH);
                TAG_LIGHT_SOURCE = getExifTag(exif, ExifInterface.TAG_LIGHT_SOURCE);
                TAG_MAKER_NOTE = getExifTag(exif, ExifInterface.TAG_MAKER_NOTE);
                TAG_MAX_APERTURE_VALUE = getExifTag(exif, ExifInterface.TAG_MAX_APERTURE_VALUE);
                TAG_OECF = getExifTag(exif, ExifInterface.TAG_OECF);
                TAG_PHOTOMETRIC_INTERPRETATION = getExifTag(exif, ExifInterface.TAG_PHOTOMETRIC_INTERPRETATION);
                TAG_PIXEL_X_DIMENSION = getExifTag(exif, ExifInterface.TAG_PIXEL_X_DIMENSION);
                TAG_PIXEL_Y_DIMENSION = getExifTag(exif, ExifInterface.TAG_PIXEL_Y_DIMENSION);
                TAG_PLANAR_CONFIGURATION = getExifTag(exif, ExifInterface.TAG_PLANAR_CONFIGURATION);
                TAG_PRIMARY_CHROMATICITIES = getExifTag(exif, ExifInterface.TAG_PRIMARY_CHROMATICITIES);
                TAG_REFERENCE_BLACK_WHITE = getExifTag(exif, ExifInterface.TAG_REFERENCE_BLACK_WHITE);
                TAG_RELATED_SOUND_FILE = getExifTag(exif, ExifInterface.TAG_RELATED_SOUND_FILE);
                TAG_RESOLUTION_UNIT = getExifTag(exif, ExifInterface.TAG_RESOLUTION_UNIT);
                TAG_ROWS_PER_STRIP = getExifTag(exif, ExifInterface.TAG_ROWS_PER_STRIP);
                TAG_SAMPLES_PER_PIXEL = getExifTag(exif, ExifInterface.TAG_SAMPLES_PER_PIXEL);
                TAG_SCENE_CAPTURE_TYPE = getExifTag(exif, ExifInterface.TAG_SCENE_CAPTURE_TYPE);
                TAG_SCENE_TYPE = getExifTag(exif, ExifInterface.TAG_SCENE_TYPE);
                TAG_SENSING_METHOD = getExifTag(exif, ExifInterface.TAG_SENSING_METHOD);
                TAG_SOFTWARE = getExifTag(exif, ExifInterface.TAG_SOFTWARE);
                TAG_SPATIAL_FREQUENCY_RESPONSE = getExifTag(exif, ExifInterface.TAG_SPATIAL_FREQUENCY_RESPONSE);
                TAG_SPECTRAL_SENSITIVITY = getExifTag(exif, ExifInterface.TAG_SPECTRAL_SENSITIVITY);
                TAG_STRIP_BYTE_COUNTS = getExifTag(exif, ExifInterface.TAG_STRIP_BYTE_COUNTS);
                TAG_STRIP_OFFSETS = getExifTag(exif, ExifInterface.TAG_STRIP_OFFSETS);
                TAG_SUBJECT_AREA = getExifTag(exif, ExifInterface.TAG_SUBJECT_AREA);
                TAG_SUBJECT_DISTANCE = getExifTag(exif, ExifInterface.TAG_SUBJECT_DISTANCE);
                TAG_SUBJECT_DISTANCE_RANGE = getExifTag(exif, ExifInterface.TAG_SUBJECT_DISTANCE_RANGE);
                TAG_SUBJECT_LOCATION = getExifTag(exif, ExifInterface.TAG_SUBJECT_LOCATION);
                TAG_SUBSEC_TIME = getExifTag(exif, ExifInterface.TAG_SUBSEC_TIME);
                TAG_SUBSEC_TIME_DIGITIZED = getExifTag(exif, ExifInterface.TAG_SUBSEC_TIME_DIGITIZED);
                TAG_SUBSEC_TIME_ORIGINAL = getExifTag(exif, ExifInterface.TAG_SUBSEC_TIME_ORIGINAL);
                TAG_THUMBNAIL_IMAGE_LENGTH = getExifTag(exif, ExifInterface.TAG_THUMBNAIL_IMAGE_LENGTH);
                TAG_THUMBNAIL_IMAGE_WIDTH = getExifTag(exif, ExifInterface.TAG_THUMBNAIL_IMAGE_WIDTH);
                TAG_TRANSFER_FUNCTION = getExifTag(exif, ExifInterface.TAG_TRANSFER_FUNCTION);
                TAG_WHITE_POINT = getExifTag(exif, ExifInterface.TAG_WHITE_POINT);
                TAG_X_RESOLUTION = getExifTag(exif, ExifInterface.TAG_X_RESOLUTION);
                TAG_Y_CB_CR_COEFFICIENTS = getExifTag(exif, ExifInterface.TAG_Y_CB_CR_COEFFICIENTS);
                TAG_Y_CB_CR_POSITIONING = getExifTag(exif, ExifInterface.TAG_Y_CB_CR_POSITIONING);
                TAG_Y_CB_CR_SUB_SAMPLING = getExifTag(exif, ExifInterface.TAG_Y_CB_CR_SUB_SAMPLING);
                TAG_Y_RESOLUTION = getExifTag(exif, ExifInterface.TAG_Y_RESOLUTION);
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                TAG_RW2_ISO = getExifTag(exif, ExifInterface.TAG_RW2_ISO);
                TAG_DEFAULT_CROP_SIZE = getExifTag(exif, ExifInterface.TAG_DEFAULT_CROP_SIZE);
                TAG_DNG_VERSION = getExifTag(exif, ExifInterface.TAG_DNG_VERSION);
                TAG_NEW_SUBFILE_TYPE = getExifTag(exif, ExifInterface.TAG_NEW_SUBFILE_TYPE);
                TAG_ORF_ASPECT_FRAME = getExifTag(exif, ExifInterface.TAG_ORF_ASPECT_FRAME);
                TAG_ORF_PREVIEW_IMAGE_LENGTH = getExifTag(exif, ExifInterface.TAG_ORF_PREVIEW_IMAGE_LENGTH);
                TAG_ORF_PREVIEW_IMAGE_START = getExifTag(exif, ExifInterface.TAG_ORF_PREVIEW_IMAGE_START);
                TAG_ORF_THUMBNAIL_IMAGE = getExifTag(exif, ExifInterface.TAG_ORF_THUMBNAIL_IMAGE);
                TAG_RW2_JPG_FROM_RAW = getExifTag(exif, ExifInterface.TAG_RW2_JPG_FROM_RAW);
                TAG_RW2_SENSOR_BOTTOM_BORDER = getExifTag(exif, ExifInterface.TAG_RW2_SENSOR_BOTTOM_BORDER);
                TAG_RW2_SENSOR_LEFT_BORDER = getExifTag(exif, ExifInterface.TAG_RW2_SENSOR_LEFT_BORDER);
                TAG_RW2_SENSOR_RIGHT_BORDER = getExifTag(exif, ExifInterface.TAG_RW2_SENSOR_RIGHT_BORDER);
                TAG_RW2_SENSOR_TOP_BORDER = getExifTag(exif, ExifInterface.TAG_RW2_SENSOR_TOP_BORDER);
                TAG_SUBFILE_TYPE = getExifTag(exif, ExifInterface.TAG_SUBFILE_TYPE);
            }

            StringBuilder builder = new StringBuilder();

            builder.append("Date & Time: " + TAG_DATETIME + "\n");
            builder.append("TAG_DATETIME_ORIGINAL: " + TAG_DATETIME_ORIGINAL + "\n");
            builder.append("TAG_DATETIME_DIGITIZED: " + TAG_DATETIME_DIGITIZED + "\n");
            builder.append("Flash: " + TAG_FLASH + "\n");
            builder.append("Focal Length: " + TAG_FOCAL_LENGTH + "\n");
            builder.append("GPS Datestamp: " + TAG_FLASH2 + "\n");
            builder.append("GPS Latitude: " + TAG_GPS_LATITUDE + "\n");
            builder.append("GPS Latitude Ref: " + TAG_GPS_LATITUDE_REF + "\n");
            builder.append("GPS Longitude: " + TAG_GPS_LONGITUDE + "\n");
            builder.append("GPS Longitude Ref: " + TAG_GPS_LONGITUDE_REF + "\n");
            builder.append("GPS Processing Method: " + TAG_GPS_PROCESSING_METHOD + "\n");
            builder.append("GPS Timestamp: " + TAG_GPS_TIMESTAMP + "\n");
            builder.append("Image Length: " + TAG_IMAGE_LENGTH + "\n");
            builder.append("Image Width: " + TAG_IMAGE_WIDTH + "\n");
            builder.append("Camera Make: " + TAG_MAKE + "\n");
            builder.append("Camera Model: " + TAG_MODEL + "\n");
            builder.append("Camera Orientation: " + TAG_ORIENTATION + "\n");
            builder.append("Camera White Balance: " + TAG_WHITE_BALANCE + "\n");
            builder.append("TAG_X_RESOLUTION: " + TAG_X_RESOLUTION + "\n");
            builder.append("TAG_Y_RESOLUTION: " + TAG_Y_RESOLUTION + "\n");
            Log.i(TAG, builder.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public boolean save(File filepath) {
        try {
            //Date & Time: 2022:07:08 14:25:06
            SimpleDateFormat timestamp = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
            String currentDateandTime = timestamp.format(new Date());
            ExifInterface exif = new ExifInterface(filepath.getAbsolutePath());
            exif.setAttribute(ExifInterface.TAG_ORIENTATION, TAG_ORIENTATION);
            exif.setAttribute(ExifInterface.TAG_DATETIME, currentDateandTime);
            exif.setAttribute(ExifInterface.TAG_FLASH, TAG_FLASH);
            exif.setAttribute(ExifInterface.TAG_FOCAL_LENGTH, TAG_FOCAL_LENGTH);
            exif.setAttribute(ExifInterface.TAG_FLASH, TAG_FLASH2);
            exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE, TAG_GPS_LATITUDE);
            exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE_REF, TAG_GPS_LATITUDE_REF);
            exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, TAG_GPS_LONGITUDE);
            exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF, TAG_GPS_LONGITUDE_REF);
            exif.setAttribute(ExifInterface.TAG_GPS_PROCESSING_METHOD, TAG_GPS_PROCESSING_METHOD);
            exif.setAttribute(ExifInterface.TAG_GPS_TIMESTAMP, TAG_GPS_TIMESTAMP);
            //exif.setAttribute(ExifInterface.TAG_IMAGE_LENGTH, TAG_IMAGE_LENGTH);
            //exif.setAttribute(ExifInterface.TAG_IMAGE_WIDTH, TAG_IMAGE_WIDTH);
            exif.setAttribute(ExifInterface.TAG_MAKE, TAG_MAKE);
            exif.setAttribute(ExifInterface.TAG_MODEL, TAG_MODEL);
            exif.setAttribute(ExifInterface.TAG_WHITE_BALANCE, TAG_WHITE_BALANCE);
            exif.setAttribute(ExifInterface.TAG_APERTURE, TAG_APERTURE);
            exif.setAttribute(ExifInterface.TAG_ISO, TAG_ISO);
            exif.setAttribute(ExifInterface.TAG_MAKE, TAG_MAKE);
            exif.setAttribute(ExifInterface.TAG_MODEL, TAG_MODEL);
            exif.setAttribute(ExifInterface.TAG_WHITE_BALANCE, TAG_WHITE_BALANCE);
            exif.setAttribute(ExifInterface.TAG_EXPOSURE_TIME, TAG_EXPOSURE_TIME);
            exif.setAttribute(ExifInterface.TAG_GPS_ALTITUDE, TAG_GPS_ALTITUDE);
            exif.setAttribute(ExifInterface.TAG_GPS_ALTITUDE_REF, TAG_GPS_ALTITUDE_REF);
            exif.setAttribute(ExifInterface.TAG_GPS_DATESTAMP, currentDateandTime);

//            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            exif.setAttribute(ExifInterface.TAG_SHUTTER_SPEED_VALUE, TAG_SHUTTER_SPEED_VALUE);
            exif.setAttribute(ExifInterface.TAG_METERING_MODE, TAG_METERING_MODE);
            exif.setAttribute(ExifInterface.TAG_ISO_SPEED_RATINGS, TAG_ISO_SPEED_RATINGS);
            exif.setAttribute(ExifInterface.TAG_COPYRIGHT, TAG_COPYRIGHT);
            exif.setAttribute(ExifInterface.TAG_SUBSEC_TIME, TAG_SUBSEC_TIME);
            exif.setAttribute(ExifInterface.TAG_APERTURE_VALUE, TAG_APERTURE_VALUE);
            exif.setAttribute(ExifInterface.TAG_SATURATION, TAG_SATURATION);
            exif.setAttribute(ExifInterface.TAG_SHARPNESS, TAG_SHARPNESS);
            exif.setAttribute(ExifInterface.TAG_CONTRAST, TAG_CONTRAST);
            exif.setAttribute(ExifInterface.TAG_USER_COMMENT, TAG_USER_COMMENT);
            exif.setAttribute(ExifInterface.TAG_ARTIST, TAG_ARTIST);
            exif.setAttribute(ExifInterface.TAG_BITS_PER_SAMPLE, TAG_BITS_PER_SAMPLE);
            exif.setAttribute(ExifInterface.TAG_BRIGHTNESS_VALUE, TAG_BRIGHTNESS_VALUE);
            exif.setAttribute(ExifInterface.TAG_CFA_PATTERN, TAG_CFA_PATTERN);
            exif.setAttribute(ExifInterface.TAG_COLOR_SPACE, TAG_COLOR_SPACE);
            exif.setAttribute(ExifInterface.TAG_COMPONENTS_CONFIGURATION, TAG_COMPONENTS_CONFIGURATION);
            exif.setAttribute(ExifInterface.TAG_COMPRESSED_BITS_PER_PIXEL, TAG_COMPRESSED_BITS_PER_PIXEL);
            exif.setAttribute(ExifInterface.TAG_COMPRESSION, TAG_COMPRESSION);
            exif.setAttribute(ExifInterface.TAG_CUSTOM_RENDERED, TAG_CUSTOM_RENDERED);
            exif.setAttribute(ExifInterface.TAG_DATETIME_DIGITIZED, currentDateandTime); //TAG_DATETIME_DIGITIZED
            exif.setAttribute(ExifInterface.TAG_DATETIME_ORIGINAL, currentDateandTime);//TAG_DATETIME_ORIGINAL
            exif.setAttribute(ExifInterface.TAG_DATETIME, currentDateandTime);
            exif.setAttribute(ExifInterface.TAG_DEVICE_SETTING_DESCRIPTION, TAG_DEVICE_SETTING_DESCRIPTION);
            exif.setAttribute(ExifInterface.TAG_DIGITAL_ZOOM_RATIO, TAG_DIGITAL_ZOOM_RATIO);
            exif.setAttribute(ExifInterface.TAG_EXIF_VERSION, TAG_EXIF_VERSION);
            exif.setAttribute(ExifInterface.TAG_EXPOSURE_BIAS_VALUE, TAG_EXPOSURE_BIAS_VALUE);
            exif.setAttribute(ExifInterface.TAG_EXPOSURE_INDEX, TAG_EXPOSURE_INDEX);
            exif.setAttribute(ExifInterface.TAG_EXPOSURE_MODE, TAG_EXPOSURE_MODE);
            exif.setAttribute(ExifInterface.TAG_EXPOSURE_PROGRAM, TAG_EXPOSURE_PROGRAM);
            exif.setAttribute(ExifInterface.TAG_FILE_SOURCE, TAG_FILE_SOURCE);
            exif.setAttribute(ExifInterface.TAG_FLASHPIX_VERSION, TAG_FLASHPIX_VERSION);
            exif.setAttribute(ExifInterface.TAG_FLASH_ENERGY, TAG_FLASH_ENERGY);
            exif.setAttribute(ExifInterface.TAG_F_NUMBER, TAG_F_NUMBER);
            exif.setAttribute(ExifInterface.TAG_FOCAL_LENGTH_IN_35MM_FILM, TAG_FOCAL_LENGTH_IN_35MM_FILM);
            exif.setAttribute(ExifInterface.TAG_FOCAL_PLANE_RESOLUTION_UNIT, TAG_FOCAL_PLANE_RESOLUTION_UNIT);
            exif.setAttribute(ExifInterface.TAG_FOCAL_PLANE_X_RESOLUTION, TAG_FOCAL_PLANE_X_RESOLUTION);
            exif.setAttribute(ExifInterface.TAG_FOCAL_PLANE_Y_RESOLUTION, TAG_FOCAL_PLANE_Y_RESOLUTION);
            exif.setAttribute(ExifInterface.TAG_GAIN_CONTROL, TAG_GAIN_CONTROL);
            exif.setAttribute(ExifInterface.TAG_GPS_AREA_INFORMATION, TAG_GPS_AREA_INFORMATION);
            exif.setAttribute(ExifInterface.TAG_GPS_DEST_BEARING, TAG_GPS_DEST_BEARING);
            exif.setAttribute(ExifInterface.TAG_GPS_DEST_BEARING_REF, TAG_GPS_DEST_BEARING_REF);
            exif.setAttribute(ExifInterface.TAG_GPS_DEST_DISTANCE_REF, TAG_GPS_DEST_DISTANCE_REF);
            exif.setAttribute(ExifInterface.TAG_GPS_DEST_LATITUDE, TAG_GPS_DEST_LATITUDE);
            exif.setAttribute(ExifInterface.TAG_GPS_DEST_LATITUDE_REF, TAG_GPS_DEST_LATITUDE_REF);
            exif.setAttribute(ExifInterface.TAG_GPS_DEST_LONGITUDE, TAG_GPS_DEST_LONGITUDE);
            exif.setAttribute(ExifInterface.TAG_GPS_DEST_LONGITUDE_REF, TAG_GPS_DEST_LONGITUDE_REF);
            exif.setAttribute(ExifInterface.TAG_GPS_DIFFERENTIAL, TAG_GPS_DIFFERENTIAL);
            exif.setAttribute(ExifInterface.TAG_GPS_DOP, TAG_GPS_DOP);
            exif.setAttribute(ExifInterface.TAG_GPS_IMG_DIRECTION, TAG_GPS_IMG_DIRECTION);
            exif.setAttribute(ExifInterface.TAG_GPS_IMG_DIRECTION_REF, TAG_GPS_IMG_DIRECTION_REF);
            exif.setAttribute(ExifInterface.TAG_GPS_MAP_DATUM, TAG_GPS_MAP_DATUM);
            exif.setAttribute(ExifInterface.TAG_GPS_MEASURE_MODE, TAG_GPS_MEASURE_MODE);
            exif.setAttribute(ExifInterface.TAG_GPS_SATELLITES, TAG_GPS_SATELLITES);
            exif.setAttribute(ExifInterface.TAG_GPS_SPEED, TAG_GPS_SPEED);
            exif.setAttribute(ExifInterface.TAG_GPS_SPEED_REF, TAG_GPS_SPEED_REF);
            exif.setAttribute(ExifInterface.TAG_GPS_STATUS, TAG_GPS_STATUS);
            exif.setAttribute(ExifInterface.TAG_GPS_TRACK, TAG_GPS_TRACK);
            exif.setAttribute(ExifInterface.TAG_GPS_TRACK_REF, TAG_GPS_TRACK_REF);
            exif.setAttribute(ExifInterface.TAG_GPS_VERSION_ID, TAG_GPS_VERSION_ID);
            exif.setAttribute(ExifInterface.TAG_IMAGE_DESCRIPTION, "Made with LogoLicious Add Your Logo App"); //TAG_IMAGE_DESCRIPTION
            exif.setAttribute(ExifInterface.TAG_IMAGE_UNIQUE_ID, TAG_IMAGE_UNIQUE_ID);
            exif.setAttribute(ExifInterface.TAG_INTEROPERABILITY_INDEX, TAG_INTEROPERABILITY_INDEX);
            exif.setAttribute(ExifInterface.TAG_JPEG_INTERCHANGE_FORMAT, TAG_JPEG_INTERCHANGE_FORMAT);
            exif.setAttribute(ExifInterface.TAG_JPEG_INTERCHANGE_FORMAT_LENGTH, TAG_JPEG_INTERCHANGE_FORMAT_LENGTH);
            exif.setAttribute(ExifInterface.TAG_LIGHT_SOURCE, TAG_LIGHT_SOURCE);
            exif.setAttribute(ExifInterface.TAG_MAKER_NOTE, TAG_MAKER_NOTE);
            exif.setAttribute(ExifInterface.TAG_MAX_APERTURE_VALUE, TAG_MAX_APERTURE_VALUE);
            exif.setAttribute(ExifInterface.TAG_OECF, TAG_OECF);
            exif.setAttribute(ExifInterface.TAG_PHOTOMETRIC_INTERPRETATION, TAG_PHOTOMETRIC_INTERPRETATION);
            exif.setAttribute(ExifInterface.TAG_PIXEL_X_DIMENSION, TAG_PIXEL_X_DIMENSION);
            exif.setAttribute(ExifInterface.TAG_PIXEL_Y_DIMENSION, TAG_PIXEL_Y_DIMENSION);
            exif.setAttribute(ExifInterface.TAG_PLANAR_CONFIGURATION, TAG_PLANAR_CONFIGURATION);
            exif.setAttribute(ExifInterface.TAG_PRIMARY_CHROMATICITIES, TAG_PRIMARY_CHROMATICITIES);
            exif.setAttribute(ExifInterface.TAG_REFERENCE_BLACK_WHITE, TAG_REFERENCE_BLACK_WHITE);
            exif.setAttribute(ExifInterface.TAG_RELATED_SOUND_FILE, TAG_RELATED_SOUND_FILE);
            exif.setAttribute(ExifInterface.TAG_RESOLUTION_UNIT, TAG_RESOLUTION_UNIT);
            exif.setAttribute(ExifInterface.TAG_ROWS_PER_STRIP, TAG_ROWS_PER_STRIP);
            exif.setAttribute(ExifInterface.TAG_SAMPLES_PER_PIXEL, TAG_SAMPLES_PER_PIXEL);
            exif.setAttribute(ExifInterface.TAG_SCENE_CAPTURE_TYPE, TAG_SCENE_CAPTURE_TYPE);
            exif.setAttribute(ExifInterface.TAG_SCENE_TYPE, TAG_SCENE_TYPE);
            exif.setAttribute(ExifInterface.TAG_SENSING_METHOD, TAG_SENSING_METHOD);
            exif.setAttribute(ExifInterface.TAG_SOFTWARE, TAG_SOFTWARE);
            exif.setAttribute(ExifInterface.TAG_SPATIAL_FREQUENCY_RESPONSE, TAG_SPATIAL_FREQUENCY_RESPONSE);
            exif.setAttribute(ExifInterface.TAG_SPECTRAL_SENSITIVITY, TAG_SPECTRAL_SENSITIVITY);
            exif.setAttribute(ExifInterface.TAG_STRIP_BYTE_COUNTS, TAG_STRIP_BYTE_COUNTS);
            exif.setAttribute(ExifInterface.TAG_STRIP_OFFSETS, TAG_STRIP_OFFSETS);
            exif.setAttribute(ExifInterface.TAG_SUBJECT_AREA, TAG_SUBJECT_AREA);
            exif.setAttribute(ExifInterface.TAG_SUBJECT_DISTANCE, TAG_SUBJECT_DISTANCE);
            exif.setAttribute(ExifInterface.TAG_SUBJECT_DISTANCE_RANGE, TAG_SUBJECT_DISTANCE_RANGE);
            exif.setAttribute(ExifInterface.TAG_SUBJECT_LOCATION, TAG_SUBJECT_LOCATION);
            exif.setAttribute(ExifInterface.TAG_SUBSEC_TIME, TAG_SUBSEC_TIME);
            exif.setAttribute(ExifInterface.TAG_SUBSEC_TIME_DIGITIZED, TAG_SUBSEC_TIME_DIGITIZED);
            exif.setAttribute(ExifInterface.TAG_SUBSEC_TIME_ORIGINAL, TAG_SUBSEC_TIME_ORIGINAL);
            exif.setAttribute(ExifInterface.TAG_THUMBNAIL_IMAGE_LENGTH, TAG_THUMBNAIL_IMAGE_LENGTH);
            exif.setAttribute(ExifInterface.TAG_THUMBNAIL_IMAGE_WIDTH, TAG_THUMBNAIL_IMAGE_WIDTH);
            exif.setAttribute(ExifInterface.TAG_TRANSFER_FUNCTION, TAG_TRANSFER_FUNCTION);
            exif.setAttribute(ExifInterface.TAG_WHITE_POINT, TAG_WHITE_POINT);
            exif.setAttribute(ExifInterface.TAG_X_RESOLUTION, TAG_X_RESOLUTION);
            exif.setAttribute(ExifInterface.TAG_Y_CB_CR_COEFFICIENTS, TAG_Y_CB_CR_COEFFICIENTS);
            exif.setAttribute(ExifInterface.TAG_Y_CB_CR_POSITIONING, TAG_Y_CB_CR_POSITIONING);
            exif.setAttribute(ExifInterface.TAG_Y_CB_CR_SUB_SAMPLING, TAG_Y_CB_CR_SUB_SAMPLING);
            exif.setAttribute(ExifInterface.TAG_Y_RESOLUTION, TAG_Y_RESOLUTION);
//            }

//        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            exif.setAttribute(ExifInterface.TAG_RW2_ISO, TAG_RW2_ISO);
            exif.setAttribute(ExifInterface.TAG_DEFAULT_CROP_SIZE, TAG_DEFAULT_CROP_SIZE);
            exif.setAttribute(ExifInterface.TAG_DNG_VERSION, TAG_DNG_VERSION);
            exif.setAttribute(ExifInterface.TAG_NEW_SUBFILE_TYPE, TAG_NEW_SUBFILE_TYPE);
            exif.setAttribute(ExifInterface.TAG_ORF_ASPECT_FRAME, TAG_ORF_ASPECT_FRAME);
            exif.setAttribute(ExifInterface.TAG_ORF_PREVIEW_IMAGE_LENGTH, TAG_ORF_PREVIEW_IMAGE_LENGTH);
            exif.setAttribute(ExifInterface.TAG_ORF_PREVIEW_IMAGE_START, TAG_ORF_PREVIEW_IMAGE_START);
            exif.setAttribute(ExifInterface.TAG_ORF_THUMBNAIL_IMAGE, TAG_ORF_THUMBNAIL_IMAGE);
            exif.setAttribute(ExifInterface.TAG_RW2_JPG_FROM_RAW, TAG_RW2_JPG_FROM_RAW);
            exif.setAttribute(ExifInterface.TAG_RW2_SENSOR_BOTTOM_BORDER, TAG_RW2_SENSOR_BOTTOM_BORDER);
            exif.setAttribute(ExifInterface.TAG_RW2_SENSOR_LEFT_BORDER, TAG_RW2_SENSOR_LEFT_BORDER);
            exif.setAttribute(ExifInterface.TAG_RW2_SENSOR_RIGHT_BORDER, TAG_RW2_SENSOR_RIGHT_BORDER);
            exif.setAttribute(ExifInterface.TAG_RW2_SENSOR_TOP_BORDER, TAG_RW2_SENSOR_TOP_BORDER);
            exif.setAttribute(ExifInterface.TAG_SUBFILE_TYPE, TAG_SUBFILE_TYPE);
//        }

            exif.saveAttributes();
        } catch (IOException e) {
            String error = e.getMessage();
            Log.d(TAG, error);
            return false;
        }
        return true;
    }

    private String getExifTag(ExifInterface exif, String tag) {
        String attribute = exif.getAttribute(tag);

        return (null != attribute ? attribute : "");
    }

    public void copyExif(String originalPath, String newPath) throws IOException {

        List<String> arrayListExifTags = new ArrayList<String>();
        arrayListExifTags.add(ExifInterface.TAG_ORIENTATION);
        arrayListExifTags.add(ExifInterface.TAG_APERTURE);
        arrayListExifTags.add(ExifInterface.TAG_FOCAL_LENGTH);
        arrayListExifTags.add(ExifInterface.TAG_ISO);
        arrayListExifTags.add(ExifInterface.TAG_DATETIME);
        arrayListExifTags.add(ExifInterface.TAG_FLASH);
        arrayListExifTags.add(ExifInterface.TAG_WHITE_BALANCE);
        arrayListExifTags.add(ExifInterface.TAG_MAKE);
        arrayListExifTags.add(ExifInterface.TAG_MODEL);
        arrayListExifTags.add(ExifInterface.TAG_WHITE_BALANCE);
        arrayListExifTags.add(ExifInterface.TAG_EXPOSURE_TIME);
        arrayListExifTags.add(ExifInterface.TAG_GPS_ALTITUDE);
        arrayListExifTags.add(ExifInterface.TAG_GPS_ALTITUDE_REF);
        arrayListExifTags.add(ExifInterface.TAG_GPS_DATESTAMP);
        arrayListExifTags.add(ExifInterface.TAG_GPS_LATITUDE);
        arrayListExifTags.add(ExifInterface.TAG_GPS_LATITUDE_REF);
        arrayListExifTags.add(ExifInterface.TAG_GPS_LONGITUDE);
        arrayListExifTags.add(ExifInterface.TAG_GPS_LONGITUDE_REF);
        arrayListExifTags.add(ExifInterface.TAG_GPS_PROCESSING_METHOD);
        arrayListExifTags.add(ExifInterface.TAG_GPS_TIMESTAMP);
        //arrayListExifTags.add(ExifInterface.TAG_IMAGE_LENGTH);
        //arrayListExifTags.add(ExifInterface.TAG_IMAGE_WIDTH);

        arrayListExifTags.add(ExifInterface.TAG_SHUTTER_SPEED_VALUE);


//        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        arrayListExifTags.add(ExifInterface.TAG_SHUTTER_SPEED_VALUE);
        arrayListExifTags.add(ExifInterface.TAG_METERING_MODE);
        arrayListExifTags.add(ExifInterface.TAG_ISO_SPEED_RATINGS);
        arrayListExifTags.add(ExifInterface.TAG_COPYRIGHT);
        arrayListExifTags.add(ExifInterface.TAG_SUBSEC_TIME);
        arrayListExifTags.add(ExifInterface.TAG_APERTURE_VALUE);
        arrayListExifTags.add(ExifInterface.TAG_SATURATION);
        arrayListExifTags.add(ExifInterface.TAG_SHARPNESS);
        arrayListExifTags.add(ExifInterface.TAG_CONTRAST);
        arrayListExifTags.add(ExifInterface.TAG_USER_COMMENT);
        arrayListExifTags.add(ExifInterface.TAG_ARTIST);
        arrayListExifTags.add(ExifInterface.TAG_BITS_PER_SAMPLE);
        arrayListExifTags.add(ExifInterface.TAG_BRIGHTNESS_VALUE);
        arrayListExifTags.add(ExifInterface.TAG_CFA_PATTERN);
        arrayListExifTags.add(ExifInterface.TAG_COLOR_SPACE);
        arrayListExifTags.add(ExifInterface.TAG_COMPONENTS_CONFIGURATION);
        arrayListExifTags.add(ExifInterface.TAG_COMPRESSED_BITS_PER_PIXEL);
        arrayListExifTags.add(ExifInterface.TAG_COMPRESSION);
        arrayListExifTags.add(ExifInterface.TAG_CUSTOM_RENDERED);
        arrayListExifTags.add(ExifInterface.TAG_DATETIME_DIGITIZED);
        arrayListExifTags.add(ExifInterface.TAG_DATETIME_ORIGINAL);
        arrayListExifTags.add(ExifInterface.TAG_DEVICE_SETTING_DESCRIPTION);
        arrayListExifTags.add(ExifInterface.TAG_DIGITAL_ZOOM_RATIO);
        arrayListExifTags.add(ExifInterface.TAG_EXIF_VERSION);
        arrayListExifTags.add(ExifInterface.TAG_EXPOSURE_BIAS_VALUE);
        arrayListExifTags.add(ExifInterface.TAG_EXPOSURE_INDEX);
        arrayListExifTags.add(ExifInterface.TAG_EXPOSURE_MODE);
        arrayListExifTags.add(ExifInterface.TAG_EXPOSURE_PROGRAM);
        arrayListExifTags.add(ExifInterface.TAG_FILE_SOURCE);
        arrayListExifTags.add(ExifInterface.TAG_FLASHPIX_VERSION);
        arrayListExifTags.add(ExifInterface.TAG_FLASH_ENERGY);
        arrayListExifTags.add(ExifInterface.TAG_F_NUMBER);
        arrayListExifTags.add(ExifInterface.TAG_FOCAL_LENGTH_IN_35MM_FILM);
        arrayListExifTags.add(ExifInterface.TAG_FOCAL_PLANE_RESOLUTION_UNIT);
        arrayListExifTags.add(ExifInterface.TAG_FOCAL_PLANE_X_RESOLUTION);
        arrayListExifTags.add(ExifInterface.TAG_FOCAL_PLANE_Y_RESOLUTION);
        arrayListExifTags.add(ExifInterface.TAG_GAIN_CONTROL);
        arrayListExifTags.add(ExifInterface.TAG_GPS_AREA_INFORMATION);
        arrayListExifTags.add(ExifInterface.TAG_GPS_DEST_BEARING);
        arrayListExifTags.add(ExifInterface.TAG_GPS_DEST_BEARING_REF);
        arrayListExifTags.add(ExifInterface.TAG_GPS_DEST_BEARING_REF);
        arrayListExifTags.add(ExifInterface.TAG_GPS_DEST_DISTANCE_REF);
        arrayListExifTags.add(ExifInterface.TAG_GPS_DEST_LATITUDE);
        arrayListExifTags.add(ExifInterface.TAG_GPS_DEST_LATITUDE_REF);
        arrayListExifTags.add(ExifInterface.TAG_GPS_DEST_LONGITUDE);
        arrayListExifTags.add(ExifInterface.TAG_GPS_DEST_LONGITUDE_REF);
        arrayListExifTags.add(ExifInterface.TAG_GPS_DIFFERENTIAL);
        arrayListExifTags.add(ExifInterface.TAG_GPS_DOP);
        arrayListExifTags.add(ExifInterface.TAG_GPS_IMG_DIRECTION);
        arrayListExifTags.add(ExifInterface.TAG_GPS_IMG_DIRECTION_REF);
        arrayListExifTags.add(ExifInterface.TAG_GPS_MAP_DATUM);
        arrayListExifTags.add(ExifInterface.TAG_GPS_MEASURE_MODE);
        arrayListExifTags.add(ExifInterface.TAG_GPS_SATELLITES);
        arrayListExifTags.add(ExifInterface.TAG_GPS_SPEED);
        arrayListExifTags.add(ExifInterface.TAG_GPS_SPEED_REF);
        arrayListExifTags.add(ExifInterface.TAG_GPS_STATUS);
        arrayListExifTags.add(ExifInterface.TAG_GPS_TRACK);
        arrayListExifTags.add(ExifInterface.TAG_GPS_TRACK_REF);
        arrayListExifTags.add(ExifInterface.TAG_GPS_VERSION_ID);
        arrayListExifTags.add(ExifInterface.TAG_IMAGE_DESCRIPTION);
        arrayListExifTags.add(ExifInterface.TAG_IMAGE_UNIQUE_ID);
        arrayListExifTags.add(ExifInterface.TAG_INTEROPERABILITY_INDEX);
        arrayListExifTags.add(ExifInterface.TAG_JPEG_INTERCHANGE_FORMAT);
        arrayListExifTags.add(ExifInterface.TAG_JPEG_INTERCHANGE_FORMAT_LENGTH);
        arrayListExifTags.add(ExifInterface.TAG_LIGHT_SOURCE);
        arrayListExifTags.add(ExifInterface.TAG_MAKER_NOTE);
        arrayListExifTags.add(ExifInterface.TAG_MAX_APERTURE_VALUE);
        arrayListExifTags.add(ExifInterface.TAG_OECF);
        arrayListExifTags.add(ExifInterface.TAG_PHOTOMETRIC_INTERPRETATION);
        arrayListExifTags.add(ExifInterface.TAG_PIXEL_X_DIMENSION);
        arrayListExifTags.add(ExifInterface.TAG_PIXEL_Y_DIMENSION);
        arrayListExifTags.add(ExifInterface.TAG_PLANAR_CONFIGURATION);
        arrayListExifTags.add(ExifInterface.TAG_PRIMARY_CHROMATICITIES);
        arrayListExifTags.add(ExifInterface.TAG_REFERENCE_BLACK_WHITE);
        arrayListExifTags.add(ExifInterface.TAG_RELATED_SOUND_FILE);
        arrayListExifTags.add(ExifInterface.TAG_RESOLUTION_UNIT);
        arrayListExifTags.add(ExifInterface.TAG_ROWS_PER_STRIP);
        arrayListExifTags.add(ExifInterface.TAG_SAMPLES_PER_PIXEL);
        arrayListExifTags.add(ExifInterface.TAG_SCENE_CAPTURE_TYPE);
        arrayListExifTags.add(ExifInterface.TAG_SCENE_TYPE);
        arrayListExifTags.add(ExifInterface.TAG_SENSING_METHOD);
        arrayListExifTags.add(ExifInterface.TAG_SOFTWARE);
        arrayListExifTags.add(ExifInterface.TAG_SPATIAL_FREQUENCY_RESPONSE);
        arrayListExifTags.add(ExifInterface.TAG_SPECTRAL_SENSITIVITY);
        arrayListExifTags.add(ExifInterface.TAG_STRIP_BYTE_COUNTS);
        arrayListExifTags.add(ExifInterface.TAG_STRIP_OFFSETS);
        arrayListExifTags.add(ExifInterface.TAG_SUBJECT_AREA);
        arrayListExifTags.add(ExifInterface.TAG_SUBJECT_DISTANCE);
        arrayListExifTags.add(ExifInterface.TAG_SUBJECT_DISTANCE_RANGE);
        arrayListExifTags.add(ExifInterface.TAG_SUBJECT_LOCATION);
        arrayListExifTags.add(ExifInterface.TAG_SUBSEC_TIME);
        arrayListExifTags.add(ExifInterface.TAG_SUBSEC_TIME_DIGITIZED);
        arrayListExifTags.add(ExifInterface.TAG_SUBSEC_TIME_ORIGINAL);
        arrayListExifTags.add(ExifInterface.TAG_THUMBNAIL_IMAGE_LENGTH);
        arrayListExifTags.add(ExifInterface.TAG_THUMBNAIL_IMAGE_WIDTH);
        arrayListExifTags.add(ExifInterface.TAG_TRANSFER_FUNCTION);
        arrayListExifTags.add(ExifInterface.TAG_WHITE_POINT);
        arrayListExifTags.add(ExifInterface.TAG_X_RESOLUTION);
        arrayListExifTags.add(ExifInterface.TAG_Y_CB_CR_COEFFICIENTS);
        arrayListExifTags.add(ExifInterface.TAG_Y_CB_CR_POSITIONING);
        arrayListExifTags.add(ExifInterface.TAG_Y_CB_CR_SUB_SAMPLING);
        arrayListExifTags.add(ExifInterface.TAG_Y_RESOLUTION);
//        }

//        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        arrayListExifTags.add(ExifInterface.TAG_RW2_ISO);
        arrayListExifTags.add(ExifInterface.TAG_DEFAULT_CROP_SIZE);
        arrayListExifTags.add(ExifInterface.TAG_DNG_VERSION);
        arrayListExifTags.add(ExifInterface.TAG_NEW_SUBFILE_TYPE);
        arrayListExifTags.add(ExifInterface.TAG_ORF_ASPECT_FRAME);
        arrayListExifTags.add(ExifInterface.TAG_ORF_PREVIEW_IMAGE_LENGTH);
        arrayListExifTags.add(ExifInterface.TAG_ORF_PREVIEW_IMAGE_START);
        arrayListExifTags.add(ExifInterface.TAG_ORF_THUMBNAIL_IMAGE);
        arrayListExifTags.add(ExifInterface.TAG_RW2_JPG_FROM_RAW);
        arrayListExifTags.add(ExifInterface.TAG_RW2_SENSOR_BOTTOM_BORDER);
        arrayListExifTags.add(ExifInterface.TAG_RW2_SENSOR_LEFT_BORDER);
        arrayListExifTags.add(ExifInterface.TAG_RW2_SENSOR_RIGHT_BORDER);
        arrayListExifTags.add(ExifInterface.TAG_RW2_SENSOR_TOP_BORDER);
        arrayListExifTags.add(ExifInterface.TAG_SUBFILE_TYPE);
//        }

        String[] attributes = arrayListExifTags.toArray(new String[]{});

        ExifInterface oldExif = new ExifInterface(originalPath);
        ExifInterface newExif = new ExifInterface(newPath);

        if (attributes.length > 0) {
            for (int i = 0; i < attributes.length; i++) {
                String value = oldExif.getAttribute(attributes[i]);
                if (value != null)
                    newExif.setAttribute(attributes[i], value);
            }
            newExif.saveAttributes();
        }
    }

    public static void updateExif(String exifToBeUpdate, String newValue, String imgPath) throws IOException {
        ExifInterface newExif = new ExifInterface(imgPath);
        newExif.setAttribute(exifToBeUpdate, newValue);
        newExif.saveAttributes();
    }

    private String getTempFilePath(String filename) {
        String temp = "_temp";
        int dot = filename.lastIndexOf(".");
        String ext = filename.substring(dot + 1);

        if (dot == -1 || !ext.matches("\\w+")) {
            filename += temp;
        } else {
            filename = filename.substring(0, dot) + temp + "." + ext;
        }

        return filename;
    }

    public byte[] readFile(File file) throws IOException {
        // Open file
        RandomAccessFile f = new RandomAccessFile(file, "r");
        try {
            // Get and check length
            long longlength = f.length();
            int length = (int) longlength;
            if (length != longlength)
                throw new IOException("File size >= 2 GB");
            // Read file and return data
            byte[] data = new byte[length];
            f.readFully(data);
            return data;
        } finally {
            f.close();
        }
    }

}