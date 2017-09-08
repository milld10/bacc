package iaik.bacc.camilla.androidcredentialstore.tools;

import android.util.Patterns;

/**
 * Class CheckingTools holds methods for checking the input of the user
 */

public class CheckingTools
{
    public static boolean websiteOk(String website)
    {
        if(!website.isEmpty() && Patterns.WEB_URL.matcher(website).matches())
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public static boolean usernameOk(String username)
    {
        if(!username.isEmpty())
            return true;

        return false;
    }


    public static boolean passwordOk(byte[] password)
    {
        if(password.length > 0)
            return true;

        return false;
    }
}
