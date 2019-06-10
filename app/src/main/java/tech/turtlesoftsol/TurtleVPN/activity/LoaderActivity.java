package tech.turtlesoftsol.TurtleVPN.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;

import android.view.View;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.DownloadListener;
import com.androidnetworking.interfaces.DownloadProgressListener;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.numberprogressbar.NumberProgressBar;

import tech.turtlesoftsol.TurtleVPN.BuildConfig;
import tech.turtlesoftsol.TurtleVPN.R;
import tech.turtlesoftsol.TurtleVPN.model.Server;
import tech.turtlesoftsol.TurtleVPN.util.PropertiesService;
import tech.turtlesoftsol.TurtleVPN.util.Stopwatch;
import com.viksaa.sssplash.lib.model.ConfigSplash;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.text.Html;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class LoaderActivity extends BaseActivity {
    private NumberProgressBar progressBar;
    private TextView commentsText;
    TextView t1;
    private Handler updateHandler;

    private final int LOAD_ERROR = 0;
    private final int DOWNLOAD_PROGRESS = 1;
    private final int PARSE_PROGRESS = 2;
    private final int LOADING_SUCCESS = 3;
    private final int SWITCH_TO_RESULT = 4;
    private final String BASE_URL = "http://www.vpngate.net/api/iphone/";
    private final String BASE_FILE_NAME = "vpngate.csv";

    private boolean premiumStage = true;

    private final String PREMIUM_URL = "http://www.vpngate.net/api/iphone/";
    private final String PREMIUM_FILE_NAME = "vpngate.csv";

    private int percentDownload = 0;
    private Stopwatch stopwatch;


    public void initSplash(ConfigSplash configSplash) {

        configSplash.setTitleSplash("Please Wait While"); //change your app name here
        configSplash.setTitleTextColor(R.color.mapLine);
        configSplash.setTitleTextSize(30f); //float value
        configSplash.setAnimTitleDuration(2000);
        configSplash.setAnimTitleTechnique(Techniques.RollIn);
        configSplash.setTitleFont("fonts/Pacifico.ttf"); //provide string to your font located in assets/fonts/
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(tech.turtlesoftsol.TurtleVPN.R.layout.activity_loader);
        progressBar = (NumberProgressBar) findViewById(tech.turtlesoftsol.TurtleVPN.R.id.number_progress_bar);
        commentsText = (TextView) findViewById(tech.turtlesoftsol.TurtleVPN.R.id.commentsText);
        t1 = (TextView) findViewById(R.id.quote_textview);
        getQuote("http://quotesondesign.com/wp-json/posts?filter%5Borderby%5D=rand&filter%5Bposts_per_page%5D=1&callback=22");

        if(savedInstanceState != null) {
            t1.setText(savedInstanceState.get("Quote").toString());
        }

        if (getIntent().getBooleanExtra("firstPremiumLoad", false))
            ((TextView) findViewById(tech.turtlesoftsol.TurtleVPN.R.id.loaderPremiumText)).setVisibility(View.VISIBLE);

        progressBar.setMax(100);
        updateHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                switch (msg.arg1) {
                    case LOAD_ERROR: {
                        commentsText.setText(msg.arg2);
                        progressBar.setProgress(100);
                    }
                    break;
                    case DOWNLOAD_PROGRESS: {
                        commentsText.setText(tech.turtlesoftsol.TurtleVPN.R.string.downloading_csv_text);
                        progressBar.setProgress(msg.arg2);

                    }
                    break;
                    case PARSE_PROGRESS: {
                        commentsText.setText(tech.turtlesoftsol.TurtleVPN.R.string.parsing_csv_text);
                        progressBar.setProgress(msg.arg2);
                    }
                    break;
                    case LOADING_SUCCESS: {
                        commentsText.setText(tech.turtlesoftsol.TurtleVPN.R.string.successfully_loaded);
                        progressBar.setProgress(100);
                        Message end = new Message();
                        end.arg1 = SWITCH_TO_RESULT;
                        updateHandler.sendMessageDelayed(end, 500);

                    }
                    break;
                    case SWITCH_TO_RESULT: {
                        if (!BuildConfig.DEBUG)
                            Answers.getInstance().logCustom(new CustomEvent("Time servers loading")
                                    .putCustomAttribute("Time servers loading", stopwatch.getElapsedTime()));

                        if (PropertiesService.getConnectOnStart()) {
                            Server randomServer = getRandomServer();
                            if (randomServer != null) {
                                newConnecting(randomServer, true, true);
                            } else {
                                startActivity(new Intent(LoaderActivity.this, HomeActivity.class));
                            }
                        } else {
                            startActivity(new Intent(LoaderActivity.this, HomeActivity.class));
                        }
                    }
                }
                return true;
            }
        });
        progressBar.setProgress(0);


    }
    private void getQuote(String url) {

        OkHttpClient client = new OkHttpClient();

        final Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                updateUIWithError(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Gson gson = new Gson();
                Quotes[] quotesArray = gson.fromJson(response.body().charStream(), Quotes[].class);
                updateUI(quotesArray[0].content);
            }
        });
    }

    public void updateUI(final String content){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                t1.setText(Html.fromHtml(content));
            }
        });
    }


    public void updateUIWithError(final Exception e){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(LoaderActivity.this, "Error " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the user's current game state
        savedInstanceState.putString("Quote", t1.getText().toString());

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }



    @Override
    protected void onResume() {
        super.onResume();
        downloadCSVFile(BASE_URL, BASE_FILE_NAME);
    }

    @Override
    protected boolean useHomeButton() {
        return false;
    }

    @Override
    protected boolean useMenu() {
        return false;
    }

    private void downloadCSVFile(String url, String fileName) {
        stopwatch = new Stopwatch();

        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build();

        AndroidNetworking.download(url, getCacheDir().getPath(), fileName)
                .setTag("downloadCSV")
                .setPriority(Priority.MEDIUM)
                .setOkHttpClient(okHttpClient)
                .build()
                .setDownloadProgressListener(new DownloadProgressListener() {
                    @Override
                    public void onProgress(long bytesDownloaded, long totalBytes) {
                        if(totalBytes <= 0) {
                            // when we dont know the file size, assume it is 1200000 bytes :)
                            totalBytes = 1200000;
                        }

                        if (!premiumServers || !premiumStage) {
                            if (percentDownload <= 90)
                            percentDownload = percentDownload + (int)((100 * bytesDownloaded) / totalBytes);
                        } else {
                            percentDownload = (int)((100 * bytesDownloaded) / totalBytes);
                        }

                        Message msg = new Message();
                        msg.arg1 = DOWNLOAD_PROGRESS;
                        msg.arg2 = percentDownload;
                        updateHandler.sendMessage(msg);
                    }
                })
                .startDownload(new DownloadListener() {
                    @Override
                    public void onDownloadComplete() {
                        if (premiumServers && premiumStage) {
                            premiumStage = false;
                            downloadCSVFile(PREMIUM_URL, PREMIUM_FILE_NAME);
                        } else {
                            parseCSVFile(BASE_FILE_NAME);
                        }
                    }
                    @Override
                    public void onError(ANError error) {
                        Message msg = new Message();
                        msg.arg1 = LOAD_ERROR;
                        msg.arg2 = tech.turtlesoftsol.TurtleVPN.R.string.network_error;
                        updateHandler.sendMessage(msg);
                    }
                });
    }

    private void parseCSVFile(String fileName) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(getCacheDir().getPath().concat("/").concat(fileName)));
        } catch (IOException e) {
            e.printStackTrace();
            Message msg = new Message();
            msg.arg1 = LOAD_ERROR;
            msg.arg2 = tech.turtlesoftsol.TurtleVPN.R.string.csv_file_error;
            updateHandler.sendMessage(msg);
        }
        if (reader != null) {
            try {
                int startLine = 2;
                int type = 0;

                if (premiumServers && premiumStage) {
                    startLine = 0;
                    type = 1;
                } else {
                    dbHelper.clearTable();
                }

                int counter = 0;
                String line = null;
                while ((line = reader.readLine()) != null) {
                    if (counter >= startLine) {
                        dbHelper.putLine(line, type);
                    }
                    counter++;
                    if (!premiumServers || !premiumStage) {
                        Message msg = new Message();
                        msg.arg1 = PARSE_PROGRESS;
                        msg.arg2 = counter;// we know that the server returns 100 records
                        updateHandler.sendMessage(msg);
                    }
                }

                if (premiumServers && !premiumStage) {
                    Message end = new Message();
                    end.arg1 = LOADING_SUCCESS;
                    updateHandler.sendMessageDelayed(end,200);
                } else {
                    premiumStage = true;
                    parseCSVFile(PREMIUM_FILE_NAME);

                }

            } catch (Exception e) {
                e.printStackTrace();
                Message msg = new Message();
                msg.arg1 = LOAD_ERROR;
                msg.arg2 = tech.turtlesoftsol.TurtleVPN.R.string.csv_file_error_parsing;
                updateHandler.sendMessage(msg);
            }
        }
    }
}
