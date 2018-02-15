package iaik.bacc.camilla.androidcredentialstore.tools;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import iaik.bacc.camilla.androidcredentialstore.R;

/**
 * Created by Camilla on 15.02.2018.
 */

public class FullScreenDialog extends DialogFragment
{
    public static final String TAG = "FullScreenDialog";

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogStyle);
    }

    @Override
    public void onStart()
    {
        super.onStart();

        Dialog dialog = getDialog();
        if (dialog != null)
        {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState)
    {
        super.onCreateView(inflater, parent, savedInstanceState);

//        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_about, parent, false);
        View view = inflater.inflate(R.layout.dialog_about, parent, false);
        return view;
    }
}
