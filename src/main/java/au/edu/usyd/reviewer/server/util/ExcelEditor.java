package au.edu.usyd.reviewer.server.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

public class ExcelEditor {

	private WritableWorkbook wwb = null;
	private WritableSheet ws = null;
	private Workbook w;
	private ArrayList<String> Column1data = new ArrayList<String>();
	private ArrayList<String> Column2data = new ArrayList<String>();
	private ArrayList<String> Column3data = new ArrayList<String>();
	private ArrayList<String> Column4data = new ArrayList<String>();

	public ExcelEditor() {

	}

	public ExcelEditor(String filepath) {
		try {
			wwb = Workbook.createWorkbook(new File(filepath));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ws = wwb.createSheet("Test Sheet 1", 0);
	}

	public void addelementinColumn0(ArrayList<String> cells) {
		Label labelC = null;
		try {

			for (int i = 0; i < cells.size(); i++) {
				labelC = new jxl.write.Label(0, i, cells.get(i));
				ws.addCell(labelC);
			}

		} catch (RowsExceededException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (WriteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void addelementinColumn1(ArrayList<String> cells) {
		Label labelC = null;
		try {
			for (int i = 0; i < cells.size(); i++) {
				labelC = new jxl.write.Label(1, i, cells.get(i));
				ws.addCell(labelC);
			}

		} catch (RowsExceededException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (WriteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void addelementinColumn2(ArrayList<String> cells) {
		Label labelC = null;
		try {
			for (int i = 0; i < cells.size(); i++) {
				labelC = new jxl.write.Label(2, i, cells.get(i));
				ws.addCell(labelC);
			}

		} catch (RowsExceededException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (WriteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void addelementinColumn3(ArrayList<String> cells) {
		Label labelC = null;
		try {
			for (int i = 0; i < cells.size(); i++) {
				labelC = new jxl.write.Label(3, i, cells.get(i));
				ws.addCell(labelC);
			}

		} catch (RowsExceededException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (WriteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void addelementinColumn4(ArrayList<String> cells) {
		Label labelC = null;
		try {
			for (int i = 0; i < cells.size(); i++) {
				labelC = new jxl.write.Label(4, i, cells.get(i));
				ws.addCell(labelC);
			}

		} catch (RowsExceededException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (WriteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void addelementinColumn5(ArrayList<String> cells) {
		Label labelC = null;
		try {
			for (int i = 0; i < cells.size(); i++) {
				labelC = new jxl.write.Label(5, i, cells.get(i));
				ws.addCell(labelC);
			}

		} catch (RowsExceededException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (WriteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void addelementinColumn6(ArrayList<String> cells) {
		// TODO Auto-generated method stub
		Label labelC = null;
		try {
			for (int i = 0; i < cells.size(); i++) {
				labelC = new jxl.write.Label(6, i, cells.get(i));
				ws.addCell(labelC);
			}

		} catch (RowsExceededException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (WriteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void addelementinColumn7(ArrayList<String> nativespeakerlist) {
		// TODO Auto-generated method stub
		Label labelC = null;
		try {
			for (int i = 0; i < nativespeakerlist.size(); i++) {
				labelC = new jxl.write.Label(7, i, nativespeakerlist.get(i));
				ws.addCell(labelC);
			}

		} catch (RowsExceededException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (WriteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void commit() {
		try {
			wwb.write();
			wwb.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (WriteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public ArrayList<String> getColumn1data() {
		return Column1data;
	}

	public ArrayList<String> getColumn2data() {
		return Column2data;
	}

	public ArrayList<String> getColumn3data() {
		return Column3data;
	}

	public ArrayList<String> getColumn4data() {
		// TODO Auto-generated method stub

		return Column4data;
	}

	public void read(String excelfile) throws BiffException, IOException {

		w = Workbook.getWorkbook(new File(excelfile));
		// Get the first sheet
		Sheet sheet = w.getSheet(0);
		for (int j = 0; j < sheet.getColumns(); j++) {
			for (int i = 0; i < sheet.getRows(); i++) {
				Cell cell = sheet.getCell(j, i);
				if (j == 0) {
					if (!cell.getContents().trim().equals(""))
						Column1data.add(cell.getContents());
				} else if (j == 1) {
					if (!cell.getContents().trim().equals(""))
						Column2data.add(cell.getContents());
				} else if (j == 2) {
					if (!cell.getContents().trim().equals(""))
						Column3data.add(cell.getContents());
				} else {
					if (!cell.getContents().trim().equals(""))
						Column4data.add(cell.getContents());
				}

			}
		}

	}

	public void setColumn1data(ArrayList<String> column1data) {
		Column1data = column1data;
	}

	public void setColumn2data(ArrayList<String> column2data) {
		Column2data = column2data;
	}

	public void setColumn3data(ArrayList<String> column3data) {
		Column3data = column3data;
	}

	public void setColumn4data(ArrayList<String> column4data) {
		Column4data = column4data;
	}

}
