package com.shabaton.gdejeproblem;

/**
 * Created by david on 16.12.2017.
 */

public class Alati {

    public static String Lat(String cir)
    {
        String lat = cir;

        String[] latinica = { "A", "B", "V", "G", "D", "Đ", "E", "Dž", "Ž", "Z", "I", "K", "Lj", "L", "M", "Nj", "J", "N", "O", "P", "R", "S", "T", "Ć", "U", "F", "H", "C", "Č", "Š", "a", "b", "v", "g", "d", "đ", "e", "dž", "ž", "z", "i", "k", "lj", "l", "m", "nj", "j", "n", "o", "p", "r", "s", "t", "ć", "u", "f", "h", "c", "č", "š" };
        String[] cirilica = { "А", "Б", "В", "Г", "Д", "Ђ", "Е", "Џ", "Ж", "З", "И", "К", "Љ", "Л", "М", "Њ", "Ј", "Н", "О", "П", "Р", "С", "Т", "Ћ", "У", "Ф", "Х", "Ц", "Ч", "Ш", "а", "б", "в", "г", "д", "ђ", "е", "џ", "ж", "з", "и", "к", "љ", "л", "м", "њ", "ј", "н", "о", "п", "р", "с", "т", "ћ", "у", "ф", "х", "ц", "ч", "ш" };

        for (int i = 0; i < 60; i++) lat = lat.replace(cirilica[i], latinica[i]);

        return lat;

    }
}
