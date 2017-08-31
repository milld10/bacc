package com.example.camilla.androidcredentialstore.tools;

import android.support.design.widget.TextInputEditText;
import com.example.camilla.androidcredentialstore.models.Account;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;


public class Converter {

    /*
    private static final char[] HEX_DIGITS = "0123456789abcdef".toCharArray();

    private static char[] toHex(byte[] bytes)
    {
        char[] c = new char[bytes.length*2];
        int index = 0;
        for (byte b : bytes)
        {
            c[index++] = HEX_DIGITS[(b >> 4) & 0xf];
            c[index++] = HEX_DIGITS[b & 0xf];
        }
        return c;
    }*/


    /**
     * converts char[] to byte[], for pw typed in by user. byte[] is needed to
     * store into DB
     * @return
     */
    public static byte[] charToByte(TextInputEditText pw)
    {
        int length = pw.length();
        char[] charArray = new char[length];
        pw.getText().getChars(0, length, charArray, 0);

        ByteBuffer buffer = StandardCharsets.UTF_8.encode(CharBuffer.wrap(charArray));
        byte[] byteArray = new byte[buffer.limit()];
        buffer.get(byteArray);

        return byteArray;
    }


    public static char[] byteToChar(Account account)
    {
        byte[] byteArray = account.getPassword();

        CharBuffer buffer = StandardCharsets.UTF_8.decode(ByteBuffer.wrap(byteArray));
        char[] charArray = new char[buffer.limit()];
        buffer.get(charArray);

        return charArray;
    }
}
