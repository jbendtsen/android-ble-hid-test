package com.example.hidtest;

import java.io.*;
import java.util.*;

public class CircularBuffer
{
	public static final int DEFAULT_CAP = 1024;

	public byte[] data;
	int read_head;
	int write_head;

	public CircularBuffer() {
		init(DEFAULT_CAP);
	}
	public CircularBuffer(int cap) {
		init(cap);
	}

	private void init(int cap)
	{
		if (cap <= 0)
			cap = DEFAULT_CAP;

		cap--;
		int bit_width = 0;
		while (cap != 0) {
			cap >>= 1;
			bit_width++;
		}

		cap = 1 << bit_width;

		data = new byte[cap];
		read_head = 0;
		write_head = 0;
	}

	public void put_byte(byte b)
	{
		if (write_head >= data.length)
			write_head = 0;

		data[write_head++] = b;
	}

	public byte get_byte()
	{
		if (read_head >= data.length)
			read_head = 0;

		return data[read_head++];
	}

	public int fresh_size() {
		return (write_head - read_head + data.length) % data.length;
	}
}
