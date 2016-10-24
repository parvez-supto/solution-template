package com.tigerit.exam;


import static com.tigerit.exam.IO.*;
import java.io.*;
import java.util.*;

/**
 * All of your application logic should be placed inside this class.
 * Remember we will load your application from our custom container.
 * You may add private method inside this class but, make sure your
 * application's execution points start from inside run method.
 */
class Table {

    String name;
    String columnName[];
    int data[][];
    int row, column;

    Table() {
    }

    Table(String name) {
        columnName = new String[105];
        data = new int[105][105];
        row = column = 0;
        this.name = name;
    }

    void setColumn(int column) {
        this.column = column;
    }

    void setColumnNames(String names) {
        String tokens[] = names.split("[ ]+");
        for (int i = 0; i < column; i++) {
            columnName[i] = tokens[i];
        }
    }

    void addColumnName(String name) {
        columnName[column] = name;
        column++;
    }

    void addRow(String values) {
        String tokens[] = values.split("[ ]+");
        for (int i = 0; i < column; i++) {
            data[row][i] = Integer.parseInt(tokens[i]);
        }
        row++;
    }
}

public class Solution implements Runnable {
    private static final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    static Table tables[] = new Table[12];
    @Override
    public void run() {
        int ncase = readInt(readString());
        for (int tcase = 1; tcase <= ncase; tcase++) {
            int nT = readInt(readString());
            for (int tablei = 0; tablei < nT; tablei++) {
                String name = readString();
                tables[tablei] = new Table(name);
                String tokens[] = readString().split("[ ]+");
                int nC = readInt(tokens[0]);
                tables[tablei].setColumn(nC);
                int nD = readInt(tokens[1]);
                tables[tablei].setColumnNames(readString());
                for (int rowi = 0; rowi < nD; rowi++) {
                    String values = readString();
                    tables[tablei].addRow(values);
                }
            }
            int nQ = readInt(readString());
            System.out.println("Test: " + tcase);
            for (int queryi = 0; queryi < nQ; queryi++) {
                String line1 = readString();
                String line2 = readString();
                String line3 = readString();
                String line4 = readString();
                processQuery(nT, line1, line2, line3, line4);
                readString();
            }
        }
    }

    public static String readString() {
        String tmp;
        try {
            tmp = reader.readLine();
        } catch (IOException ex) {
            tmp = null;
        }
        return tmp;
    }

    public static int readInt(String str) {
        return Integer.parseInt(str);
    }

    static int findTableIndex(int nT, String name) {
        for (int i = 0; i < nT; i++) {
            if (tables[i].name.equals(name)) {
                return i;
            }
        }
        return -1;
    }

    static int findColumnIndex(int idx, String name) {
        for (int i = 0; i < tables[idx].column; i++) {
            if (tables[idx].columnName[i].equals(name)) {
                return i;
            }
        }
        return -1;
    }

    static void processQuery(int nT, String line1, String line2, String line3, String line4) {
        Table result = new Table("Answer");
        int indexColumns[] = new int[205];
        int indexRows[] = new int[20005];
        int idxr = 0, idxc = 0;
        String tokens1[] = line2.split("[ ]+");
        String tokens2[] = line3.split("[ ]+");
        int shortFlag = 0;
        String shortName1 = null, shortName2 = null;
        if (tokens1.length == 3) {
            shortFlag = 1;
            shortName1 = tokens1[2];
            shortName2 = tokens2[2];
        }
        int index[] = new int[2];
        index[0] = findTableIndex(nT, tokens1[1]);
        index[1] = findTableIndex(nT, tokens2[1]);
        String tokens3[] = line1.split("[ ,]+");
        if (tokens3[1].equals("*")) {
            for (int i = 0; i < 2; i++) {
                for (int j = 0; j < tables[index[i]].column; j++) {
                    indexColumns[idxc++] = i;
                    indexColumns[idxc++] = j;
                    result.addColumnName(tables[index[i]].columnName[j]);
                }
            }
        } else {
            int size = tokens3.length;
            for (int i = 1; i < size; i++) {
                String tokens[] = tokens3[i].split("\\.");
                if (tokens[0].equals(shortName1)) {
                    int idx = findColumnIndex(index[0], tokens[1]);
                    indexColumns[idxc++] = 0;
                    indexColumns[idxc++] = idx;
                } else {
                    int idx = findColumnIndex(index[1], tokens[1]);
                    indexColumns[idxc++] = 1;
                    indexColumns[idxc++] = idx;
                }
                result.addColumnName(tokens[1]);
            }
        }
        int columns[] = new int[2];
        String tokens4[] = line4.split("[ ]+");
        String tokens41[] = tokens4[1].split("\\.");
        String tokens42[] = tokens4[3].split("\\.");
        columns[0] = findColumnIndex(index[0], tokens41[1]);
        columns[1] = findColumnIndex(index[1], tokens42[1]);
        for (int row1 = 0; row1 < tables[index[0]].row; row1++) {
            int value1 = tables[index[0]].data[row1][columns[0]];
            for (int row2 = 0; row2 < tables[index[1]].row; row2++) {
                int value2 = tables[index[1]].data[row2][columns[1]];
                if (value1 == value2) {
                    indexRows[idxr++] = row1;
                    indexRows[idxr++] = row2;
                }
            }
        }
        int sizer = (idxr+1)/2, sizec = result.column;
        int answer[][] = new int[sizer][sizec];
        for (int i = 0, r = 0; i < idxr; i += 2, r++) {
            int rows[] = new int[2];
            rows[0] = indexRows[i];
            rows[1] = indexRows[i + 1];
            String tmp = "";
            for (int j = 0, c = 0; j < idxc; j += 2, c++) {
                int t = indexColumns[j];
                int cc = indexColumns[j + 1];
                answer[r][c] = tables[index[t]].data[rows[t]][cc];
            }
        }
        Arrays.sort(answer, new Comparator<int[]>() {
            @Override
            public int compare(int[] a, int[] b) {
                for(int i = 0; i < sizec; i++) {
                    if(a[i] != b[i]) return Integer.compare(a[i], b[i]);
                }
                return 1;
            }
        });
        for (int i = 0; i < sizec; i++) {
            if (i > 0) {
                System.out.print(" ");
            }
            System.out.print(result.columnName[i]);
        }
        System.out.print("\n");
        for (int i = 0; i < sizer; i++) {
            for (int j = 0; j < sizec; j++) {
                if (j > 0) {
                    System.out.print(" ");
                }
                System.out.print(answer[i][j]);
            }
            System.out.print("\n");
        }
        System.out.print("\n");
    }
}
