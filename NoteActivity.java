package edu.utep.cs.cs4330.notebookio;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.CharacterStyle;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import edu.utep.cs.cs4330.notebookio.utility.Tuple;

import java.util.ArrayList;
import java.util.List;
public class NoteActivity extends AppCompatActivity implements KeyEvent.Callback {
    String paragraph = "";//some text. Other
    private StringBuffer buffer;
    private JSONObject note;
    private JSONArray spans;
    private int format;
    boolean isBold = false, isItalics = false, isUnderlined = false;
    private NoteWorker noteWorker = new NoteWorker();
    int end = 17;
    public final static int MAX_E_SIZE = 40;
    private String jsonStr = "{\"paragraph\":\"some text. Other \",\"spans\":[{\"id\": 0,\"size\":10,\"start\":0,\"end\": 10,\"format\":1101,\"color\":\"\",\"font\":\"MonoSpace\"},{\"id\": 1,\"size\":10,\"start\":10,\"end\": 16,\"format\":1110,\"color\":\"\",\"font\":\"MonoSpace\"},{\"id\": 2,\"size\":10,\"start\":16,\"end\": 17,\"format\":0,\"color\":\"\",\"font\":\"MonoSpace\"}]}";
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        List<Tuple<Integer, String>> colorPickerList = new ArrayList<>();{
            colorPickerList.add(new Tuple<>(R.drawable.black, "black"));
            colorPickerList.add(new Tuple<>(R.drawable.blue, "blue"));
            colorPickerList.add(new Tuple<>(R.drawable.green, "green"));
            colorPickerList.add(new Tuple<>(R.drawable.red, "red"));
            colorPickerList.add(new Tuple<>(R.drawable.grey, "grey"));
            colorPickerList.add(new Tuple<>(R.drawable.yellow, "yellow"));//

        }

        //actionBar.
        final android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        //setActionBar(toolbar);
        String[] itemSize = new String[MAX_E_SIZE];{
            for(int i = 0; i < itemSize.length; i++){
                itemSize[i] = ""+i;
            }
        };
        //get the spinner from the xml.
        Spinner textSizeSpinner = findViewById(R.id.test_size_spinner);
        Spinner textColorSpinner = findViewById(R.id.test_color_spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, itemSize);
        //set the spinners adapter to the previously created one.
        textSizeSpinner.setAdapter(adapter);
        textColorSpinner.setAdapter(new CustomSpinnerAdapter<>(this, colorPickerList));

        initialize(10, 1000, "#FFFFF","MonoSans", paragraph);
        EditText noteView = findViewById(R.id.note_state);
        noteView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Log.d("__ before text changed", s +" "+start+" "+count+" "+after);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d("__ on text changed", s +" "+start+" "+count+" "+before);
                int spanEnd, spanStart;
                JSONObject span;
                try {
                    JSONArray spans = (JSONArray) note.get("spans");
                    Log.d("__ ", spans.length() +" ");

                    span = (JSONObject) spans.get(spans.length()-1);
                    spanEnd = span.getInt("end");
                    spanStart = span.getInt("start");
                    Log.d("__ span size", spanStart +" "+spanEnd);
                    if(spanEnd < spanStart && spans.length() > 1){
                        spans.remove(spans.length() - 1);
                    }
                    if(count>before){
                        buffer.append(s.subSequence(start+before, count+start));
                        span.put("end", count+start); // same format@!!!@@
                    }else{
                        buffer = new StringBuffer(buffer.substring(0, before+start-1)); // backspace
                        span.put("end", before+start-1);
                    }
                    note.put("spans", spans);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                // handle new position?

                // pass the buffer to the service update last span
            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.d("__ after text changed", s.toString());
                Log.d("__ buffer", buffer.toString());
                noteWorker.onEvent("text_changed");
            }
        });
        // add a text change listener
    }

    /**
     * Called when bold button is clicked, sets a selection of edit text bold
     * @param view view of the button
     */
    public void setTextBold(View view){
        if(isBold) isBold = false;
        else isBold = true;
        // should change the format.
    }
    /**
     * Called when bold button is clicked, sets a selection of edit text bold
     * @param view view of the button
     */
    public void setTextItalics(View view){
        if(isItalics) isItalics = false;
        else isItalics = true;
        // should change the format.
    }
    /**
     * Called when bold button is clicked, sets a selection of edit text bold
     * @param view view of the button
     */
    public void setTextUnderlined(View view){
        if(isUnderlined) isUnderlined = false;
        else isUnderlined = true;
        // should change the format.
    }
    private int getFormat(){
       if(!isBold&&!isItalics&&!isUnderlined) {
            return 0;
       } else if(!isBold&&!isItalics&&isUnderlined){
            return 1001;
        }else if(!isBold&&isItalics&&!isUnderlined){
            return 1010;
       } else if(!isBold&& isItalics&& isUnderlined){
            return 1011;
       } else if(isBold&&!isItalics&&!isUnderlined){
            return 1100;
       } else if(isBold&&!isItalics&&isUnderlined){
           return 1101;
       } else if(isBold&&isItalics&&isUnderlined){
           return 1111;
       }
       return 0;
    }
    private void updateNote(JSONObject noteEncode) throws JSONException {
        Log.d("__ updating note", "true");
        String paragraph = noteEncode.getString("paragraph");
        JSONArray spans = noteEncode.getJSONArray("spans");
        JSONObject span;
        int id, start, end, size;
        int format, bold, italics, underline;
        String color, font, text;
        EditText note = findViewById(R.id.note_state);
        note.getEditableText().clear();
        note.getText().clear();
        note.setText(paragraph);
        String inner = note.getEditableText().toString();
        Log.d("__ setting text", inner);
        final SpannableStringBuilder spannableStrBld = new SpannableStringBuilder(inner);
        Log.d("__ array size", spans.length()+" ");
        for(int i = 0; i < spans.length(); i++){
            span = spans.getJSONObject(i);
            Log.d("__ iter", i+" ");
            id = span.getInt("id");
            start = span.getInt("start");
            end = span.getInt("end");
            size = span.getInt("size");
            format = span.getInt("format"); // getInt will return binary value wow
            color = span.getString("color");
            font = span.getString("font");
            // future format 1font1color1bold1italics1underline 11111
            // in octal format


            Log.d("__ format", format+" ");
            Log.d("__ ", "loc, "+ " "+ start+" "+end);
        }
       // note.setText(spannableStrBld);
       // note.invalidate();
    }
    /**
     *
     * @param style
     */
    private void setText(CharacterStyle style){
        EditText note = findViewById(R.id.note_state);
        int startSelection=note.getSelectionStart();
        int endSelection =note.getSelectionEnd();
        String text = note.getEditableText().toString();
        String selected = text.substring(startSelection, endSelection);
        //CharSequence content =
        final SpannableStringBuilder spannableStrBld = new SpannableStringBuilder(text);
        spannableStrBld.setSpan(style, startSelection, endSelection, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        note.setText(spannableStrBld);

    }
    private void setText(int typeface){
        final StyleSpan styleSpan = new StyleSpan(typeface);
        setText(styleSpan);
    }
    public class CustomSpinnerAdapter<T extends Tuple> extends ArrayAdapter implements SpinnerAdapter {

        private final List<T> objects; // android.graphics.Color list
        private final Context context;
        public CustomSpinnerAdapter(Context context, List<T> objects) {
            super(context, android.R.layout.simple_spinner_dropdown_item, objects);
            this.context = context;
            this.objects = objects;

        }

        @TargetApi(Build.VERSION_CODES.O)
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            super.getDropDownView(position, convertView, parent);
            return buildView(position, convertView, parent);
        }

        @NonNull
        @TargetApi(Build.VERSION_CODES.O)
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            return buildView(position, convertView, parent);
        }
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
        private View buildView(int position, View view, @NonNull ViewGroup parent){
            Drawable next;
            View line = view;
            if (line == null) {
                // Get a new instance of the row layout view
                LayoutInflater inflater = NoteActivity.this.getLayoutInflater();
                line = inflater.inflate(android.R.layout.simple_spinner_dropdown_item, null);
                int drawableId = (int) objects.get(position).k;
                String name = (String) objects.get(position).l;
                next = context.getResources().getDrawable(drawableId);
                ((TextView) line).setCompoundDrawablesRelativeWithIntrinsicBounds(next,null,null,null);
                ((TextView) line).setText(name);
            } else {
                int drawableId = (int) objects.get(position).k;
                String name = (String) objects.get(position).l;
                next = context.getResources().getDrawable(drawableId);
                ((TextView) line).setCompoundDrawablesRelativeWithIntrinsicBounds(next,null,null,null);
                ((TextView) line).setText(name);
            }
            return line;
        }
    }

    private void initialize(int size, int format, String color, String font, String paragraph){
        int id = 0;
        try {
            JSONObject span = new JSONObject();
            span.put("size", size);
            span.put("id", id);
            span.put("start", 0);
            span.put("end", 0);
            span.put("format", format);
            span.put("color", color);
            span.put("font", font);
            // initializes a new array of spans
            spans = new JSONArray();
            spans.put(0, span);
            // new buffer
            buffer = new StringBuffer(paragraph);
            note = new JSONObject();
            note.put("paragraph", "");
            note.put("spans", spans);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("__ json ", note.toString());

    }
    public class NoteWorker extends Thread{
        private boolean lock = true;
        Handler handler = new Handler();
        @Override
        public void run(){
            super.run();
            // should update the note based on the json object
            // maybe set text triggers the listener
            //could be notified by the listener
            while (getLock()){
                synchronized (this){
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                /* when event occurs, the thread is woken up and applies spans to text edit */
                handler.post(()->{
                    try {
                        JSONArray spans = (JSONArray) note.get("spans");
                        String paragraph = note.getString("paragraph");
                        EditText noteView;
                        int start, end, size, id, format;
                        String color, font;
                        JSONObject current;
                        noteView = findViewById(R.id.note_state);
                        noteView.getText().clear();
                        noteView.setText(paragraph);
                        String inner = noteView.getEditableText().toString();
                        Log.d("__ setting text", inner);
                        final SpannableStringBuilder spannableStrBld = new SpannableStringBuilder(inner);
                        for (int i = 0; i < spans.length(); i++) {
                            current = spans.getJSONObject(i);
                            start = current.getInt("start");
                            end = current.getInt("end");
                            size = current.getInt("size");
                            id = current.getInt("id");
                            color = current.getString("color");
                            font = current.getString("font");
                            format = current.getInt("format");
                            switch (format) {
                                case 0: {
                                    // no formatting
                                    final StyleSpan styleSpan = new StyleSpan(Typeface.NORMAL);
                                    spannableStrBld.setSpan(styleSpan, start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                                    break;
                                }
                                case 1001: {
                                    // for sure only underline
                                    spannableStrBld.setSpan(new UnderlineSpan(), start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                                    break;
                                }
                                case 1010: {
                                    // only italics
                                    final StyleSpan styleSpan = new StyleSpan(Typeface.ITALIC);
                                    spannableStrBld.setSpan(styleSpan, start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                                    break;
                                }
                                case 1011: {
                                    // italics and underline
                                    spannableStrBld.setSpan(new UnderlineSpan(), start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                                    final StyleSpan styleSpan = new StyleSpan(Typeface.ITALIC);
                                    spannableStrBld.setSpan(styleSpan, start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                                    break;
                                }
                                case 1100: {
                                    //only bold
                                    final StyleSpan styleSpan = new StyleSpan(Typeface.BOLD);
                                    spannableStrBld.setSpan(styleSpan, start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                                    break;
                                }
                                case 1101: {
                                    //bold italics
                                    final StyleSpan styleSpan = new StyleSpan(Typeface.BOLD);
                                    spannableStrBld.setSpan(new UnderlineSpan(), start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                                    spannableStrBld.setSpan(styleSpan, start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                                    break;
                                }
                                case 1110: {
                                    //bold italics
                                    final StyleSpan styleSpan = new StyleSpan(Typeface.BOLD_ITALIC);
                                    spannableStrBld.setSpan(styleSpan, start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                                    break;
                                }
                                case 1111: {
                                    // all styles
                                    final StyleSpan styleSpan = new StyleSpan(Typeface.BOLD_ITALIC);
                                    spannableStrBld.setSpan(styleSpan, start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                                    spannableStrBld.setSpan(new UnderlineSpan(), start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                                    break;
                                }
                            }
                        }
                    }catch (JSONException e) {
                        e.printStackTrace();
                    }
                });
            }
        }
        private synchronized boolean getLock(){
            return lock;
        }

        /**
         * notify when event occurs
         * @param event event description
         */
        private synchronized void onEvent(String event){
            Log.d("__ event", event);
            notify();
        }
        private synchronized void release(){
            lock = false;
            notify();
        }
    }
}
