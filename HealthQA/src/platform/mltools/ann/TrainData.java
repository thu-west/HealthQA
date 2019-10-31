package platform.mltools.ann;

import java.io.*;

import org.neuroph.core.data.DataSet;
import org.neuroph.core.data.DataSetRow;

public class TrainData {
	/*
	 * Data Line Format:
	 * 3000 5     // 首行：输入维数 输出维数
	 * 1 3\t1:0.2 5:0.1 6:0 1000:1 // 分类 分类\t词汇索引:值 词汇索引:值
	 * 1\t3:0.2 5:0.1 6:0 990:0.1 2019:0.5
	 * 2 3 5\t3:0.2 5:0.1 6:0 1000:1
	 */
	public static DataSet read(String filename) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(
				new FileInputStream(filename), "utf8"));
		String line = null;
		line = br.readLine();
		String[] temp = line.split(" ");
		int input_dim = Integer.parseInt(temp[0]);
		int output_dim = Integer.parseInt(temp[1]);
		DataSet ds = new DataSet(input_dim, output_dim);
		while( (line=br.readLine()) != null ) {
			String[] ts = line.split("\t");
			String[] value = ts[1].split(" ");
			double[] i_vector = new double[input_dim];
			for(String v: value) {
				temp = v.split(":");
				i_vector[Integer.parseInt(temp[0])-1]=Double.parseDouble(temp[1]);
			}
			double[] o_vector = new double[output_dim];
			value = ts[0].split(" ");
			for(String v: value) {
				o_vector[Integer.parseInt(v)-1] = 1;
			}
			ds.addRow(new DataSetRow(i_vector, o_vector));
		}
		br.close();
		return ds;
	}

}
