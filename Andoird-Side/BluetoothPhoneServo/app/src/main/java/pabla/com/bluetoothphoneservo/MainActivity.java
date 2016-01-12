package pabla.com.bluetoothphoneservo;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;


public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";
    BluetoothAdapter mBluetoothAdapter;
    BluetoothDevice mDevice;
    private static String address = "98:D3:31:20:60:48";
    private static final UUID MY_UUID =
            UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    ThreadConnectBTdevice myThreadConnectBTdevice;
    ThreadConnected myThreadConnected;
    TextView textStatus;

    TextView joint1TextView;
    SeekBar joint1SeekBar;
    TextView joint2TextView;
    SeekBar joint2SeekBar;
    TextView joint3TextView;
    SeekBar joint3SeekBar;
    TextView joint4TextView;
    SeekBar joint4SeekBar;
    CheckBox joint5CheckBox;

    Button goButton;
    Button plotButton;
    Button taskButton;

    Button joint1PlusButton;
    Button joint1MinusButton;
    Button joint2PlusButton;
    Button joint2MinusButton;
    Button joint3PlusButton;
    Button joint3MinusButton;
    Button joint4PlusButton;
    Button joint4MinusButton;

    boolean connected = true;


    ArrayList<byte[]> jointDataPlots = new ArrayList<>();
    int dataCounter = 0;
    /*byte[] jointDataPlot1 = new byte[5];
    byte[] jointDataPlot2 = new byte[5];
    byte[] jointDataPlot3 = new byte[5];*/


    ThreadTask myThreadTask;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        textStatus = (TextView)findViewById(R.id.status);


        joint1TextView = (TextView)findViewById(R.id.joint_1_textView);
        joint1SeekBar = (SeekBar)findViewById(R.id.joint_1_seekBar);
        joint1SeekBar.setMax(180);

        joint2TextView = (TextView)findViewById(R.id.joint_2_textView);
        joint2SeekBar = (SeekBar)findViewById(R.id.joint_2_seekBar);
        joint2SeekBar.setMax(180);

        joint3TextView = (TextView)findViewById(R.id.joint_3_textView);
        joint3SeekBar = (SeekBar)findViewById(R.id.joint_3_seekBar);
        joint3SeekBar.setMax(180);

        joint4TextView = (TextView)findViewById(R.id.joint_4_textView);
        joint4SeekBar = (SeekBar)findViewById(R.id.joint_4_seekBar);
        joint4SeekBar.setMax(180);

        joint5CheckBox = (CheckBox)findViewById(R.id.joint_5_checkBox);
        joint1SeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                joint1TextView.setText("Joint 1: " + progress + " degrees / 180 degrees");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        joint2SeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                joint2TextView.setText("Joint 2: " + progress + " degrees / 180 degrees");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        joint3SeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                joint3TextView.setText("Joint 3: "+progress+" degrees / 180 degrees");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        joint4SeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                joint4TextView.setText("Joint 4: "+progress+" degrees / 180 degrees");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        goButton = (Button) findViewById(R.id.goButton);
        plotButton = (Button) findViewById(R.id.plotButton);
        taskButton = (Button) findViewById(R.id.taskButton);

        goButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(getBaseContext(), "Sending joint data.", Toast.LENGTH_SHORT).show();
                byte[] data = getProgressData();
                myThreadConnected.write(data);

            }
        });

        plotButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(getBaseContext(), "Adding joint data points.", Toast.LENGTH_SHORT).show();
                jointDataPlots.add(getProgressData());

                /*if(dataCounter == 0) {
                    jointDataPlot1 = getProgressData();
                }
                else if(dataCounter == 1) {
                    jointDataPlot2 = getProgressData();
                }
                else if(dataCounter == 2) {
                    jointDataPlot3 = getProgressData();
                }*/
                //dataCounter++;
            }
        });

        taskButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(getBaseContext(), "Starting task.", Toast.LENGTH_SHORT).show();
                myThreadTask = null;
                myThreadTask = new ThreadTask();
                myThreadTask.start();

            }
        });


        joint1PlusButton = (Button) findViewById(R.id.joint_1_plusButton);
        joint1MinusButton = (Button) findViewById(R.id.joint_1_minusButton);
        joint2PlusButton = (Button) findViewById(R.id.joint_2_plusButton);
        joint2MinusButton = (Button) findViewById(R.id.joint_2_minusButton);
        joint3PlusButton = (Button) findViewById(R.id.joint_3_plusButton);
        joint3MinusButton = (Button) findViewById(R.id.joint_3_minusButton);
        joint4PlusButton = (Button) findViewById(R.id.joint_4_plusButton);
        joint4MinusButton = (Button) findViewById(R.id.joint_4_minusButton);

        joint1PlusButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                int progress = addToProgress(joint1SeekBar.getProgress(), 2);
                joint1SeekBar.setProgress(progress);
                byte[] data = getProgressData();
                myThreadConnected.write(data);

            }
        });
        joint1MinusButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                int progress = addToProgress(joint1SeekBar.getProgress(),-2);
                joint1SeekBar.setProgress(progress);
                byte[] data = getProgressData();
                myThreadConnected.write(data);
            }
        });
        joint2PlusButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                int progress = addToProgress(joint2SeekBar.getProgress(),2);
                joint2SeekBar.setProgress(progress);
                byte[] data = getProgressData();
                myThreadConnected.write(data);
            }
        });
        joint2MinusButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                int progress = addToProgress(joint2SeekBar.getProgress(),-2);
                joint2SeekBar.setProgress(progress);
                byte[] data = getProgressData();
                myThreadConnected.write(data);
            }
        });
        joint3PlusButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                int progress = addToProgress(joint3SeekBar.getProgress(),2);
                joint3SeekBar.setProgress(progress);
                byte[] data = getProgressData();
                myThreadConnected.write(data);
            }
        });
        joint3MinusButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                int progress = addToProgress(joint3SeekBar.getProgress(),-2);
                joint3SeekBar.setProgress(progress);
                byte[] data = getProgressData();
                myThreadConnected.write(data);
            }
        });
        joint4PlusButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                int progress = addToProgress(joint4SeekBar.getProgress(),2);
                joint4SeekBar.setProgress(progress);
                byte[] data = getProgressData();
                myThreadConnected.write(data);
            }
        });
        joint4MinusButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                int progress = addToProgress(joint4SeekBar.getProgress(),-2);
                joint4SeekBar.setProgress(progress);
                byte[] data = getProgressData();
                myThreadConnected.write(data);
            }
        });
        init();
    }

    public int addToProgress(int progress, int amount){
        int progressTemp = progress;
        progressTemp += amount;
        if(progressTemp>180)
            progressTemp = 180;
        else if(progressTemp<0)
            progressTemp = 0;
        return progressTemp;
    }

    public byte[] getProgressData(){
        byte progress1 = (byte) joint1SeekBar.getProgress();
        byte progress2 = (byte) joint2SeekBar.getProgress();
        byte progress3 = (byte) joint3SeekBar.getProgress();
        byte progress4 = (byte) joint4SeekBar.getProgress();
        byte progress5 = (byte) 0;
        if (joint5CheckBox.isChecked())
            progress5 = 0;
        else
            progress5 = 90;
        byte[] data = {progress1, progress2, progress3, progress4, progress5};
        return data;
    }

    public void init()
    {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(getBaseContext(), "Bluetooth is not supported on this device.", Toast.LENGTH_SHORT).show();
        }
        else{
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 1);
                Toast.makeText(getBaseContext(), "Bluetooth is off and needs to turn on.", Toast.LENGTH_SHORT).show();
            }
            bluetoothReady();
        }
    }


    public void bluetoothReady() {
        boolean found = false;
        //mDevice = mBluetoothAdapter.get      address);
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        // If there are paired devices
        if (pairedDevices.size() > 0) {
            // Loop through paired devices
            for (BluetoothDevice device : pairedDevices) {
                if(device.getName().equals("HC-06") && device.getAddress().equals(address))
                {
                    Log.d(TAG,"FOUND");
                    mDevice = device;
                    found = true;
                    break;
                }
            }
        }

        if(found == true) {
            mBluetoothAdapter.cancelDiscovery();
            myThreadConnectBTdevice = new ThreadConnectBTdevice(mDevice);
            myThreadConnectBTdevice.start();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(myThreadConnectBTdevice!=null){
            myThreadConnectBTdevice.cancel();
            myThreadConnectBTdevice = null;
        }
        if(myThreadConnected!=null){
            myThreadConnected.cancel();
            myThreadConnected = null;
        }
        connected = false;
    }

    @Override
    protected void onPause() {
        super.onPause();

        Toast.makeText(getBaseContext(), "Killing On pause.", Toast.LENGTH_SHORT).show();
        if(myThreadConnectBTdevice!=null){
            myThreadConnectBTdevice.cancel();
            myThreadConnectBTdevice = null;
        }
        if(myThreadConnected!=null){
            myThreadConnected.cancel();
            myThreadConnected = null;
        }
        connected = false;
    }

    @Override
    public void onStop(){
        super.onStop();
        Toast.makeText(getBaseContext(), "Killing On stop.", Toast.LENGTH_SHORT).show();
        if(myThreadConnectBTdevice!=null){
            myThreadConnectBTdevice.cancel();
            myThreadConnectBTdevice = null;
        }
        if(myThreadConnected!=null){
            myThreadConnected.cancel();
            myThreadConnected = null;
        }
        connected = false;
    }

    //Called in ThreadConnectBTdevice once connect successed
    //to start ThreadConnected
    private void startThreadConnected(BluetoothSocket socket){

        myThreadConnected = new ThreadConnected(socket);
        myThreadConnected.start();
        //myThreadConnected.write(new byte[]{0x5A});  //writing 90 to servo 1 to test if connection has been established
    }

    /*
    ThreadConnectBTdevice:
    Background Thread to handle BlueTooth connecting
    */
    private class ThreadConnectBTdevice extends Thread {

        private BluetoothSocket bluetoothSocket = null;
        private final BluetoothDevice bluetoothDevice;


        private ThreadConnectBTdevice(BluetoothDevice device) {
            bluetoothDevice = device;

            try {
                bluetoothSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
                textStatus.setText("bluetoothSocket: \n" + bluetoothSocket);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            boolean success = false;
            try {
                bluetoothSocket.connect();
                success = true;
            } catch (IOException e) {
                e.printStackTrace();

                final String eMessage = e.getMessage();
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        textStatus.setText("something wrong bluetoothSocket.connect(): \n" + eMessage);
                    }
                });

                try {
                    bluetoothSocket.close();
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }

            if(success){
                //connect successful
                final String msgconnected = "connect successful:\n"
                        + "BluetoothSocket: " + bluetoothSocket + "\n"
                        + "BluetoothDevice: " + bluetoothDevice;

                runOnUiThread(new Runnable(){

                    @Override
                    public void run() {
                        textStatus.setText(msgconnected);

                    }});

                startThreadConnected(bluetoothSocket);
            }else{
                //fail
            }
        }

        public void cancel() {

            Toast.makeText(getApplicationContext(),
                    "close bluetoothSocket",
                    Toast.LENGTH_LONG).show();

            try {
                bluetoothSocket.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }

    }

    /*
    ThreadConnected:
    Background Thread to handle Bluetooth data communication
    after connected
     */
    private class ThreadConnected extends Thread {
        private final BluetoothSocket connectedBluetoothSocket;
        private final InputStream connectedInputStream;
        private final OutputStream connectedOutputStream;

        public ThreadConnected(BluetoothSocket socket) {
            connectedBluetoothSocket = socket;
            InputStream in = null;
            OutputStream out = null;

            try {
                in = socket.getInputStream();
                out = socket.getOutputStream();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            connectedInputStream = in;
            connectedOutputStream = out;
        }

        @Override
        public void run() {
            byte[] buffer = new byte[1024];
            int bytes;

            while (connected) {
                /*try {
                    bytes = connectedInputStream.read(buffer);
                    String strReceived = new String(buffer, 0, bytes);
                    final String msgReceived = String.valueOf(bytes) +
                            " bytes received:\n"
                            + strReceived;

                    runOnUiThread(new Runnable(){

                        @Override
                        public void run() {
                            textStatus.setText(msgReceived);
                        }});

                } catch (IOException e) {
                    e.printStackTrace();

                    final String msgConnectionLost = "Connection lost:\n"
                            + e.getMessage();
                    runOnUiThread(new Runnable(){

                        @Override
                        public void run() {
                            textStatus.setText(msgConnectionLost);
                        }});
                }*/
            }
        }

        public void write(byte[] buffer) {
            try {
                connectedOutputStream.flush();
                connectedOutputStream.write(buffer);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        public void cancel() {
            try {
                connectedInputStream.close();
                connectedOutputStream.close();
                connectedBluetoothSocket.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    private class ThreadTask extends Thread {

        @Override
        public void run() {
            while(true){

                for(int index = 0; index< jointDataPlots.size();index++){
                    myThreadConnected.write(jointDataPlots.get(index));
                    try {
                        this.sleep(4000);
                    }catch(InterruptedException error){}
                }


                /*myThreadConnected.write(jointDataPlot2);
                try {
                    this.sleep(5000);
                }catch(InterruptedException error){}
                myThreadConnected.write(jointDataPlot3);
                try {
                    this.sleep(5000);
                }catch(InterruptedException error){}*/
            }
        }
    }

}