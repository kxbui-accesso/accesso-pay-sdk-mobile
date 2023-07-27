package com.accesso.accessopay;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.accesso.accessopay.databinding.FragmentFirstBinding;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class FirstFragment extends Fragment {

    private static final MediaType JSON
            = MediaType.get("application/json; charset=utf-8");

    private FragmentFirstBinding binding;

    private final OkHttpClient client = new OkHttpClient();

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentFirstBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.buttonFirst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirstFragment.this.submitData("https://api.dev-na2.accessoticketing.com/accesso-pay-sessions", FirstFragment.this.buildJsonObj(), FirstFragment.this.buildHeaders());
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void submitData(String url, String json, Headers headers) {
            OkHttpClient client = new OkHttpClient();
            Handler mainHandler = new Handler(Looper.getMainLooper());

            RequestBody body = RequestBody.create(json, JSON);
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .headers(headers)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) {
                    mainHandler.post(new Runnable() {

                        @Override
                        public void run() {
                            try {
                                if (!response.isSuccessful())
                                    throw new IOException("Unexpected code " + response);

                                JSONObject obj = new JSONObject(response.body().string());


                                Bundle bundle = new Bundle();
                                bundle.putString("paymentUrl", obj.getString("paymentUrl"));
                                NavHostFragment.findNavController(FirstFragment.this)
                                        .navigate(R.id.action_FirstFragment_to_SecondFragment, bundle);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    });


                }
            });
    }


    private String buildJsonObj() {
//        Gson gson = new Gson();
//        return gson.toJson("{\"amount\":10, \"attribute\":\"value\"}");
        return "{" +
                "    \"payload\": \"{\\\"amount\\\":10, \\\"attribute\\\":\\\"value\\\"}\",\n" +
                "    \"language\": \"en-US\",\n" +
                "    \"merchantId\": 800,\n" +
                "    \"sessionState\": \"INITIALIZE\"\n" +
                "}";
    }

    private Headers buildHeaders() {
        Headers.Builder builder = new Headers.Builder();
        builder.add("Com-Accessopassport-Client", "accesso111").add("Accept", String.valueOf(JSON));
        return builder.build();
    }

}