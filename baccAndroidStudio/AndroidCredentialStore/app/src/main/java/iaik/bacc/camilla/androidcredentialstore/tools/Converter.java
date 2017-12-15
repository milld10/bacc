package iaik.bacc.camilla.androidcredentialstore.tools;

import android.support.design.widget.TextInputEditText;
import iaik.bacc.camilla.androidcredentialstore.models.Account;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;

/**
 * class Converter holds methods for the conversion of byte[] to char[] and vice versa
 */

public class Converter {

    /**
     * converts char[] to byte[], a pw typed in by user is given in an char[]
     * byte[] is needed to store into DB
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


    /**
     * converts the byte[] back to a char[] to be displayed in the application again.
     */
    public static char[] byteToChar(Account account)
    {
        byte[] byteArray = account.getPassword();

        CharBuffer buffer = StandardCharsets.UTF_8.decode(ByteBuffer.wrap(byteArray));
        char[] charArray = new char[buffer.limit()];
        buffer.get(charArray);

        return charArray;
    }
}
