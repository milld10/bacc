package iaik.bacc.camilla.androidcredentialstore.tools;

import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import iaik.bacc.camilla.androidcredentialstore.models.Account;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;

/**
 * class Converter holds various methods for conversions of different data types
 * - byte[] to char[] and vice versa
 * - String to byte[] and vice versa
 */

public class Converter {

    /**
     * converts char[] to byte[], a pw typed in by user is given in an char[]
     * byte[] is needed to store into DB
     */
    public static byte[] charToByte(TextInputEditText editText)
    {
        int length = editText.length();
        char[] charArray = new char[length];
        editText.getText().getChars(0, length, charArray, 0);

        ByteBuffer buffer = StandardCharsets.UTF_8.encode(CharBuffer.wrap(charArray));
        byte[] byteArray = new byte[buffer.limit()];
        buffer.get(byteArray);

        return byteArray;
    }

    /**
     * converts the byte[] back to a char[] to be displayed in the application again.
     */
    public static char[] byteToChar(byte[] byteArray)
    {
//        byte[] byteArray = account.getPassword();

        CharBuffer buffer = StandardCharsets.UTF_8.decode(ByteBuffer.wrap(byteArray));
        char[] charArray = new char[buffer.limit()];
        buffer.get(charArray);

        return charArray;
    }

    /**
     * converts byte[] to String to be displayed in the application
     */
    @NonNull
    public static String byteToString(byte[] byteArray) throws UnsupportedEncodingException
    {
        return new String(byteArray, "UTF-8");
    }

    /**
     * converts String to byte[] to be saved in the DB
     */
    @NonNull
    public static byte[] StringToByte(String string) throws UnsupportedEncodingException
    {
        return string.getBytes("UTF-8");
    }

    private final static char[] hexArray = "0123456789ABCDEF".toCharArray();
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

}
