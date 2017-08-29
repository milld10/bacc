package com.example.camilla.androidcredentialstore.tools;

import android.support.design.widget.TextInputEditText;
import android.util.Log;
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
    public static byte[] charToByte(TextInputEditText pw)
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

        Log.w("CONVERTER", "length of pw: " + length);
        Log.w("CONVERTER", "account_id: " + account.getAccount_id());
        Log.w("CONVERTER", "account_name: " + account.getAccount_name());
        Log.w("CONVERTER", "pw: " + account.getPassword());

        Log.w("CONVERTER", "before decode");
        CharBuffer buffer = StandardCharsets.UTF_8.decode(ByteBuffer.allocate(length));



        Log.w("CONVERTER", "after decode");

        char[] charArray = new char[buffer.limit()];

        Log.w("CONVERTER", "charArray: " + charArray);

        buffer.get(charArray);
        Log.w("CONVERTER", "charArray: " + charArray);
        return charArray;
    }


}
