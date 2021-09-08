package com.example.hidtest;

import java.lang.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;

import android.app.*;
import android.bluetooth.*;
import android.bluetooth.le.*;
import android.content.*;
import android.content.pm.PackageManager;
import android.os.*;
import android.util.*;
import android.view.*;
import android.widget.*;

class Utils
{
	public static byte[] make_byte_array(String hexstr)
	{
		if (hexstr == null || hexstr.length() < 2)
			return null;

		byte[] array = new byte[hexstr.length() / 2];

		for (int i = 0; i < array.length; i++) {
			char hc = hexstr.charAt(i*2);
			char lc = hexstr.charAt(i*2+1);

			int h = 0;
			if (hc >= '0' && hc <= '9') h = (int)(hc - '0');
			else if (hc >= 'A' && hc <= 'F') h = (int)(hc - 'A') + 10;
			else if (hc >= 'a' && hc <= 'f') h = (int)(hc - 'a') + 10;

			int l = 0;
			if (lc >= '0' && lc <= '9') l = (int)(lc - '0');
			else if (lc >= 'A' && lc <= 'F') l = (int)(lc - 'A') + 10;
			else if (lc >= 'a' && lc <= 'f') l = (int)(lc - 'a') + 10;

			array[i] = (byte)((h << 4) | l);
		}

		return array;
	}

	public static byte[] make_ble_address(long n) {
		return new byte[] {
			(byte)((n & 0xff0000000000L) >> 40),
			(byte)((n & 0xff00000000L) >> 32),
			(byte)((n & 0xff000000L) >> 24),
			(byte)((n & 0xff0000L) >> 16),
			(byte)((n & 0xff00L) >> 8),
			(byte)(n & 0xffL)
		};
	}
}

class Keys
{
	// top bit is a flag that determines whether the character of a key requires the shift modifier, eg. ! = shift+1, @ = shift+2, ? = shift+/, etc.
	static final int[] ascii_to_cmd = new int[] {
		0x2c, 0x9e, 0xb4, 0xa0, 0xa1, 0xa2, 0xa4, 0x34, // ' ' - '\''
		0xa6, 0xa7, 0xa5, 0xae, 0x36, 0x2d, 0x37, 0x38, // '(' - '/'
		0x27, 0x1e, 0x1f, 0x20, 0x21, 0x22, 0x23, 0x24, // '0' - '7'
		0x25, 0x26, 0xb3, 0x33, 0xb6, 0x2e, 0xb7, 0xb8, // '8' - '?'
		0x9f, 0x84, 0x85, 0x86, 0x87, 0x88, 0x89, 0x8a, // '@' - 'G'
		0x8b, 0x8c, 0x8d, 0x8e, 0x8f, 0x90, 0x91, 0x92, // 'H' - 'O'
		0x93, 0x94, 0x95, 0x96, 0x97, 0x98, 0x99, 0x9a, // 'P' - 'W'
		0x9b, 0x9c, 0x9d, 0x2f, 0x31, 0x30, 0xa3, 0xad, // 'X' - '_'
		0x35, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0a, // '`' - 'g'
		0x0b, 0x0c, 0x0d, 0x0e, 0x0f, 0x10, 0x11, 0x12, // 'h' - 'o'
		0x13, 0x14, 0x15, 0x16, 0x17, 0x18, 0x19, 0x1a, // 'p' - 'w'
		0x1b, 0x1c, 0x1d, 0xaf, 0xb1, 0xb0, 0xb5, 0x00  // 'x' - DEL
	};
}

class UUIDs
{
	static final String STANDARD = "-0000-1000-8000-00805F9B34FB";
	static final String HID = "00001812" + STANDARD;
	static final String DIS = "0000180A" + STANDARD;
	static final String BAS = "0000180F" + STANDARD;
	static final String CHAR_DEVICE_NAME = "00002A00" + STANDARD;
	static final String CHAR_REPORT_MAP = "00002A4B" + STANDARD;
	static final String CHAR_HID_INFORMATION = "00002A4A" + STANDARD;
	static final String CHAR_HID_CONTROL_POINT = "00002A4C" + STANDARD;
	static final String CHAR_PNP_ID = "00002A50" + STANDARD;
	static final String CHAR_BATTERY_LEVEL = "00002A19" + STANDARD;
	static final String CHAR_REPORT = "00002A4D" + STANDARD;
	static final String DESC_REPORT_REFERENCE = "00002908" + STANDARD;
	static final String DESC_CCC = "00002902" + STANDARD;
}

class Data
{
	static final byte[] REPORT_MAP = Utils.make_byte_array
	(
		"050C" + /*        Usage Page (Consumer Devices)       */
		"0901" + /*        Usage (Consumer Control)            */
		"A101" + /*        Collection (Application)            */
		"8502" + /*        Report ID=2                         */
		"0507" + /*        Usage Page (Keyboard/Keypad)        */
		"1500" + /*        Logical Minimum (0)                 */
		"2501" + /*        Logical Maximum (1)                 */
		"7501" + /*        Report Size (1)                     */
		"9508" + /*        Report Count (8)                    */
		"09E0" + /* 1       Usage (LeftControl)                */
		"09E1" + /* 2       Usage (LeftShift)                  */
		"09E2" + /* 3       Usage (LeftAlt)                    */
		"09E3" + /* 4       Usage (LeftGUI)                    */
		"09E4" + /* 5       Usage (RightControl)               */
		"09E5" + /* 6       Usage (RightShift)                 */
		"09E6" + /* 7       Usage (RightAlt)                   */
		"09E7" + /* 8       Usage (RightGUI)                   */
		"8102" + /*        Input (Data, Variable, Absolute)    */
		"0507" + /*        Usage Page (Keyboard/Keypad)        */
		"9501" + /*        Report Count (1)                    */
		"7508" + /*        Report Size (8)                     */
		"1504" + /*        Logical Minimum (4)                 */
		"25DF" + /*        Logical Maximum (223)               */
		"0507" + /*        Usage Page (Key codes)              */
		"1904" + /*        Usage Minimum (4)                   */
		"29DF" + /*        Usage Maximum (223)                 */
		"8100" + /*        Input (Data, Array)                 */
		"C0"     /*        End Collection                      */
	);

	static final byte[] HID_SPEC = new byte[]
	{
		0x01, 0x11, // HID Class Specification 1.11
		0x00, 0x03
	};

	static final byte[] REPORT_REF = new byte[] {
		0x02, 0x01  // Report ID (2 is our ID), Report Type (1 = Input).
	};

	static final byte[] PNP_ID = new byte[]
	{
		2,    // Vendor ID Source
		0, 0, // Vendor ID
		0, 0, // Product ID
		0, 0  // Product Version
	};
}

class Gatt
{
	static final int READ_ONLY = 0;
	static final int WRITE_ONLY = 1;
	static final int READ_WRITE = 2;

	int read_pms = BluetoothGattCharacteristic.PERMISSION_READ;
	int write_pms = BluetoothGattCharacteristic.PERMISSION_WRITE;

	public Gatt(int level)
	{
		if (level <= 1) {
			read_pms = BluetoothGattCharacteristic.PERMISSION_READ;
			write_pms = BluetoothGattCharacteristic.PERMISSION_WRITE;
		}
		else if (level == 2) {
			read_pms = BluetoothGattCharacteristic.PERMISSION_READ_ENCRYPTED;
			write_pms = BluetoothGattCharacteristic.PERMISSION_WRITE_ENCRYPTED;
		}
		else {
			read_pms = BluetoothGattCharacteristic.PERMISSION_READ_ENCRYPTED_MITM;
			write_pms = BluetoothGattCharacteristic.PERMISSION_WRITE_ENCRYPTED_MITM;
		}
	}

	BluetoothGattCharacteristic make_char(String name, int access, byte[] value)
	{
		int pms;
		int props;

		if (access == READ_ONLY) {
			pms = read_pms;
			props = BluetoothGattCharacteristic.PROPERTY_READ;
		}
		else if (access == WRITE_ONLY) {
			pms = write_pms;
			props = BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE;
		}
		else {
			pms = read_pms | write_pms;
			props = BluetoothGattCharacteristic.PROPERTY_READ | BluetoothGattCharacteristic.PROPERTY_NOTIFY;
		}

		var ch = new BluetoothGattCharacteristic(UUID.fromString(name), props, pms);
		ch.setValue(value);
		return ch;
	}

	BluetoothGattDescriptor make_desc(String name, int access, byte[] value)
	{
		int pms;
		if (access == READ_ONLY)
			pms = read_pms;
		else if (access == WRITE_ONLY)
			pms = write_pms;
		else
			pms = read_pms | write_pms;

		var desc = new BluetoothGattDescriptor(UUID.fromString(name), pms);
		desc.setValue(value);
		return desc;
	}
}

class AdCallback extends AdvertiseCallback
{
	MainActivity main;

	public AdCallback(MainActivity m) {
		main = m;
	}

	@Override
	public void onStartSuccess(AdvertiseSettings settingsInEffect) {}

	@Override
	public void onStartFailure(int errorCode) {}
}

class Server extends BluetoothGattServerCallback
{
	//static final int DEFAULT_MTU = 23;
	//int mtu = DEFAULT_MTU;

	MainActivity main;
	BluetoothDevice device;
	LinkedBlockingQueue<Integer> add_service_sync;

	public Server(MainActivity m) {
		main = m;
		device = null;
		add_service_sync = new LinkedBlockingQueue<Integer>(1);
	}

	public void add_services(BluetoothGattService[] services)
	{
		new Thread() {
			@Override
			public void run() {
				add_service_sync.poll();
				for (int i = 0; i < services.length; i++) {
					main.gatt_server.addService(services[i]);
					try {
						add_service_sync.take();
					}
					catch (InterruptedException e) {
						break;
					}
				}
			}
		}.start();
	}

	@Override
	public void onConnectionStateChange(BluetoothDevice dev, int status, int newState)
	{
		if (newState == BluetoothAdapter.STATE_CONNECTED) {
			this.device = dev;
			Log.i("HIDTEST", "onConnectionStateChange() to on");
		}
		else if (newState == BluetoothAdapter.STATE_DISCONNECTED) {
			this.device = null;
			Log.i("HIDTEST", "onConnectionStateChange() to off");
		}
	}

	/*
	@Override
	public void onMtuChanged(BluetoothDevice dev, int mtu) {
		this.device = dev;
		this.mtu = mtu;
	}
	*/

	@Override
	public void onCharacteristicReadRequest(BluetoothDevice dev, int requestId, int offset, BluetoothGattCharacteristic characteristic)
	{
		Log.i("HIDTEST", "onCharacteristicReadRequest()");
		this.device = dev;

		byte[] value = characteristic.getValue();

		if (offset == 0)
			main.gatt_server.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, 0, value);
		else {
			int trunc_size = value.length - offset;
			if (trunc_size <= 0)
				main.gatt_server.sendResponse(device, requestId, BluetoothGatt.GATT_FAILURE, 0, null);
			else {				
				byte[] truncated = new byte[trunc_size];
				System.arraycopy(value, offset, truncated, 0, trunc_size);
				main.gatt_server.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, 0, value);
			}
		}
	}

	@Override
	public void onCharacteristicWriteRequest(BluetoothDevice dev, int requestId, BluetoothGattCharacteristic characteristic, boolean preparedWrite, boolean responseNeeded, int offset, byte[] value)
	{
		Log.i("HIDTEST", "onCharacteristicWriteRequest()");
		this.device = dev;

		characteristic.setValue(value);
		if (responseNeeded)
			main.gatt_server.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, 0, value);
	}

	@Override
	public void onDescriptorReadRequest(BluetoothDevice dev, int requestId, int offset, BluetoothGattDescriptor descriptor)
	{
		Log.i("HIDTEST", "onDescriptorReadRequest()");
		this.device = dev;

		main.gatt_server.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, 0, descriptor.getValue());
	}

	@Override
	public void onDescriptorWriteRequest(BluetoothDevice dev, int requestId, BluetoothGattDescriptor descriptor, boolean preparedWrite, boolean responseNeeded, int offset, byte[] value)
	{
		Log.i("HIDTEST", "onDescriptorWriteRequest()");
		this.device = dev;

		descriptor.setValue(value);
		if (responseNeeded)
			main.gatt_server.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, 0, value);
	}

	@Override
	public void onNotificationSent(BluetoothDevice device, int status)
	{
		Log.i("HIDTEST", "onNotificationSent()");
		main.send_keys.comms_sync.offer(1);
	}

	@Override
	public void onServiceAdded(int status, BluetoothGattService service)
	{
		Log.i("HIDTEST", String.format("onServiceAdded() service=%s, status=%d", service.getUuid().toString(), status));
		add_service_sync.offer(1);
	}
}

class SendKeys extends Thread
{
	MainActivity main;
	LinkedBlockingQueue<Integer> keys_sync, comms_sync;

	public SendKeys(MainActivity m)
	{
		main = m;
		keys_sync = new LinkedBlockingQueue<Integer>(1);
		comms_sync = new LinkedBlockingQueue<Integer>(1);
	}

	boolean await_comms() {
		try {
			comms_sync.take();
		}
		catch (InterruptedException e) {
			return false;
		}
		return true;
	}

	@Override
	public void run()
	{
		boolean running = true;
		while (running) {
			Log.i("HIDTEST", "SendKeys top");

			try {
				keys_sync.take();
			}
			catch (InterruptedException e) {
				break;
			}

			if (main.server.device == null) {
				Log.i("HIDTEST", "SendKeys device was null");
				continue;
			}

			int len = main.buffer.fresh_size();
			byte[] packet = new byte[2];

			// clear comms_sync
			comms_sync.poll();

			Log.i("HIDTEST", String.format("SendKeys send %d keys", len));

			for (int i = 0; i < len; i++) {
				byte b = main.buffer.get_byte();
				packet[0] = (byte)((b & 0x80) != 0 ? 2 : 0); // if the key requires shift, set left_shift modifier
				packet[1] = (byte)(b & 0x7f);

				main.report_char.setValue(packet);
				main.gatt_server.notifyCharacteristicChanged(main.server.device, main.report_char, false);

				running = await_comms();
				if (!running) break;

				packet[0] = 0;
				packet[1] = 0;

				main.report_char.setValue(packet);
				main.gatt_server.notifyCharacteristicChanged(main.server.device, main.report_char, false);

				if (i < len-1) {
					running = await_comms();
					if (!running) break;
				}
			}
		}
	}
}

public class MainActivity extends Activity
{
	String adapter_name = "BLE HID Test";

	byte[] dictionary;
	Random rng;

	CircularBuffer buffer;
	Gatt gatt;
	AdCallback ad_callback;
	Server server;
	SendKeys send_keys;

	BluetoothManager bluetooth;
	BluetoothLeAdvertiser advertiser;

	BluetoothGattServer gatt_server;
	BluetoothGattCharacteristic report_char;
	BluetoothGattDescriptor report_desc;

	String[] permissions = new String[] {
		android.Manifest.permission.BLUETOOTH,
		android.Manifest.permission.BLUETOOTH_ADMIN,
		android.Manifest.permission.BLUETOOTH_PRIVILEGED
	};

	void request_permissions(String[] all_pms)
	{
		long needed = 0;
		int n_needed = 0;

		for (int i = 0; i < all_pms.length; i++) {
			var access = checkSelfPermission(all_pms[i]);
			int req = (access != PackageManager.PERMISSION_GRANTED) ? 1 : 0;
			needed <<= 1;
			needed |= (long)req;
			n_needed += req;
		}

		String[] requested = new String[n_needed];
		int idx = 0;

		for (int i = all_pms.length - 1; i >= 0; i--) {
			if ((needed & 1) == 1)
				requested[idx++] = all_pms[i];
			needed >>= 1;
		}

		requestPermissions(requested, 0);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		request_permissions(permissions);

		InputStream stream = null;
		try {
			stream = getResources().openRawResource(R.raw.words);
			dictionary = new byte[stream.available()];
			stream.read(dictionary);
			stream.close();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}

		((Button)findViewById(R.id.generate_btn)).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				generate_words();
			}
		});

		rng = new Random();

		buffer = new CircularBuffer();
		gatt = new Gatt(2);
		ad_callback = new AdCallback(this);
		server = new Server(this);
		send_keys = new SendKeys(this);
		//bt_notifier = new BT_Notifier(this);

		bluetooth = (BluetoothManager)getSystemService(Context.BLUETOOTH_SERVICE);

		var adapter = bluetooth.getAdapter();
		adapter.setName(adapter_name);

		if (!adapter.isEnabled())
			adapter.enable();

		setup_server();
		start_advertising();
	}

	@Override
	protected void onResume()
	{
		super.onResume();

		if (!send_keys.isAlive())
			send_keys = new SendKeys(this);

		send_keys.start();
		//generate_words();
	}

	@Override
	protected void onPause()
	{
		super.onPause();
		send_keys.interrupt();
	}

	@Override
	protected void onStop()
	{
		super.onStop();

		var adapter = bluetooth.getAdapter();
		if (adapter.isEnabled()) {
			stop_advertising();
			stop_server();
		}
	}

	void setup_server()
	{
		Log.i("HIDTEST", "setup_server()");

		this.gatt_server = bluetooth.openGattServer(this, server);

		var hid_service = new BluetoothGattService(UUID.fromString(UUIDs.HID), BluetoothGattService.SERVICE_TYPE_PRIMARY);

		this.report_char = gatt.make_char(UUIDs.CHAR_REPORT, Gatt.READ_WRITE, new byte[] {0, 0});
		this.report_desc = gatt.make_desc(UUIDs.DESC_CCC, Gatt.READ_WRITE, new byte[] {0, 0});

		report_char.addDescriptor(gatt.make_desc(UUIDs.DESC_REPORT_REFERENCE, Gatt.READ_ONLY, Data.REPORT_REF));
		report_char.addDescriptor(report_desc);

		hid_service.addCharacteristic(gatt.make_char(UUIDs.CHAR_DEVICE_NAME, Gatt.READ_ONLY, adapter_name.getBytes()));
		hid_service.addCharacteristic(gatt.make_char(UUIDs.CHAR_REPORT_MAP, Gatt.READ_ONLY, Data.REPORT_MAP));
		hid_service.addCharacteristic(gatt.make_char(UUIDs.CHAR_HID_INFORMATION, Gatt.READ_ONLY, Data.HID_SPEC));
		hid_service.addCharacteristic(gatt.make_char(UUIDs.CHAR_HID_CONTROL_POINT, Gatt.WRITE_ONLY, new byte[]{0}));
		hid_service.addCharacteristic(report_char);

		// I have done a service_dis to consistency
		var service_dis = new BluetoothGattService(UUID.fromString(UUIDs.DIS), BluetoothGattService.SERVICE_TYPE_PRIMARY);
		service_dis.addCharacteristic(gatt.make_char(UUIDs.CHAR_PNP_ID, Gatt.READ_ONLY, Data.PNP_ID));

		var bas_service = new BluetoothGattService(UUID.fromString(UUIDs.BAS), BluetoothGattService.SERVICE_TYPE_PRIMARY);
		bas_service.addCharacteristic(gatt.make_char(UUIDs.CHAR_BATTERY_LEVEL, Gatt.READ_ONLY, new byte[]{100}));

		var services = new BluetoothGattService[] { hid_service, service_dis, bas_service };
		server.add_services(services);
	}

	void start_advertising()
	{
		Log.i("HIDTEST", "start_advertising()");

		AdvertiseSettings settings = new AdvertiseSettings.Builder()
			.setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_BALANCED)
			.setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_MEDIUM)
			.setConnectable(true)
			.build();

		var hid_uuid = ParcelUuid.fromString(UUIDs.HID);

		AdvertiseData service_list = new AdvertiseData.Builder()
			.setIncludeDeviceName(true)
			.addServiceUuid(hid_uuid)
			.addServiceUuid(ParcelUuid.fromString(UUIDs.BAS))
			.addServiceUuid(ParcelUuid.fromString(UUIDs.DIS))
			.build();

		var adapter = bluetooth.getAdapter();

		this.advertiser = adapter.getBluetoothLeAdvertiser();
		advertiser.startAdvertising(settings, service_list, ad_callback);
	}

	void stop_server()
	{
		Log.i("HIDTEST", "stop_server()");

		if (gatt_server != null) {
			if (server.device != null)
				gatt_server.cancelConnection(server.device);

			gatt_server.close();
		}
	}

	void stop_advertising()
	{
		Log.i("HIDTEST", "stop_advertising()");

		if (advertiser != null)
			advertiser.stopAdvertising(ad_callback);
	}

	public void generate_words()
	{
		String words = "";
		for (int i = 0; i < 3; i++)
			words += pick_word(rng.nextInt()) + " ";

		((TextView)findViewById(R.id.test_text)).setText(words);

		send_string(words);
	}

	public String pick_word(int offset)
	{
		offset &= 0x7fffffff;
		offset %= dictionary.length;

		while (offset < dictionary.length && dictionary[offset] != (byte)'\n')
			offset++;

		offset++;

		if (offset >= dictionary.length)
			offset = 0;

		int last = offset;
		while (last < dictionary.length && dictionary[last] != (byte)'\n')
			last++;

		last--;

		return new String(dictionary, offset, last - offset + 1);
	}

	public void send_string(String str)
	{
		int len = str.length();

		for (int i = 0; i < len; i++) {
			char c = str.charAt(i);
			if (c >= 0x20 && c <= 0x7e)
				buffer.put_byte((byte)Keys.ascii_to_cmd[(int)c - 0x20]);
		}

		send_keys.keys_sync.offer(1);
	}
}
