package mk.ukim.finki.mpip.studentskiprasanja;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FirstWindowAcitivity extends AppCompatActivity {

    // креирање променливи за
    // алатките во xml file-от.
    private RecyclerView chatsRV;
    private ImageButton sendMsgIB;
    private EditText userMsgEdt;
    private final String USER_KEY = "user";
    private final String BOT_KEY = "bot";

    // креирање променлива за volley request queue.
    private RequestQueue mRequestQueue;

    // креирање на променливи за листа и адаптер класа
    private ArrayList<ChatsModel> messageModalArrayList;
    private ChatRVAdapter messageRVAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_window_acitivity);

        // иницијализација на сите наши views во продолжение
        chatsRV = findViewById(R.id.idRVChats);
        sendMsgIB = findViewById(R.id.idIBSend);
        userMsgEdt = (EditText) findViewById(R.id.idEdtMessage);
        userMsgEdt.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        // иницијализација на request queue.
        mRequestQueue = Volley.newRequestQueue(FirstWindowAcitivity.this);
        mRequestQueue.getCache().clear();

        // креирање на листа
        messageModalArrayList = new ArrayList<>();

        // додавање на click listener на копчето за испраќање на порака
        sendMsgIB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // проверка дали пораката внесена од корисникот е празна или не
                if (userMsgEdt.getText().toString().isEmpty()) {
                    // ако е празна, покажи ја оваа порака
                    Toast.makeText(FirstWindowAcitivity.this, "Те молам внеси порака :)", Toast.LENGTH_SHORT).show();
                    return;
                }

                // повик на метод за испраќање на порака за да се добие одговор од ботот
                // тука имаме 2 методи, бидејќи се обидовме на 2 начини:
                // со Volley и со Retrofit - моменталната верзија работи со Volley

                String userMessage = userMsgEdt.getText().toString();
                boolean valid = userMessage.matches("[A-Za-z ]+");

                if(valid) {
                    userMessage = convertCyrilic(userMessage);
                }

                sendMessage(userMessage);
                // getResponse(userMsgEdt.getText().toString());


                // поставување на празно поле во edit text полето
                if(userMsgEdt.getText().toString().length() > 0){
                    userMsgEdt.getText().clear();
                    userMsgEdt.setText("");
                }

            }
        });

        // иницијализација на адаптер класа и предавање на листа
        messageRVAdapter = new ChatRVAdapter(messageModalArrayList, this);

        // креирање на променлива за linear layout manager
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(FirstWindowAcitivity.this, RecyclerView.VERTICAL, false);

        // поставување на layout manager во recycler view-то
        chatsRV.setLayoutManager(linearLayoutManager);

        // поставување на адаптер во recycler view-то
        chatsRV.setAdapter(messageRVAdapter);
    }


    private void getResponse(String message) {
        messageModalArrayList.add(new ChatsModel(message, USER_KEY));
        messageRVAdapter.notifyDataSetChanged();
        String url = "http://api.brainshop.ai/get?bid=156983&key=K5746WdM6vHF6tah&uid=mashape&msg=" + message;
        String BASE_URL = "http://api.brainshop.ai/";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RetrofitAPI retrofitAPI = retrofit.create(RetrofitAPI.class);
        Call<MsgModel> call = retrofitAPI.getMessage(url);
        call.enqueue(new Callback<MsgModel>() {
            @Override
            public void onResponse(Call<MsgModel> call, retrofit2.Response<MsgModel> response) {
                if (response.isSuccessful()){
                    MsgModel model = response.body();
                    messageModalArrayList.add(new ChatsModel(model.getCnt(),BOT_KEY));
                    messageRVAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<MsgModel> call, Throwable t) {

                messageModalArrayList.add(new ChatsModel("Те молам одново напиши ја пораката!",BOT_KEY));
                messageRVAdapter.notifyDataSetChanged();
            }
        });


    }

    private void sendMessage(String userMsg) {
        // предавање на пораката внесена од корисникот во листата
        messageModalArrayList.add(new ChatsModel(userMsg, USER_KEY));
        messageRVAdapter.notifyDataSetChanged();

        // url за „мозокот“
        // се поставува mshape за uid.
        String url = "http://api.brainshop.ai/get?bid=156983&key=K5746WdM6vHF6tah&uid=mashape&msg=" + userMsg;

        // креирање на променлива за request queue.
        RequestQueue queue = Volley.newRequestQueue(FirstWindowAcitivity.this);

        // креирање на json object барање за get барање и предавање на нашето url
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    // извлекување на податоци од json response и додавање на
                    // ваквиот одговор во листата

                    String botResponse = response.getString("cnt");
                    messageModalArrayList.add(new ChatsModel(botResponse, BOT_KEY));

                    // известување на адаптерот за промена на податоците
                    messageRVAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();

                    // справување со грешки кај ботот
                    messageModalArrayList.add(new ChatsModel("Нема одговор", BOT_KEY));
                    messageRVAdapter.notifyDataSetChanged();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // справување со грешки
                messageModalArrayList.add(new ChatsModel("Извини, не е пронајден одговор.", BOT_KEY));
                Toast.makeText(FirstWindowAcitivity.this, "Нема одговор од ботот...", Toast.LENGTH_SHORT).show();
            }
        });

        // додавање на json object
        // во нашата редица (queue)
        queue.add(jsonObjectRequest);
    }

    @Override
    protected void onPause() {
        // криење на тастатурата со цел да се избегне getTextBeforeCursor на неактивна InputConnection
        InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);

        inputMethodManager.hideSoftInputFromWindow(userMsgEdt.getWindowToken(), 0);

        super.onPause();
    }

    public static String convertCyrilic(String message){
        Character[] abcCyr =   {' ','а','б','в','г','д','ѓ','е', 'ж','з','ѕ','и','ј','к','л','љ','м','н','њ','о','п','р','с','т', 'ќ','у', 'ф','х','ц','ч','џ','ш', 'А','Б','В','Г','Д','Ѓ','Е', 'Ж','З','Ѕ','И','Ј','К','Л','Љ','М','Н','Њ','О','П','Р','С','Т', 'Ќ', 'У','Ф', 'Х','Ц','Ч','Џ','Ш'};
        String[] abcLat = {" ","a","b","v","g","d","gj","e","zh","z","dz","i","j","k","l","lj","m","n","nj","o","p","r","s","t","kj","u","f","h", "c","ch", "dj","sh","A","B","V","G","D","Gj","E","Zh","Z","Dz","I","J","K","L","Lj","M","N","Nj","O","P","R","S","T","KJ","U","F","H", "C","Dj", "Dz","Sh"};
        List<String> abcLatList = new ArrayList<>(Arrays.asList(abcLat));
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < message.length(); i++) {
            int m = i + 1;
            if (m < message.length()) {
                StringBuilder check = new StringBuilder();
                check.append(message.charAt(i));
                check.append(message.charAt(m));
                if (abcLatList.contains(check.toString())) {
                    int position = abcLatList.indexOf(check.toString());
                    builder.append(abcCyr[position]);
                    i++;
                } else {
                    String c = String.valueOf(message.charAt(i));
                    if (abcLatList.contains(c)) {
                        int position = abcLatList.indexOf(c);
                        builder.append(abcCyr[position]);
                    }
                }
            } else {
                String c = String.valueOf(message.charAt(i));
                if (abcLatList.contains(c)) {
                    int position = abcLatList.indexOf(c);
                    builder.append(abcCyr[position]);
                }

            }
        }
        return builder.toString();
    }
}