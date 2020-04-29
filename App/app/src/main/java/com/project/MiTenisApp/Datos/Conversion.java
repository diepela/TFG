package com.project.MiTenisApp.Datos;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class Conversion {

    /**
     * @param value array de bytes con los datos recibidos
     * @return entero con el tiempo in milliseconds
     */
    public static Integer convertTs(byte[] value) {

        //TIMESTAP  uint16
        int ts = ByteBuffer.wrap(value, 0, 2).order(ByteOrder.LITTLE_ENDIAN).getShort() & 0xFFFF;
        System.out.println("\n TIMESTAMP:" + ts);

        return (ts);
    }

    /**
     * @param value array de bytes con los datos recibidos
     * @return array de reales con los valores de aceleración en g los 3 ejes
     */
    public static Double[] convertACC(byte[] value) {

        //ACCELERATION  int16
        Double[] ACC = new Double[3];
        //X axis
        ACC[0] = (int) ByteBuffer.wrap(value, 2, 2).order(ByteOrder.LITTLE_ENDIAN).getShort() / 1000.0;

        //Y axis
        ACC[1] = (int) ByteBuffer.wrap(value, 4, 2).order(ByteOrder.LITTLE_ENDIAN).getShort() / 1000.0;

        //Z axis
        ACC[2] = (int) ByteBuffer.wrap(value, 6, 2).order(ByteOrder.LITTLE_ENDIAN).getShort() / 1000.0;

        System.out.println("\n ACELERACIÓN (g): [ " + ACC[0] + " , " + ACC[1] + " , " + ACC[2] + "]");

        return (ACC);
    }

    /**
     * @param value array de bytes con los datos recibidos
     * @return array de reales con los valores de giro en rad/s los 3 ejes
     */
    public static Double[] convertGYR(byte[] value) {

        //GYROSCOPE   int16
        Double[] GYR = new Double[3];

        //X axis
        GYR[0] = ByteBuffer.wrap(value, 8, 2).order(ByteOrder.LITTLE_ENDIAN).getShort() / 10.0;

        //Y axis
        GYR[1] = ByteBuffer.wrap(value, 10, 2).order(ByteOrder.LITTLE_ENDIAN).getShort() / 10.0;

        //Z axis
        GYR[2] = ByteBuffer.wrap(value, 12, 2).order(ByteOrder.LITTLE_ENDIAN).getShort() / 10.0;

        System.out.println("\n GIRÓSCOPO (rad/s): [ " + GYR[0] + " , " + GYR[1] + " , " + GYR[2] + "]");

        return new Double[] {toRadians(GYR[0]), toRadians(GYR[1]), toRadians(GYR[2])};
    }

    /**
     * @param value array de bytes con los datos recibidos
     * @return array of integers with the interpreted magnetometer values in Ga in the three axis
     */
    public static Double[] convertMAG(byte[] value) {

        //MAGNETOMETER   int16
        Double[] MAG = new Double[3];

        //X axis
        MAG[0] = (int) ByteBuffer.wrap(value, 14, 2).order(ByteOrder.LITTLE_ENDIAN).getShort() / 1000.0;

        //Y axis
        MAG[1] = (int) ByteBuffer.wrap(value, 16, 2).order(ByteOrder.LITTLE_ENDIAN).getShort() / 1000.0;

        //Z axis
        MAG[2] = (int) ByteBuffer.wrap(value, 18, 2).order(ByteOrder.LITTLE_ENDIAN).getShort() / 1000.0;

        System.out.println("\n MAGNETÓMETRO (Ga):  [ " + MAG[0] + " , " + MAG[1] + " , " + MAG[2] + "]");

        return (MAG);
    }


    /**
     Convertir de grados a radianes
     @param deg ángulo en grados
     @return rad ángulo en radianes
     */
    public static double toRadians(double deg) {
        return deg*(Math.PI/180);
    }



}


