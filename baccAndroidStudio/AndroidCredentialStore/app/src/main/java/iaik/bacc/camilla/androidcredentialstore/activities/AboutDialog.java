package iaik.bacc.camilla.androidcredentialstore.activities;

import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import java.text.SimpleDateFormat;

import iaik.bacc.camilla.androidcredentialstore.R;

/**
 * Created by Camilla on 15.02.2018.
 */

public class AboutDialog extends Activity
{
    private static final String TAG = "AboutDialog";

    TextView titel;
    TextView institute;
    TextView project;
    TextView author;
    TextView authorName;
    TextView advisor;
    TextView advisorName;
    TextView date;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_about);

        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT);

        titel = (TextView) findViewById(R.id.titel);
        institute = (TextView) findViewById(R.id.institute);
        project = (TextView) findViewById(R.id.project);

        author = (TextView) findViewById(R.id.author);
        authorName = (TextView) findViewById(R.id.author_name);
        advisor = (TextView) findViewById(R.id.advisor);
        advisorName = (TextView) findViewById(R.id.advisor_name);

        date = (TextView) findViewById(R.id.date);

        date.setText("11.03.2018");

    }
}