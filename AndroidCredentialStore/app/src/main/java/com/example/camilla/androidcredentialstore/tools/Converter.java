package com.example.camilla.androidcredentialstore.tools;

import android.widget.EditText;

import com.example.camilla.androidcredentialstore.models.Account;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;

/**
 * Created by Camilla on 12.08.2017.
 */

public class Converter {

    /**
     * converts char[] to byte[], for pw typed in by user. byte[] is needed to
     * store into DB
     * @return
     */
    public static byte[] charToByte(EditText pw)
    {

        /* Code used before in AddCredActivity:
        //uses a toString -> not for passwords!
        //byte[] pwArray = password.getText().toString().getBytes(StandardCharsets.UTF_8);
        int length = password.length();
        char[] pwArray = new char[length];
        password.getText().getChars(0, length, pwArray, 0);

        //****
        //casted to byte array?
        //byte[] _pwArray = Charset.forName("UTF-8").encode(CharBuffer.wrap(pwArray)).array();

        ByteBuffer buf = StandardCharsets.UTF_8.encode(CharBuffer.wrap(pwArray));
        byte[] _pwArray = new byte[buf.limit()];
        buf.get(_pwArray);
        //****
        */

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
        int length = account.getPassword().length;
        char[] charArray = new char[length];

        CharBuffer buffer = StandardCharsets.UTF_8.decode(ByteBuffer.allocate(length));

        buffer.get(charArray);

        return charArray;
    }


}
