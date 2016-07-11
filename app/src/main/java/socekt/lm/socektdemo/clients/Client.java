package socekt.lm.socektdemo.clients;

import android.os.AsyncTask;
import android.os.StrictMode;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

import socekt.lm.socektdemo.utils.Constants;
import socekt.lm.socektdemo.utils.MyApplication;

/**
 * 客户端
 * 
 * @author way
 * 
 */
public class Client {

	private Socket client;
	private ClientThread clientThread;
	private String ip;
	private int port;
	MyApplication myApplication;

	public Client(String ip, int port) {
		this.ip = ip;
		this.port = port;
	}

	public void start(MyApplication myApplication) {
	  this.myApplication=myApplication;
		new OkAsyncTask().execute();



	}

	// 直接通过client得到读线程
	public ClientInputThread getClientInputThread() {
		return clientThread.getIn();
	}

	// 直接通过client得到写线程
	public ClientOutputThread getClientOutputThread() {
		return clientThread.getOut();
	}

	// 直接通过client停止读写消息
	public void setIsStart(boolean isStart) {
		clientThread.getIn().setStart(isStart);
		clientThread.getOut().setStart(isStart);
	}

	public class ClientThread extends Thread {

		private ClientInputThread in;
		private ClientOutputThread out;

		public ClientThread(Socket socket) {
			in = new ClientInputThread(socket);
			out = new ClientOutputThread(socket);
		}

		public void run() {
			in.setStart(true);
			out.setStart(true);
			in.start();
			out.start();
		}

		// 得到读消息线程
		public ClientInputThread getIn() {
			return in;
		}

		// 得到写消息线程
		public ClientOutputThread getOut() {
			return out;
		}
	}

	class OkAsyncTask extends AsyncTask {


		@Override
		protected void onPostExecute(Object o) {
			myApplication.setClientStart(true);
		}

		@Override
		protected Object doInBackground(Object[] objects) {

			try {
				client = new Socket();
				// client.connect(new InetSocketAddress(Constants.SERVER_IP,
				// Constants.SERVER_PORT), 3000);
				SocketAddress socketAddress=new InetSocketAddress(ip, port);
				client.connect(socketAddress,3000);
				if (client.isConnected()) {
					// System.out.println("Connected..");
					clientThread = new ClientThread(client);
					clientThread.start();
				}
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
			return true;
		}
	}
}