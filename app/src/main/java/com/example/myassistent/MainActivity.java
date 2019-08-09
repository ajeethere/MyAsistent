package com.example.myassistent;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.room.Room;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myassistent.CartDatabase.QuestionAnswerDB;
import com.example.myassistent.CartDatabase.Table;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    ImageButton click;
    TextView editText;
    QuestionAnswerDB questionAnswerDB;
    List<Table> data;
    EditText answerTxt;
    int lastPosition;
    boolean answered=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText=findViewById(R.id.text_view);
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MainActivity.this);
// ...Irrelevant code for customizing the buttons and title
        LayoutInflater inflater = MainActivity.this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.adding_answer_dialogue, null);
        dialogBuilder.setView(dialogView);
        final AlertDialog alertDialog = dialogBuilder.create();

         questionAnswerDB= Room.databaseBuilder(getApplicationContext(), QuestionAnswerDB.class, "cart").allowMainThreadQueries().build();

        final TextToSpeech ttobj=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
            }
        });
        ttobj.setLanguage(Locale.ENGLISH);
        ttobj.setPitch(0);
        final SpeechRecognizer mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        final SpeechRecognizer answerListner=SpeechRecognizer.createSpeechRecognizer(this);
        answerListner.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle bundle) {

            }

            @Override
            public void onBeginningOfSpeech() {

            }

            @Override
            public void onRmsChanged(float v) {

            }

            @Override
            public void onBufferReceived(byte[] bytes) {

            }

            @Override
            public void onEndOfSpeech() {

            }

            @Override
            public void onError(int i) {

            }

            @Override
            public void onResults(Bundle bundle) {
                ArrayList<String> matches = bundle
                        .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (matches!=null){
                    answerTxt.setText(matches.get(0));
                }
            }

            @Override
            public void onPartialResults(Bundle bundle) {

            }

            @Override
            public void onEvent(int i, Bundle bundle) {

            }
        });

        final Intent mSpeechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,
                Locale.getDefault());

        mSpeechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle bundle) {

            }

            @Override
            public void onBeginningOfSpeech() {

            }

            @Override
            public void onRmsChanged(float v) {

            }

            @Override
            public void onBufferReceived(byte[] bytes) {

            }

            @Override
            public void onEndOfSpeech() {

            }

            @Override
            public void onError(int i) {
                editText.setText("SORRY! You did not ask anything...");
                ttobj.speak("SORRY! You did not ask anything...", TextToSpeech.QUEUE_FLUSH, null);
            }

            @Override
            public void onResults(Bundle bundle) {
                //getting all the matches
                final ArrayList<String> matches = bundle
                        .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                data=questionAnswerDB.MyDao().getAnswers();
                final Table table=new Table();
                lastPosition=data.size();
                //displaying the first match
                if (matches != null){
                    if (data.size()>0) {
                        for (int i = 0; i < data.size(); i++) {

                            if (data.get(i).getQuestion().equals(matches.get(0))) {
                                editText.setText(matches.get(0));
                                ttobj.speak(data.get(i).getAnswer(), TextToSpeech.QUEUE_FLUSH, null);
                            } else {
                                    editText.setText("SORRY! Answer is not in my data");
                                    ttobj.speak("SORRY! Answer is not in my data, Do you want to add it?", TextToSpeech.QUEUE_FLUSH, null);


                                    TextView questionTxt = dialogView.findViewById(R.id.question_txt);
                                    questionTxt.setText(matches.get(0));
                                    answerTxt = dialogView.findViewById(R.id.answer_txt);
                                    Button cancelBtn = dialogView.findViewById(R.id.cancel_btn);
                                    cancelBtn.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            alertDialog.dismiss();
                                        }
                                    });

                                    Button okBtn = dialogView.findViewById(R.id.ok_btn);
                                    okBtn.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            if (answerTxt.getText().toString().trim().equals("")) {
                                                ttobj.speak("Please give answer", TextToSpeech.QUEUE_FLUSH, null);
                                            } else {
                                                alertDialog.dismiss();
                                                table.setId(lastPosition + 1);
                                                table.setAnswer(answerTxt.getText().toString());
                                                table.setQuestion(matches.get(0));
                                                questionAnswerDB.MyDao().addQuestionAnswer(table);
                                                ttobj.speak("Thank you", TextToSpeech.QUEUE_FLUSH, null);
                                            }
                                        }
                                    });

                                    ImageButton andwerListnerBtn = dialogView.findViewById(R.id.answer_listner_btn);
                                    andwerListnerBtn.setOnTouchListener(new View.OnTouchListener() {
                                        @Override
                                        public boolean onTouch(View view, MotionEvent motionEvent) {
                                            switch (motionEvent.getAction()) {
                                                case MotionEvent.ACTION_UP:
                                                    answerListner.stopListening();
                                                    answerTxt.setHint("Please wait...");
                                                    break;

                                                case MotionEvent.ACTION_DOWN:
                                                    answerListner.startListening(mSpeechRecognizerIntent);
                                                    answerTxt.setText("");
                                                    answerTxt.setHint("Listening...");
                                                    break;
                                            }
                                            return false;
                                        }
                                    });

                                    alertDialog.show();
                            }
                        }
                    }else {
                            editText.setText("SORRY! I do not have data in my memory");
                            ttobj.speak("SORRY! I do not have data in my memory, Do you want to add it?", TextToSpeech.QUEUE_FLUSH, null);

                            TextView questionTxt = dialogView.findViewById(R.id.question_txt);
                            questionTxt.setText(matches.get(0));

                            answerTxt = dialogView.findViewById(R.id.answer_txt);

                            Button cancelBtn = dialogView.findViewById(R.id.cancel_btn);
                            cancelBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    alertDialog.dismiss();
                                }
                            });

                            Button okBtn = dialogView.findViewById(R.id.ok_btn);
                            okBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if (answerTxt.getText().toString().trim().equals("")) {
                                        ttobj.speak("Please give answer", TextToSpeech.QUEUE_FLUSH, null);
                                    } else {
                                        alertDialog.dismiss();
                                        table.setId(lastPosition + 1);
                                        table.setAnswer(answerTxt.getText().toString());
                                        table.setQuestion(matches.get(0));
                                        questionAnswerDB.MyDao().addQuestionAnswer(table);
                                        ttobj.speak("Thank you", TextToSpeech.QUEUE_FLUSH, null);
                                    }
                                }
                            });
                        ImageButton andwerListnerBtn=dialogView.findViewById(R.id.answer_listner_btn);
                        andwerListnerBtn.setOnTouchListener(new View.OnTouchListener() {
                            @Override
                            public boolean onTouch(View view, MotionEvent motionEvent) {
                                switch (motionEvent.getAction()) {
                                    case MotionEvent.ACTION_UP:
                                        answerListner.stopListening();
                                        answerTxt.setHint("Please wait...");
                                        break;

                                    case MotionEvent.ACTION_DOWN:
                                        answerListner.startListening(mSpeechRecognizerIntent);
                                        answerTxt.setText("");
                                        answerTxt.setHint("Listening...");
                                        break;
                                }
                                return false;
                            }
                        });

                        alertDialog.show();
                    }

                }
            }

            @Override
            public void onPartialResults(Bundle bundle) {

            }

            @Override
            public void onEvent(int i, Bundle bundle) {

            }
        });

        click=(ImageButton) findViewById(R.id.button);
        checkPermission();

        click.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_UP:
                        mSpeechRecognizer.stopListening();
                        editText.setHint("Please wait...");
                        break;

                    case MotionEvent.ACTION_DOWN:
                        mSpeechRecognizer.startListening(mSpeechRecognizerIntent);
                        editText.setText("");
                        editText.setHint("Listening...");
                        break;
                }
                return false;
            }
        });

    }
    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!(ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED)) {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.parse("package:" + getPackageName()));
                startActivity(intent);
                finish();
            }
        }

    }
}
