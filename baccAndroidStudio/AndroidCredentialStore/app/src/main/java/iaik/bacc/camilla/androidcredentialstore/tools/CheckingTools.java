package iaik.bacc.camilla.androidcredentialstore.tools;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.util.Patterns;
import android.widget.BaseExpandableListAdapter;

import iaik.bacc.camilla.androidcredentialstore.activities.MainActivity;

/**
 * Class CheckingTools holds methods for checking the input of the user when adding a new account
 * Also checking tools for bluetooth low energy
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



    //here
    public static boolean passwordOk(byte[] password)
    {
        if(password.length > 0)
            return true;

        return false;
    }


    public static boolean checkBluetooth(BluetoothAdapter bluetoothAdapter)
    {
        if(bluetoothAdapter == null || !bluetoothAdapter.isEnabled())
        {
            return false;
        }
        else
        {
            return true;
        }
    }


    public static void requestUserBluetooth(Activity activity)
    {
//        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//        activity.startActivityForResult(intent, MainActivity.REQUEST_ENABLE_BT);


    }
}
