
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.WritableComparable;

public class MatrixMatrix implements WritableComparable<MatrixMatrix> {

    private int rowNumber;
    private int columnNumber;
    private Double[][] value;

    public MatrixMatrix(int row_number, int column_number, Double[][] elements) {
        this.rowNumber = row_number;
        this.columnNumber = column_number;
        this.value = elements;
    }

    public MatrixMatrix(Text s) {
        parseLine(s.toString(), this);
    }

    public MatrixMatrix() {
        ;
    }

    static public MatrixMatrix parseLine(String s) {
        MatrixMatrix mv = new MatrixMatrix();
        parseLine(s, mv);
        return mv;

    }
    // vector format : numbe_of_elements#elem1#elem2#elem3....
    // a vector (row or column) per line
    // no new line at the end of a file

    static private void parseLine(String s, MatrixMatrix mv) {

        try {
            String[] splitted = s.split("\t");
            System.out.println("MatrixMatrix: stringa partizionata");

            String[] tmp = splitted[0].split("#");
            System.out.println("parsing a #");

            System.out.println("Conversione 1:<"+ tmp[0] +">");
            mv.rowNumber = Integer.parseInt(tmp[0]);

            System.out.println("Conversione 2:<"+ tmp[1] +">");
            mv.columnNumber = Integer.parseInt(tmp[1]);
            
            System.out.println("Sono nella parse Line e non so perche");
            
/*
            mv.value = new Double[mv.rowNumber][mv.columnNumber];
            System.out.println("Dimensione presa " + splitted.length);

            for (int row = 1; row < splitted.length && row - 1 < mv.rowNumber; row++) {
                System.out.println("SPLITTED ROW " + row + " " + splitted[row]);
                tmp = splitted[row].split("#");
                System.out.println("row partitioned in " + tmp.length + " values");
                for (int i = 0; i < mv.columnNumber && i <= splitted.length; i++) {
                    System.out.println("row " + i + "^ value = <" + tmp[i] + ">");
                    mv.value[row - 1][i] = new Double(tmp[i]);
                    System.out.println("Ho acquisito il " + i + "-esimo parametro");
                }
                System.out.println("Valori del vettore presi");
            }
            */
        } catch (NumberFormatException e) {
            System.out.println("Input Error reading MatrixMatrix Value <" + s+">");
            System.out.println(e.toString());
            mv.columnNumber = 0;
            mv.rowNumber = 0;
            mv.value = null;
        }
        
    }

    public int getRowNumber() {
        return this.rowNumber;
    }

    public int getColumnNumber() {
        return this.columnNumber;
    }

    public Double[][] getValues() {
        return this.value;
    }

    @Override
    public void readFields(DataInput arg0) throws IOException {
    	this.rowNumber = arg0.readInt();
    	this.columnNumber = arg0.readInt();
    	this.columnNumber = arg0.readInt();
        //String tmp = arg0.readLine();
        //parseLine(tmp);
    }

    @Override
    public void write(DataOutput arg0) throws IOException {
        
    	System.out.println("Sono nella write obj" + this.toString());
    	arg0.writeInt(7);
    	arg0.writeInt(8);
    	arg0.writeInt(12);

    	//arg0.writeChars(this.toString());
    }

    @Override
    public int compareTo(MatrixMatrix o) {
        if (this.rowNumber - o.rowNumber != 0) {
            return this.rowNumber - o.rowNumber;
        }

        if (this.columnNumber - o.columnNumber != 0) {
            return this.rowNumber - o.rowNumber;
        }

        /*
        for(int i=0; i<this.rowNumber; i++)
        for(int j=0; j<this.columnNumber; j++)
        if(this.value[i][j] - o.value[i][j] != 0)
        return (int) (this.value[i][j] - o.value[i][j]);
         */
        return 0;
    }

    public String toString() {
        String tmp = "" + this.rowNumber;
        StringBuilder stringBuilder = new StringBuilder(tmp);

        stringBuilder.append("#" + this.columnNumber);

        //stringBuilder.append('\t');
        
        /*
        for (int i = 0; i < this.rowNumber; i++) {
            stringBuilder.append(this.value[i][0]);
            for (int j = 1; j < this.columnNumber; j++) {
                stringBuilder.append("#" + this.value[i][j]);
            }

            if (i < this.rowNumber - 1) {
                stringBuilder.append('\t');
            }
        }*/
        stringBuilder.append('\n');
        
        System.out.println("Sono nella printf " + stringBuilder.toString());
        
        return stringBuilder.toString();
    }

    /** Implements sum of matrices
     *
     * @param m
     *      Matrix which has to be summed with this
     * @return
     *      null if dimensions of this and m do not agree
     *      MatrixMatrix of the sum otherwise
     */
    public MatrixMatrix sum(MatrixMatrix m){
        if(this.rowNumber != m.rowNumber || this.columnNumber !=  m.columnNumber) return null;
        MatrixMatrix ret = new MatrixMatrix(this.rowNumber, this.columnNumber, value);
        for(int i = 0 ;i<this.rowNumber;i++){
            for (int j = 0; i<this.columnNumber;j++){
                ret.value[i][j] += m.value[i][j];
            }
        }
        return ret;
    }

    public MatrixVector vectorMul (MatrixVector v){
        MatrixVector ret = new MatrixVector(this.rowNumber,new Double[rowNumber]);
        for (int i =0;i<this.rowNumber;i++){
            ret.value[i] = this.getRowVector(i).internalProduct(v);
        }
        return ret;
    }

    private Double[] getRowVector(int i) {
		// TODO Auto-generated method stub
		return this.value[i];
	}

	public void inPlacePointMul(MatrixMatrix m) throws IOException{
        if(this.rowNumber != m.rowNumber || this.columnNumber !=  m.columnNumber)
            throw new IOException();
        for (int i =0;i<this.rowNumber;i++){
            for (int j=0; j<this.columnNumber;j++){
                this.value[i][j] *= m.value[i][j];
            }
        }
    }
    
    public void inPlacePointDiv(MatrixMatrix m) throws IOException{
        if(this.rowNumber != m.rowNumber || this.columnNumber !=  m.columnNumber)
            throw new IOException();
        for (int i =0;i<this.rowNumber;i++){
            for (int j=0; j<this.columnNumber;j++){
                this.value[i][j] /= m.value[i][j];
            }
        }
    }
}
