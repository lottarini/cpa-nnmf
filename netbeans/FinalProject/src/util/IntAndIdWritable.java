package util;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.WritableComparator;

public class IntAndIdWritable extends IntWritable {

	private char id;

	public IntAndIdWritable() {
		super();
	}

	public IntAndIdWritable(int intValue, char charValue) {
		super(intValue);
		this.id = charValue;
	}

	public IntAndIdWritable(String intValue, char charValue) {
		super(Integer.parseInt(intValue));
		this.id = charValue;
	}

	public void set(int intValue, char charValue) {
		this.set(intValue);
		this.id = charValue;
	}

	@Override
	public void readFields(DataInput in) throws IOException {
		super.readFields(in);
		this.id = in.readChar();
	}

	@Override
	public void write(DataOutput out) throws IOException {
		super.write(out);
		out.writeChar(id);

	}

	public char getId() {
		return this.id;
	}

	/** Returns true iff <code>o</code> is a IntWritable with the same value. */
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof IntWritable)) {
			return false;
		}
		IntWritable otherObject = (IntWritable) o;
		return this.get() == otherObject.get();
	}

	//Compares two IntWritables.
	@Override
	public int compareTo(Object o) {
		//System.out.println("IntAndIdWritable. Sono nella compareTo: "+this.toString() + " "+o.toString());
		int compare_value = super.compareTo(o);

		return (compare_value == 0) ? this.id - ((IntAndIdWritable) o).id : compare_value;
	}

	@Override
	public String toString() {
		return super.toString() + "-" + this.id;
	}

	/** A Comparator optimized for IntWritable. */
	public static class Comparator extends WritableComparator {

		public Comparator() {
			super(IntAndIdWritable.class);
		}

		public int compare(byte[] b1, int s1, int l1,
				byte[] b2, int s2, int l2) {

			int thisValue = readInt(b1, s1);
			int thatValue = readInt(b2, s2);

			// QUI i char sono in UTF quindi occupano 2 bytes
			int confrontoChar = compareBytes(b1, s1 + l1 - 2, 2, b2, s2 + l2 - 2, 2);
			return (thisValue < thatValue ? -1 : (thisValue == thatValue ? confrontoChar : 1));
		}
	}

	static {                                        // register this comparator
		WritableComparator.define(IntAndIdWritable.class, new Comparator());
	}
}
