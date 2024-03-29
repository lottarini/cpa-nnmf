package util;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.WritableComparable;

public class NMFVector implements WritableComparable<NMFVector> {

	private static int elementsNumber = 0;
	protected double[] value;

	public static void setElementsNumber(int value) {
		elementsNumber = value;
	}

	public void setArray(double[] a) {
		this.value = a;
	}

	public NMFVector(int element_number, double[] elements) {
		//this.elementsNumber = element_number;
		this.value = elements;
	}

	public NMFVector(Text s) throws IOException {
		parseLine(s.toString(), this);
	}

	@SuppressWarnings("empty-statement")
	public NMFVector() {
		;
	}

	static public NMFVector parseLine(String s) throws IOException {
		NMFVector mv = new NMFVector();
		parseLine(s, mv);
		return mv;

	}
	// vector format : numbe_of_elements#elem1#elem2#elem3....
	// a vector (row or column) per line
	// no new line at the end of a file

	static private void parseLine(String s, NMFVector mv) throws IOException {

		if (elementsNumber == 0) {
			throw new IOException("Fail read fields: maybe you did not setup the k parameter of this NMFVector instance");
		}

		try {

			mv.value = new double[elementsNumber];

			String[] splitted = s.split("#");

			for (int i = 0; i < NMFVector.elementsNumber && i <= splitted.length; i++) {
				mv.value[i] = new Double(splitted[i]);

			}

		} catch (NumberFormatException e) {
			System.err.println("Input Error reading NMFVector Value" + s);
			mv.value = null;
			throw new IOException();
		}
	}

	public int getNumberOfElement() {
		return NMFVector.elementsNumber;
	}

	public double[] getValues() {
		return this.value;
	}

	public NMFMatrix externalProduct(NMFVector v) // Tensor Product
	{
		int size = this.getNumberOfElement();
		if (this.getNumberOfElement() != v.getNumberOfElement()) {
			return null;
		}

		double[][] tmp = new double[size][size];
		double[] vect1 = this.getValues();
		double[] vect2 = v.getValues();

		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				tmp[i][j] = vect1[i] * vect2[j];
			}
		}

		return new NMFMatrix(size, size, tmp);
	}

	public double internalProduct(NMFVector v) {
		/*if (this.elementsNumber != v.elementsNumber) {
			return 0;
		} now is a static variable */
		int ret = 0;
		for (int i = 0; i < NMFVector.elementsNumber; i++) {
			ret += (this.value[i] * v.value[i]);
		}
		return ret;
	}

	public NMFVector ScalarProduct(double value) {
		double[] doubleTmp = this.value.clone();

		for (int i = 0; i < NMFVector.elementsNumber; i++) {
			doubleTmp[i] = doubleTmp[i] * value;
		}

		return new NMFVector(this.getNumberOfElement(), doubleTmp);
	}

	@Override
	public void readFields(DataInput arg0) throws IOException {
		//this.elementsNumber = arg0.readInt();
		if (elementsNumber == 0) {
			throw new IOException("fail read fields: maybe you did not setup the k parameter of this NMFVector instance");
		}
		this.value = new double[NMFVector.elementsNumber];
		for (int i = 0; i < NMFVector.elementsNumber; i++) {
			this.value[i] = arg0.readDouble();
		}
	}

	@Override
	public void write(DataOutput arg0) throws IOException {
		//arg0.writeInt(this.elementsNumber);
		for (int i = 0; i < NMFVector.elementsNumber; i++) {
			arg0.writeDouble(this.value[i]);
		}
	}

	@Override
	public int compareTo(NMFVector o) {
		/* if (this.elementsNumber - o.elementsNumber != 0) {
			return this.elementsNumber - o.elementsNumber;
		} now is a static variable */
		/*
		int i;
		for(i=0; i<this.elementsNumber; i++)
		if(this.value[i] - o.value[i] != 0)
		return (int) (this.value[i] - o.value[i]);
		 */
		return 0;
	}

	@Override
	public String toString() {

		String tmp = "" + this.value[0];
		StringBuilder stringBuilder = new StringBuilder(tmp);

		for (int i = 1; i < NMFVector.elementsNumber; i++) {
			stringBuilder.append("#");
			stringBuilder.append(this.value[i]);
		}

		//stringBuilder.append('\n');
		return stringBuilder.toString();
	}

	public void inPlacePointDiv(NMFVector m) throws IOException {
		/* if (this.elementsNumber != m.elementsNumber) {
			throw new IOException();
		} now is a static variable */
		for (int i = 0; i < NMFVector.elementsNumber; i++) {

			this.value[i] /= m.value[i];

		}
	}

	public void inPlacePointMul(NMFVector m) throws IOException {
		/* if (this.elementsNumber != m.elementsNumber) {
			throw new IOException();
		} now is a static variable */
		for (int i = 0; i < NMFVector.elementsNumber; i++) {

			this.value[i] *= m.value[i];

		}
	}

	public void inPlaceSum(NMFVector m) throws IOException {
		/* if (this.elementsNumber != m.elementsNumber) {
			throw new IOException();
		} now is a static variable */
		for (int i = 0; i < NMFVector.elementsNumber; i++) {

			this.value[i] += m.value[i];

		}
	}
}
