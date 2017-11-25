package com.androidsrc.client;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.widget.TextView;

public class Client extends AsyncTask<Void, Void, Void> {

	String dstAddress;
	int dstPort;
	String response = "";
	TextView textResponse;

	//static AudioFormat format;
	AudioRecord audioRecord;
	private int sampleRate = 16000;
	private int channelConfig = AudioFormat.CHANNEL_CONFIGURATION_MONO;
	private int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
	int minBufSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat);
	public boolean status  = true;

	Client(String addr, int port,TextView textResponse) {
		dstAddress = addr;
		dstPort = port;
		this.textResponse=textResponse;
	}

	@Override
	protected Void doInBackground(Void... arg0) {
		Socket socket = null;
		try {
			socket = new Socket(dstAddress, dstPort);
			someK(socket);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			response = "UnknownHostException: " + e.toString();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			response = "IOException: " + e.toString();
		} finally {
			if (socket != null) {
				try {
					socket.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	void someK(Socket socket) {
		//int minBufSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat);
		//textResponse.setText("Connected to Sphinx Server");
		byte[] buffer = new byte[minBufSize];

		audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, sampleRate, channelConfig, audioFormat, minBufSize);
		audioRecord.startRecording();
		while (status) {
			minBufSize = audioRecord.read(buffer, 0, buffer.length);
			try {
				OutputStream ostream = socket.getOutputStream();
				ostream.write(buffer, 0, buffer.length);
				ostream.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	@Override
	protected void onPostExecute(Void result) {
		textResponse.setText(response);
		super.onPostExecute(result);
	}
}
