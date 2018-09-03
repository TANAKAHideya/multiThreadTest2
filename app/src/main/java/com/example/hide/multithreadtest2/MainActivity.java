package com.example.hide.multithreadtest2;

import android.app.Activity;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.TextView;

public class MainActivity extends Activity implements View.OnClickListener {

        public static final int MAX_THREADS=16; /* up to 16 */
        View changeButton,clearButton,checkBox;
        boolean debugmode = false;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_main);

        changeButton = this.findViewById(R.id.Button01);
        changeButton.setOnClickListener((android.view.View.OnClickListener) this);
        clearButton = this.findViewById(R.id.Button02);
        clearButton.setOnClickListener((android.view.View.OnClickListener) this);
        checkBox = this.findViewById(R.id.checkBox1);
        ((CompoundButton) checkBox).setChecked(false);
        checkBox.setOnClickListener((android.view.View.OnClickListener) this);

    }

    public void onClick(View v) {
        View edtEnd = this.findViewById(R.id.EditText01);
        String strEnd = ((TextView) edtEnd).getText().toString();
        if(v==changeButton){
            {
                String strSum;
                long sUpTimeMillis = SystemClock.uptimeMillis();

                long sum = sumFromJNI(Integer.valueOf(strEnd),MAX_THREADS,debugmode);
                strSum = String.valueOf(sum);

                long eUpTimeMillis = SystemClock.uptimeMillis();

                View txtResult = this.findViewById(R.id.TextView04);
                ((TextView)txtResult).setText(strSum);

                View elpTime = this.findViewById(R.id.TextView05);
                ((TextView)elpTime).setText("Native : " + String.valueOf(eUpTimeMillis - sUpTimeMillis) + " ms");
            } /* */

            {
                String strSum;
                long sUpTimeMillis = SystemClock.uptimeMillis();

                if(!strEnd.equals("")){
                    long numEnd = Long.valueOf(strEnd);
                    long sum = 0;

                    Count[] thread = new Count[MAX_THREADS];

                    for(int i=0;i<MAX_THREADS;i++){
                        thread[i] = new Count(debugmode ? numEnd + i : numEnd);
                    }
                    for(int i=0;i<MAX_THREADS;i++){
                        thread[i].start();
                    }
                    try {
                        for(int i=0;i<MAX_THREADS;i++){
                            thread[i].join();
                        }
                    } catch(Exception ex) {
                        ex.printStackTrace();
                    }

                    for(int i=0;i<MAX_THREADS;i++){
                        sum += thread[i].getSum();
                    }
                    strSum = String.valueOf(sum);
                } else {
                    strSum = String.valueOf(0);
                }
                long eUpTimeMillis = SystemClock.uptimeMillis();

                View txtResult = this.findViewById(R.id.TextView02);
                ((TextView)txtResult).setText(strSum);

                View elpTime = this.findViewById(R.id.TextView03);
                ((TextView)elpTime).setText("DalVik : " + String.valueOf(eUpTimeMillis - sUpTimeMillis) + " ms");
            } /* */
        } else if(v==clearButton) {
            {
                View elpTime = this.findViewById(R.id.TextView05);
                ((TextView)elpTime).setText("Native : 0 ms");
            }
            {
                View elpTime = this.findViewById(R.id.TextView03);
                ((TextView)elpTime).setText("DalVik : 0 ms");
            }
        } else {
            if((boolean)( ((CompoundButton) checkBox).isChecked() ) == false){
                debugmode = false;
            } else {
                debugmode = true;
            }
        }
    }

    public native long  sumFromJNI(int nNumber, int tno, boolean debugmode);
    static {
        System.loadLibrary("native-lib");
    }
}
class Count extends Thread {
    private long endNum;
    private long sum;
    public Count(long x){
        endNum=x;
    }
    public void run() {
        sum =0;
        for(int i=0;i<=endNum;i++){
            sum +=i;
        }
    }
    public long getSum(){
        return sum;
    }
}