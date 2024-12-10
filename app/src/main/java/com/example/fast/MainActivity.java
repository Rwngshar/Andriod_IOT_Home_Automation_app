package com.example.fast;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
  
    private static final String HC05_MAC_ADDRESS = "HC -05 MAC Address";  //  HC -05  MAC address 
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");    //HC - 05 UUID 
    private static final int REQUEST_BLUETOOTH_PERMISSION = 1;

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothDevice hc05Device;
    private BluetoothSocket socket;
    private OutputStream outputStream;
    private TextView textStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textStatus = findViewById(R.id.textStatus);
        Button buttonConnect = findViewById(R.id.buttonConnect);
        Switch switchOn1 = findViewById(R.id.switchOn1);
        Switch switchOn2 = findViewById(R.id.switchOn2);
        Switch switchOn3 = findViewById(R.id.switchOn3);
        Switch switchOn4 = findViewById(R.id.switchOn4);
        Button buttonOnL = findViewById(R.id.buttonOnL);
        Button buttonOnS = findViewById(R.id.buttonOnS);
        Button buttonOnAll = findViewById(R.id.buttonOnAll);
        Button buttonOff = findViewById(R.id.buttonOff);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        hc05Device = bluetoothAdapter.getRemoteDevice(HC05_MAC_ADDRESS);

        // Check and request BLUETOOTH_CONNECT permission if needed
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.BLUETOOTH_CONNECT},
                        REQUEST_BLUETOOTH_PERMISSION);
            }
        }

        buttonConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectBluetooth();
            }
        });

        switchOn1.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sendCommand(isChecked ? "A" : "a"); // Command to toggle switch 1
        });

        switchOn2.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sendCommand(isChecked ? "B" : "b"); // Command to toggle switch 2
        });

        switchOn3.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sendCommand(isChecked ? "C" : "c"); // Command to toggle switch 3
        });

        switchOn4.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sendCommand(isChecked ? "D" : "d"); // Command to toggle switch 4
        });

        buttonOnL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendCommand("O"); // Command to turn on L
            }
        });

        buttonOnS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendCommand("O"); // Command to turn on S
            }
        });

        buttonOnAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendCommand("F"); // Command to turn on all switches
            }
        });

        buttonOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendCommand("f"); // Command to turn off the bulb
            }
        });
    }

    private void connectBluetooth() {
        // Check if the BLUETOOTH_CONNECT permission is granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission not granted, request it
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.BLUETOOTH_CONNECT},
                    REQUEST_BLUETOOTH_PERMISSION);
            return; // Exit method, connection will be attempted after permission is granted
        }

        // Permission granted, proceed with Bluetooth connection
        try {
            socket = hc05Device.createRfcommSocketToServiceRecord(MY_UUID);
            socket.connect();
            outputStream = socket.getOutputStream();
            textStatus.setText("Status: Connected");
            Toast.makeText(this, "Bluetooth Connected", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to connect to Bluetooth", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendCommand(String command) {
        if (outputStream != null) {
            try {
                outputStream.write(command.getBytes());
                Toast.makeText(this, "Command sent: " + command, Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to send command", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Bluetooth not connected", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Handle permission request result
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults); // Call super method

        if (requestCode == REQUEST_BLUETOOTH_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with Bluetooth operations
                Toast.makeText(this, "Bluetooth permission granted", Toast.LENGTH_SHORT).show();
                connectBluetooth(); // Attempt connection again
            } else {
                // Permission denied, handle accordingly
                Toast.makeText(this, "Bluetooth permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
