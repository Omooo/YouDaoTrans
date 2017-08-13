package top.omooo.admin.youdaotrans;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 利用有道翻译API实现在线翻译
 * 文档地址：http://ai.youdao.com/docs/api.s#java-demo
 */
public class MainActivity extends AppCompatActivity {

    private EditText mEditText;
    private Button mButton;
    private TextView mTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mEditText = (EditText) findViewById(R.id.editText1);
        mButton = (Button) findViewById(R.id.button1);
        mTextView = (TextView) findViewById(R.id.textView1);

    }

    //点击按钮方法
    public void translateString(View view) {
        String appId = "2fae5612d413d792";
        String appKey = "s6eqm2C9BmCE1k6VaLDGHSJ85Bswgp0E";
        String salt = "2";
        String textURLEncode = mEditText.getText().toString().trim();
        try {
            //进行URL编码，处理中文拼接URL导致网络访问失败
            String text = URLEncoder.encode(textURLEncode, "utf-8");
            Log.i("2333---text", text);
            //拼成需要加密的字符串
            String startMD5 = appId + textURLEncode+salt+appKey;
            //调用getMD5Str()方法获取签名sign
            String resultMD5 = getMD5Str(startMD5);
            Log.i("2333---resultMD5", resultMD5);
            //生成URL
            String URL = "http://openapi.youdao.com/api?q=" + text + "&from=auto&to=auto&appKey=" + appId + "&salt=" + salt + "&sign=" + resultMD5;
            Log.i("2333---URL", URL);

            //执行异步任务
            new MyAsync().execute(URL);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
//        Log.i("2333---text", text);
//        //拼成需要加密的字符串
//        String startMD5 = appId + text+salt+appKey;
//        //调用getMD5Str()方法获取签名sign
//        String resultMD5 = getMD5Str(startMD5);
//        Log.i("2333---resultMD5", resultMD5);
//        //生成URL
//        String URL = "http://openapi.youdao.com/api?q=" + text + "&from=auto&to=auto&appKey=" + appId + "&salt=" + salt + "&sign=" + resultMD5;
//        Log.i("2333---URL", URL);
//
//        //执行异步任务
//        new MyAsync().execute(URL);
    }

    private String getMD5Str(String str) {
        MessageDigest messageDigest = null;

        try {
            messageDigest = MessageDigest.getInstance("MD5");

            messageDigest.reset();

            messageDigest.update(str.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            System.out.println("NoSuchAlgorithmException caught!");
            System.exit(-1);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        byte[] byteArray = messageDigest.digest();

        StringBuffer md5StrBuff = new StringBuffer();

        for (int i = 0; i < byteArray.length; i++) {
            if (Integer.toHexString(0xFF & byteArray[i]).length() == 1)
                md5StrBuff.append("0").append(Integer.toHexString(0xFF & byteArray[i]));
            else
                md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));
        }

        //16位加密取32位加密的第9位到25位
        return md5StrBuff.toString().toUpperCase();
    }

    //异步网络请求
    class MyAsync extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.i("2333---s", s);
            jieXi jieXiString = new jieXi();
            jieXiString.resultAnalyze(s);
            //显示结果
//            String testString = "test t";
//            Log.i("2333--testString", testString);
            Log.i("2333--transResultString",jieXiString.transResultString);
            mTextView.setText(jieXiString.transResultString);
            Toast.makeText(MainActivity.this, "怎么说？美滋滋。", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);

        }

        @Override
        protected String doInBackground(String... strings) {
            String url = strings[0];
            Log.i("2333---url", url);
            URLConnection connection;
            InputStream inputStream;
            String transResult = "";
            try {
                connection = new URL(url).openConnection();
                inputStream = connection.getInputStream();
                InputStreamReader inputReader = new InputStreamReader(inputStream);
                BufferedReader reader = new BufferedReader(inputReader);
                String line;
                while ((line = reader.readLine()) !=null) {
                    transResult += line;
                }
                Log.i("2333---transResult", transResult);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return transResult;
        }
    }

}
