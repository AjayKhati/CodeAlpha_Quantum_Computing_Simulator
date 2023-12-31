package com.ajkhati.quantumcomputingsimulator.math;

import android.graphics.RectF;
import android.os.Build;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.stream.IntStream;

import androidx.annotation.NonNull;

/**
 * Class used to represent a Linear Operator for quantum systems, and as such
 * it has some related limitations and additional features to a mathematical linear operator
 */
public class VisualOperator {

    public static final long helpVersion = 52L;
    private Complex[][] matrix;
    //last one is to clarify meaning for navigation drawer, so length is qubits+1 if qubits > 1
    private String[] symbols;
    private Random random;

    /**
     * Visual Quantum Gate
     */
    public static final String FILE_EXTENSION_LEGACY = ".vqg";
    /**
     * Quantum Gate File
     */
    public static final String FILE_EXTENSION = ".qgf";
    public static final String HERMITIAN_CONJUGATE_SYMBOL = "†";
    private final int NQBITS;
    public int color = 0xff000000;
    public String name;
    private LinkedList<RectF> rectangle;
    private final int MATRIX_DIM;
    private int[] qubit_ids;
    private double theta;
    private double phi;
    private double lambda;

    public static final int HTML_MODE_BODY = 0b1;
    public static final int HTML_MODE_CAPTION = 0b10;
    public static final int HTML_MODE_FAT = 0b100;
    public static final int HTML_MODE_BASIC = 0b0;

    private static final double NULL_ANGLE = -10E10;

    public static final VisualOperator CNOT =
            new VisualOperator(4,
                    new Complex[][]{
                            {new Complex(1), new Complex(0), new Complex(0), new Complex(0)},
                            {new Complex(0), new Complex(1), new Complex(0), new Complex(0)},
                            {new Complex(0), new Complex(0), new Complex(0), new Complex(1)},
                            {new Complex(0), new Complex(0), new Complex(1), new Complex(0)}
                    }, "CNOT", new String[]{"●", "＋", "cX"}, 0xff009E5F);

    public static final VisualOperator CY =
            new VisualOperator(4,
                    new Complex[][]{
                            {new Complex(1), new Complex(0), new Complex(0), new Complex(0)},
                            {new Complex(0), new Complex(1), new Complex(0), new Complex(0)},
                            {new Complex(0), new Complex(0), new Complex(0), new Complex(0, -1)},
                            {new Complex(0), new Complex(0), new Complex(0, 1), new Complex(0)}
                    }, "CY", new String[]{"●", "Y", "cY"}, 0xff009E5F);

    public static final VisualOperator CZ =
            new VisualOperator(4,
                    new Complex[][]{
                            {new Complex(1), new Complex(0), new Complex(0), new Complex(0)},
                            {new Complex(0), new Complex(1), new Complex(0), new Complex(0)},
                            {new Complex(0), new Complex(0), new Complex(1), new Complex(0)},
                            {new Complex(0), new Complex(0), new Complex(0), new Complex(-1)}
                    }, "CZ", new String[]{"●", "Z", "cZ"}, 0xff009E5F);

    public static final VisualOperator SWAP =
            new VisualOperator(4,
                    new Complex[][]{
                            {new Complex(1), new Complex(0), new Complex(0), new Complex(0)},
                            {new Complex(0), new Complex(0), new Complex(1), new Complex(0)},
                            {new Complex(0), new Complex(1), new Complex(0), new Complex(0)},
                            {new Complex(0), new Complex(0), new Complex(0), new Complex(1)}
                    }, "SWAP", new String[]{"✖", "✖", "SWAP"}, 0xffF28B00);

    public static final VisualOperator CS =
            new VisualOperator(4,
                    new Complex[][]{
                            {new Complex(1), new Complex(0), new Complex(0), new Complex(0)},
                            {new Complex(0), new Complex(1), new Complex(0), new Complex(0)},
                            {new Complex(0), new Complex(0), new Complex(1), new Complex(0)},
                            {new Complex(0), new Complex(0), new Complex(0), new Complex(Math.PI / 2)}
                    }, "Controlled π/2 shift", new String[]{"●", "S", "cS"}, 0xff21BAAB);

    public static final VisualOperator CT =
            new VisualOperator(4,
                    new Complex[][]{
                            {new Complex(1), new Complex(0), new Complex(0), new Complex(0)},
                            {new Complex(0), new Complex(1), new Complex(0), new Complex(0)},
                            {new Complex(0), new Complex(0), new Complex(1), new Complex(0)},
                            {new Complex(0), new Complex(0), new Complex(0), new Complex(Math.PI / 4)}
                    }, "Controlled π/4 shift", new String[]{"●", "T", "cT"}, 0xffBA7021);

    public static final VisualOperator CH =
            new VisualOperator(4,
                    new Complex[][]{
                            {new Complex(1), new Complex(0), new Complex(0), new Complex(0)},
                            {new Complex(0), new Complex(1), new Complex(0), new Complex(0)},
                            {new Complex(0), new Complex(0), new Complex(1.0 / Math.sqrt(2), 0), new Complex(1.0 / Math.sqrt(2), 0)},
                            {new Complex(0), new Complex(0), new Complex(1.0 / Math.sqrt(2), 0), new Complex(-1.0 / Math.sqrt(2), 0)}
                    }, "Controlled Hadamard", new String[]{"●", "H", "cH"}, 0xff2155BA);

    public static final VisualOperator TOFFOLI =
            new VisualOperator(8,
                    new Complex[][]{
                            {new Complex(1), new Complex(0), new Complex(0), new Complex(0), new Complex(0), new Complex(0), new Complex(0), new Complex(0)},
                            {new Complex(0), new Complex(1), new Complex(0), new Complex(0), new Complex(0), new Complex(0), new Complex(0), new Complex(0)},
                            {new Complex(0), new Complex(0), new Complex(1), new Complex(0), new Complex(0), new Complex(0), new Complex(0), new Complex(0)},
                            {new Complex(0), new Complex(0), new Complex(0), new Complex(1), new Complex(0), new Complex(0), new Complex(0), new Complex(0)},
                            {new Complex(0), new Complex(0), new Complex(0), new Complex(0), new Complex(1), new Complex(0), new Complex(0), new Complex(0)},
                            {new Complex(0), new Complex(0), new Complex(0), new Complex(0), new Complex(0), new Complex(1), new Complex(0), new Complex(0)},
                            {new Complex(0), new Complex(0), new Complex(0), new Complex(0), new Complex(0), new Complex(0), new Complex(0), new Complex(1)},
                            {new Complex(0), new Complex(0), new Complex(0), new Complex(0), new Complex(0), new Complex(0), new Complex(1), new Complex(0)}
                    }, "Toffoli", new String[]{"●", "●", "＋", "TOF"}, 0xff9200D1);

    public static final VisualOperator FREDKIN =
            new VisualOperator(8,
                    new Complex[][]{
                            {new Complex(1), new Complex(0), new Complex(0), new Complex(0), new Complex(0), new Complex(0), new Complex(0), new Complex(0)},
                            {new Complex(0), new Complex(1), new Complex(0), new Complex(0), new Complex(0), new Complex(0), new Complex(0), new Complex(0)},
                            {new Complex(0), new Complex(0), new Complex(1), new Complex(0), new Complex(0), new Complex(0), new Complex(0), new Complex(0)},
                            {new Complex(0), new Complex(0), new Complex(0), new Complex(1), new Complex(0), new Complex(0), new Complex(0), new Complex(0)},
                            {new Complex(0), new Complex(0), new Complex(0), new Complex(0), new Complex(1), new Complex(0), new Complex(0), new Complex(0)},
                            {new Complex(0), new Complex(0), new Complex(0), new Complex(0), new Complex(0), new Complex(0), new Complex(1), new Complex(0)},
                            {new Complex(0), new Complex(0), new Complex(0), new Complex(0), new Complex(0), new Complex(1), new Complex(0), new Complex(0)},
                            {new Complex(0), new Complex(0), new Complex(0), new Complex(0), new Complex(0), new Complex(0), new Complex(0), new Complex(1)}
                    }, "Fredkin", new String[]{"●", "✖", "✖", "FRE"}, 0xffD10075);

    public static final VisualOperator HADAMARD =
            VisualOperator.multiply(
                    new VisualOperator(2, new Complex[][]{
                            new Complex[]{new Complex(1), new Complex(1)},
                            new Complex[]{new Complex(1), new Complex(-1)}
                    }, "Hadamard", new String[]{"H"}, 0xff2155BA), new Complex(1 / Math.sqrt(2), 0));

    public static final VisualOperator PAULI_Z =
            new VisualOperator(2, new Complex[][]{
                    new Complex[]{new Complex(1), new Complex(0)},
                    new Complex[]{new Complex(0), new Complex(-1)}
            }, "Pauli-Z", new String[]{"Z"}, 0xff60BA21);

    public static final VisualOperator PAULI_Y =
            new VisualOperator(2, new Complex[][]{
                    new Complex[]{new Complex(0), new Complex(0, -1)},
                    new Complex[]{new Complex(0, 1), new Complex(0)}
            }, "Pauli-Y", new String[]{"Y"}, 0xff60BA21);

    public static final VisualOperator PAULI_X =
            new VisualOperator(2, new Complex[][]{
                    new Complex[]{new Complex(0), new Complex(1)},
                    new Complex[]{new Complex(1), new Complex(0)}
            }, "Pauli-X", new String[]{"X"}, 0xff60BA21);

    public static final VisualOperator T_GATE =
            new VisualOperator(2, new Complex[][]{
                    new Complex[]{new Complex(1), new Complex(0)},
                    new Complex[]{new Complex(0), new Complex(Math.PI / 4)}
            }, "π/4 Phase-shift", new String[]{"T"}, 0xffBA7021);

    public static final VisualOperator S_GATE =
            new VisualOperator(2, new Complex[][]{
                    new Complex[]{new Complex(1), new Complex(0)},
                    new Complex[]{new Complex(0), new Complex(0, 1)}
            }, "π/2 Phase-shift", new String[]{"S"}, 0xff21BAAB);

    public static final VisualOperator SQRT_NOT =
            VisualOperator.multiply(new VisualOperator(2, new Complex[][]{
                    new Complex[]{new Complex(1, 1), new Complex(1, -1)},
                    new Complex[]{new Complex(1, -1), new Complex(1, 1)}
            }, "√NOT", new String[]{"√X"}, 0xff2155BA), new Complex(0.5, 0));

    public static final VisualOperator ID =
            new VisualOperator(2, new Complex[][]{
                    new Complex[]{new Complex(1), new Complex(0)},
                    new Complex[]{new Complex(0), new Complex(1)}
            }, "Identity", new String[]{"I"}, 0xff666666);

    public VisualOperator(int DIM, Complex[][] M, String name, String[] symbols, int color) {
        rectangle = new LinkedList<>();
        this.MATRIX_DIM = DIM;
        if (M == null) {
            throw new NullPointerException();
        }
        this.color = color;
        switch (DIM) {
            case 2:
                NQBITS = 1;
                break;
            case 4:
                NQBITS = 2;
                break;
            case 8:
                NQBITS = 3;
                break;
            case 16:
                NQBITS = 4;
                break;
            case 32:
                NQBITS = 5;
                break;
            case 64:
                NQBITS = 6;
                break;
            default:
                throw new NullPointerException("Invalid dimension");
        }
        for (int i = 0; i < DIM; i++) {
            if (!(i < M.length && M[i].length == DIM)) {
                throw new NullPointerException("Invalid array");
            }
        }
        if (symbols.length < NQBITS || symbols.length > NQBITS + 1 || (symbols.length == 2 && NQBITS == 1)) {
            throw new NullPointerException("Invalid symbol");
        }
        this.name = name;
        this.symbols = symbols.clone();
        matrix = M;

        random = new Random();
        qubit_ids = new int[NQBITS];
        theta = phi = lambda = NULL_ANGLE;
    }

    public VisualOperator(int MATRIX_DIM, Complex[][] M) {
        this(MATRIX_DIM, M, "Custom", VisualOperator.generateSymbols(MATRIX_DIM), 0xff000000);
        random = new Random();
        theta = phi = lambda = NULL_ANGLE;
    }

    public VisualOperator() {
        random = new Random();
        qubit_ids = new int[NQBITS = 2];
        rectangle = new LinkedList<>();
        MATRIX_DIM = 4;
        name = "";
        theta = phi = lambda = NULL_ANGLE;
    }

    public VisualOperator(double theta, double phi, double lambda, boolean controlled) {
        matrix = new Complex[][]{
                new Complex[]{new Complex(Math.cos(theta / 2), 0), Complex.multiply(new Complex(lambda), new Complex(-Math.sin(theta / 2), 0))},
                new Complex[]{Complex.multiply(new Complex(phi), new Complex(Math.sin(theta / 2), 0)), Complex.multiply(new Complex(lambda + phi), new Complex(Math.cos(theta / 2), 0))}
        };
        if (controlled) {
            Complex[][] dim_4_identity = tensorProduct(ID.matrix, ID.matrix);
            dim_4_identity[2][2] = matrix[0][0];
            dim_4_identity[2][3] = matrix[0][1];
            dim_4_identity[3][2] = matrix[1][0];
            dim_4_identity[3][3] = matrix[1][1];
            matrix = dim_4_identity;
        }
        MATRIX_DIM = controlled ? 4 : 2;
        qubit_ids = new int[NQBITS = controlled ? 2 : 1];
        rectangle = new LinkedList<>();
        color = 0xFFD12000;
        symbols = controlled ? new String[]{"●", "U3"} : new String[]{"U3"};
        name = controlled ? "cU3" : "U3";
        this.theta = theta;
        this.phi = phi;
        this.lambda = lambda;
    }

    //QFT
    public VisualOperator(int qubits, boolean inverse) {
        if (qubits < 2 || qubits > 6) {
            throw new IllegalArgumentException("Invalid value for qubits: " + qubits);
        }
        MATRIX_DIM = 1 << qubits;
        name = "QFT";
        qubit_ids = new int[NQBITS = qubits];
        rectangle = new LinkedList<>();
        color = 0xffbce500;
        this.lambda = inverse ? -1 : 1;
        this.theta = NULL_ANGLE;
        this.phi = NULL_ANGLE;
        Complex complexOmega = new Complex(1, Math.PI * 2 / MATRIX_DIM, true);
        matrix = generateQFTMatrix(complexOmega, inverse);
        symbols = new String[qubits + 1];
        for (int i = 1; i <= qubits; i++) {
            symbols[i - 1] = "QF" + i;
        }
        symbols[qubits] = "QFT";
    }

    public String[] getSymbols() {
        return symbols;
    }

    public boolean setSymbols(String[] symbols) {
        if (symbols.length == NQBITS) {
            this.symbols = symbols;
            return true;
        } else if (symbols.length == NQBITS + 1 && !(symbols.length == 2 && NQBITS == 1)) {
            this.symbols = symbols;
            return true;
        } else {
            return false;
        }
    }

    public void conjugate() {
        for (Complex[] ca : matrix) {
            for (Complex z : ca) {
                z.conjugate();
            }
        }
    }

    public static VisualOperator conjugate(VisualOperator visualOperator) {
        VisualOperator l = visualOperator.copy();
        for (Complex[] ca : l.matrix) {
            for (Complex z : ca) {
                z.conjugate();
            }
        }
        return l;
    }

    public void transpose() {
        Complex[][] tmp = new Complex[MATRIX_DIM][MATRIX_DIM];
        for (int i = 0; i < MATRIX_DIM; i++) {
            for (int j = 0; j < MATRIX_DIM; j++) {
                tmp[i][j] = matrix[j][i];
            }
        }
        matrix = tmp;
    }

    public static VisualOperator transpose(VisualOperator visualOperator) {
        VisualOperator t = visualOperator.copy();
        t.transpose();
        return t;
    }

    public void hermitianConjugate() {
        transpose();
        conjugate();
        for (int i = 0; i < symbols.length; i++) {
            if (!symbols[i].equals(CNOT.symbols[0]))
                symbols[i] += HERMITIAN_CONJUGATE_SYMBOL;
        }
        if (theta != NULL_ANGLE) {
            theta = -theta;
        }
        if (phi != NULL_ANGLE) {
            phi = -phi;
        }
        if (lambda != NULL_ANGLE) {
            lambda = -lambda;
        }
    }

    public static VisualOperator hermitianConjugate(VisualOperator visualOperator) {
        VisualOperator t = visualOperator.copy();
        t.hermitianConjugate();
        return t;
    }

    public boolean isHermitian() {
        for (int i = 0; i < MATRIX_DIM; i++) {
            for (int j = i + 1; j < MATRIX_DIM; j++) {
                if (!matrix[i][j].equals3Decimals(Complex.conjugate(matrix[j][i]))) {
                    return false;
                }
            }
        }
        return true;
    }

    public void multiply(Complex complex) {
        for (int i = 0; i < MATRIX_DIM; i++) {
            for (int j = 0; j < MATRIX_DIM; j++) {
                matrix[i][j].multiply(complex);
            }
        }
    }

    public static VisualOperator multiply(VisualOperator t, Complex complex) {
        VisualOperator visualOperator = t.copy();
        visualOperator.multiply(complex);
        return visualOperator;
    }

    public boolean isU3() {
        return lambda != NULL_ANGLE && theta != NULL_ANGLE && phi != NULL_ANGLE && !isMultiQubit() && name.equalsIgnoreCase("U3");
    }

    public boolean isCU3() {
        return lambda != NULL_ANGLE && theta != NULL_ANGLE && phi != NULL_ANGLE && isMultiQubit() && name.equalsIgnoreCase("cU3");
    }

    public boolean isQFT() {
        return (lambda == 1 || lambda == -1) && theta == NULL_ANGLE && phi == NULL_ANGLE && isMultiQubit() && name.equals("QFT");
    }

    public boolean isHermitianConjugate() {
        if ((isU3() || isCU3()) && (lambda < 0 || theta < 0 || phi < 0))
            return true;
        if (isQFT() && lambda == -1)
            return true;
        for (String symbol : symbols)
            if (symbol.endsWith(HERMITIAN_CONJUGATE_SYMBOL))
                return true;
        return false;
    }

    public double[] getAngles() {
        if (isU3()) {
            return new double[]{theta, phi, lambda};
        } else if (!isMultiQubit()) {
            VisualOperator operator = copy();
            if (!matrix[0][0].isReal()) {
                Log.d("Quantum VisOp", "Global phase conversion necessary...");
                operator.multiply(Complex.divide(new Complex(1, 0), new Complex(Math.atan(matrix[0][0].imaginary / matrix[0][0].real))));
            }
            Complex theta = Complex.multiply(new Complex(2, 0), Complex.acos(operator.matrix[0][0]));
            Complex lambda = Complex.divide(Complex.sub(Complex.log(new Complex(Math.E, 0), Complex.multiply(new Complex(-1, 0), operator.matrix[0][1])),
                    Complex.log(new Complex(Math.E, 0), Complex.sin(Complex.acos(operator.matrix[0][0])))), new Complex(0, 1));
            Complex phi = Complex.divide(Complex.sub(Complex.log(new Complex(Math.E, 0), operator.matrix[1][0]),
                    Complex.log(new Complex(Math.E, 0), Complex.sin(Complex.acos(operator.matrix[0][0])))), new Complex(0, 1));
            Complex phi2 = Complex.sub(Complex.divide(Complex.log(new Complex(Math.E, 0), operator.matrix[1][1]), Complex.multiply(operator.matrix[0][0], new Complex(0, 1))), lambda);
            Log.d("Quantum VisOp", "theta: " + theta.toString3Decimals() + ", lambda: " + lambda.toString3Decimals() + ", phi: " + phi.toString3Decimals() + ", phi2: " + phi2.toString3Decimals());
            return new double[]{theta.real, phi2.isReal() ? phi2.real : phi.real, lambda.real};
        } else {
            return new double[]{theta, phi, lambda};
        }
    }

    public VisualOperator copy() {
        Complex[][] complex = new Complex[MATRIX_DIM][MATRIX_DIM];
        for (int i = 0; i < MATRIX_DIM; i++) {
            complex[i] = new Complex[MATRIX_DIM];
            for (int j = 0; j < MATRIX_DIM; j++) {
                complex[i][j] = matrix[i][j].copy();
            }
        }
        String[] sym = new String[symbols.length];
        System.arraycopy(symbols, 0, sym, 0, symbols.length);
        VisualOperator v = new VisualOperator(MATRIX_DIM, complex, name, sym, color);
        v.theta = theta;
        v.phi = phi;
        v.lambda = lambda;
        v.qubit_ids = new int[qubit_ids.length];
        System.arraycopy(qubit_ids, 0, v.qubit_ids, 0, qubit_ids.length);
        return v;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Complex[] c : matrix) {
            for (Complex z : c) {
                sb.append(z.toString3Decimals());
                sb.append(", ");
            }
            sb.deleteCharAt(sb.length() - 1);
            sb.append('\n');
        }
        return sb.toString();
    }

    public String toString(int decimals) {
        StringBuilder sb = new StringBuilder();
        for (Complex[] c : matrix) {
            for (Complex z : c) {
                sb.append(z.toString(decimals));
                sb.append(",");
            }
            sb.deleteCharAt(sb.length() - 1);
            sb.append('\n');
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    public JSONObject toJSON() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name", name);
        jsonObject.put("matrix_dim", MATRIX_DIM);
        jsonObject.put("color", color);
        jsonObject.put("qubit_count", NQBITS);
        JSONArray qubits = new JSONArray();
        JSONArray symbols = new JSONArray();
        for (int i = 0; i < qubit_ids.length; i++) {
            qubits.put(qubit_ids[i]);
        }
        for (int i = 0; i < this.symbols.length; i++) {
            symbols.put(this.symbols[i]);
        }
        if (isU3()) {
            JSONObject angles = new JSONObject();
            angles.put("theta", theta);
            angles.put("phi", phi);
            angles.put("lambda", lambda);
            jsonObject.put("angles", angles);
        } else if (isQFT()) {
            JSONObject angles = new JSONObject();
            angles.put("omega", lambda);
            jsonObject.put("angles", angles);
        }
        jsonObject.put("qubits", qubits);
        jsonObject.put("symbols", symbols);
        for (int i = 0; i < matrix.length; i++) {
            JSONArray jsonArray = new JSONArray();
            for (int j = 0; j < matrix.length; j++) {
                jsonArray.put(matrix[i][j].toString());
            }
            jsonObject.put("matrix_" + i, jsonArray);
        }
        return jsonObject;
    }

    public static VisualOperator fromJSON(JSONObject jsonObject) {
        try {
            String name = jsonObject.getString("name");
            int matrix_dim = jsonObject.getInt("matrix_dim");
            int color = jsonObject.getInt("color");
            int qubit_count = jsonObject.getInt("qubit_count");
            double theta = NULL_ANGLE;
            double phi = NULL_ANGLE;
            double lambda = NULL_ANGLE;
            try {
                JSONObject angles = jsonObject.getJSONObject("angles");
                if (angles.has("omega")) {
                    lambda = angles.getDouble("omega");
                } else {
                    theta = angles.getDouble("theta");
                    phi = angles.getDouble("phi");
                    lambda = angles.getDouble("lambda");
                }
            } catch (Exception e) {
                Log.i("VisualOperator fromJSON", "No angles?");
            }
            JSONArray qubitsJson = jsonObject.getJSONArray("qubits");
            JSONArray symbolsJson = jsonObject.getJSONArray("symbols");
            int[] qubits = new int[qubitsJson.length()];
            String[] symbols = new String[symbolsJson.length()];
            for (int i = 0; i < qubitsJson.length(); i++) {
                qubits[i] = qubitsJson.getInt(i);
            }
            for (int i = 0; i < symbolsJson.length(); i++) {
                symbols[i] = symbolsJson.getString(i);
            }
            Complex[][] matrix = new Complex[matrix_dim][matrix_dim];
            for (int i = 0; i < matrix_dim; i++) {
                JSONArray row = jsonObject.getJSONArray("matrix_" + i);
                for (int j = 0; j < matrix_dim; j++) {
                    matrix[i][j] = Complex.parse(row.getString(j));
                }
            }
            VisualOperator visualOperator = new VisualOperator(matrix_dim, matrix, name, symbols, color);
            visualOperator.qubit_ids = qubits;
            visualOperator.theta = theta;
            visualOperator.phi = phi;
            visualOperator.lambda = lambda;
            return visualOperator;
        } catch (Exception e) {
            Log.e("VisualOperatorLoader", "Error while parsing:");
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Default behaviour, using HTML_MODE_BODY
     *
     * @return Matrix formatted to a table in HTML style
     */
    public String toStringHtmlTable() {
        return toStringHtmlTable(HTML_MODE_BODY);
    }

    public String toStringHtmlTable(int MODE) {
        StringBuilder sb = new StringBuilder();
        if ((MODE & HTML_MODE_BODY) > 0) {
            sb.append("<html>\n" +
                    "<head>\n" +
                    "<style>\n" +
                    "table, th, td {\n" +
                    "  border: 1px solid #BBB;\n" +
                    "  border-collapse: collapse;\n" +
                    "}\n" +
                    "th, td {\n" +
                    "  padding: 6px;\n" +
                    "}\n" +
                    "td {\n" +
                    " text-align: center;\n" +
                    "}\n" +
                    "</style>\n" +
                    "</head>\n" +
                    "<body>");
        }
        sb.append("<table align=\"center\">\n");
        if ((MODE & HTML_MODE_CAPTION) > 0) {
            sb.append("<caption>");
            sb.append(name.replace("π", "&pi;"));
            sb.append("</caption>\n");
        }
        for (int i = 0; i < MATRIX_DIM; i++) {
            sb.append("<tr>\n");
            for (int j = 0; j < MATRIX_DIM; j++) {
                sb.append("<td>");
                String matrixString = matrix[i][j].toString3Decimals();
                if ((MODE & HTML_MODE_FAT) > 0 && matrixString.length() < 3)
                    sb.append("&ensp;").append(matrixString).append("&ensp;");
                else
                    sb.append(matrixString);
                sb.append("</td>\n");
            }
            sb.append("</tr>\n");
        }
        sb.append("</table>\n");
        if ((MODE & HTML_MODE_BODY) > 0) {
            sb.append("</body>\n</html>\n");
        }
        return sb.toString();
    }

    public boolean equals(VisualOperator visualOperator) {
        for (int i = 0; i < MATRIX_DIM; i++)
            for (int j = 0; j < MATRIX_DIM; j++)
                if (!matrix[i][j].equalsExact(visualOperator.matrix[i][j]))
                    return false;

        return true;
    }

    public boolean equals3Decimals(VisualOperator visualOperator) {
        if (MATRIX_DIM != visualOperator.MATRIX_DIM) {
            return false;
        }
        for (int i = 0; i < MATRIX_DIM; i++)
            for (int j = 0; j < MATRIX_DIM; j++)
                if (!matrix[i][j].equals3Decimals(visualOperator.matrix[i][j]))
                    return false;

        return true;
    }

    public static Complex[] toQubitArray(final Qubit[] qs) {
        Complex[] inputMatrix = new Complex[1 << qs.length];
        for (int i = 0; i < (1 << qs.length); i++)
            for (int k = 0; k < qs.length; k++) {
                if (k == 0) {
                    inputMatrix[i] = Complex.multiply(qs[1].matrix[(i >> 1) % 2], qs[0].matrix[i % 2]);
                    k += 1;
                    continue;
                }
                inputMatrix[i] = Complex.multiply(inputMatrix[i], qs[k].matrix[(i >> k) % 2]);
            }

        return inputMatrix;
    }

    private static Complex[][] getQubitTensor(int qubits, VisualOperator v) {
        if (v.getQubitIDs().length != v.getQubits() || v.getQubits() < 1) return null;
        if (v.getQubits() == 1) return getSingleQubitTensor(qubits, v.getQubitIDs()[0], v);
        if (v.getQubits() == qubits) return v.copy().matrix;
        Complex[][] tensor = new Complex[][]{new Complex[]{new Complex(1)}};
        for (int i = 0; i <= qubits; i++) {
            if (i + v.getQubits() == qubits) {
                tensor = tensorProduct(tensor, v.matrix);
                i += v.getQubits();
            } else
                tensor = tensorProduct(tensor, ID.matrix);
        }
        return tensor;
    }

    private static Complex[][] getSingleQubitTensor(int qubits, int which, VisualOperator v) {
        if (v.getQubits() != 1) return null;
        if (qubits < which || qubits < 1 || which < 0) return null;
        if (qubits == 1) return v.copy().matrix;
        Complex[][] temp = new Complex[1][1];
        for (int i = 0; i < qubits; i++) {
            if (i == 0) {
                temp = tensorProduct(which == qubits - 2 ? v.matrix : ID.matrix, which == (qubits - 1) ? v.matrix : ID.matrix);
                i++;
            } else temp = tensorProduct(which == (qubits - i - 1) ? v.matrix : ID.matrix, temp);
        }
        return temp;
    }

    private static Complex[][] tensorProduct(Complex[][] first, Complex[][] second) {
        int firstDim = first[0].length;
        int secondDim = second[0].length;
        final Complex[][] result = new Complex[firstDim * secondDim][];
        for (int m = 0; m < result.length; m++) {
            int col = firstDim * secondDim;
            result[m] = new Complex[col];
        }
        final int THREADS = 4;
        if (firstDim >= THREADS) {
            ArrayList<Thread> threads = new ArrayList<>();
            for (int t = 0; t < THREADS; t++) {
                final int currentThreadId = t;
                Thread th = new Thread(() -> {
                    int start = currentThreadId * firstDim / THREADS;
                    int end = (currentThreadId + 1) * firstDim / THREADS;
                    for (int m = start; m < end; m++)
                        for (int n = 0; n < firstDim; n++)
                            for (int o = 0; o < secondDim; o++)
                                for (int p = 0; p < secondDim; p++)
                                    result[secondDim * m + o][secondDim * n + p] = Complex.multiply(first[m][n], second[o][p]);
                });
                th.start();
                threads.add(th);
            }
            for (Thread thread : threads) {
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Log.e("Quantum VisOp", "Thread join error!");
                }
            }
        } else if (secondDim >= THREADS) {
            ArrayList<Thread> threads = new ArrayList<>();
            for (int t = 0; t < THREADS; t++) {
                final int currentThreadId = t;
                Thread th = new Thread(() -> {
                    int start = currentThreadId * secondDim / THREADS;
                    int end = (currentThreadId + 1) * secondDim / THREADS;
                    for (int m = 0; m < firstDim; m++)
                        for (int n = 0; n < firstDim; n++)
                            for (int o = start; o < end; o++)
                                for (int p = 0; p < secondDim; p++)
                                    result[secondDim * m + o][secondDim * n + p] = Complex.multiply(first[m][n], second[o][p]);
                });
                th.start();
                threads.add(th);
            }
            for (Thread thread : threads) {
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Log.e("Quantum VisOp", "Thread join error!");
                }
            }
        } else {
            for (int m = 0; m < firstDim; m++)
                for (int n = 0; n < firstDim; n++)
                    for (int o = 0; o < secondDim; o++)
                        for (int p = 0; p < secondDim; p++)
                            result[secondDim * m + o][secondDim * n + p] = Complex.multiply(first[m][n], second[o][p]);
        }

        return result;
    }

    private static Complex[][] matrixProduct(Complex[][] first, Complex[][] second) {
        int dim1c = first.length;
        int dim2c = second.length;
        if (dim1c != dim2c || dim1c == 0)
            return null;
        int dim1r = first[0].length;
        int dim2r = second[0].length;
        if (dim1r != dim2r || dim1r != dim2c)
            return null;
        Complex[][] output = new Complex[dim1r][dim1r];
        for (int i = 0; i < dim1r; i++) {
            for (int j = 0; j < dim1r; j++) {
                output[i][j] = new Complex(0);
                for (int m = 0; m < dim1r; m++) {
                    output[i][j].add(Complex.multiply(first[i][m], second[m][j]));
                }
            }
        }
        return output;
    }

    public VisualOperator matrixMultiplication(VisualOperator second) {
        if (this.MATRIX_DIM != second.MATRIX_DIM)
            return this;
        this.matrix = matrixProduct(this.matrix, second.matrix);
        this.name = "MUL";
        return this;
    }

    private static Complex[] operateOn(final Complex[] qubitArray, final Complex[][] gateTensor) {
        Complex[] resultMatrix = new Complex[qubitArray.length];
        final int THREADS = 4;
        if (gateTensor[0].length > THREADS) {
            ArrayList<Thread> threads = new ArrayList<>();
            for (int t = 0; t < THREADS; t++) {
                final int currentThreadId = t;
                Thread th = new Thread(() -> {
                    int start = currentThreadId * gateTensor[0].length / THREADS;
                    int end = (currentThreadId + 1) * gateTensor[0].length / THREADS;
                    for (int i = start; i < end; i++) {
                        resultMatrix[i] = new Complex(0);
                        for (int j = 0; j < gateTensor[0].length; j++)
                            resultMatrix[i].add(Complex.multiply(gateTensor[i][j], qubitArray[j]));
                    }
                });
                th.start();
                threads.add(th);
            }
            for (Thread thread : threads)
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Log.e("Quantum VisOp", "Thread join error!");
                }
        } else
            for (int i = 0; i < gateTensor[0].length; i++) {
                resultMatrix[i] = new Complex(0);
                for (int j = 0; j < gateTensor[0].length; j++)
                    resultMatrix[i].add(Complex.multiply(gateTensor[i][j], qubitArray[j]));
            }
        return resultMatrix;
    }

    public Complex[] operateOn(final Complex[] qubitArray, int qubits) {
        if (NQBITS == 1) {
            return operateOn(qubitArray, getQubitTensor(qubits, this));
        }
        //bug here was caused by getPos
        Complex[] inputMatrix = new Complex[qubitArray.length];
        for (int i = 0; i < qubitArray.length; i++) {
            inputMatrix[getPos(qubits, i)] = qubitArray[i].copy();
        }
        inputMatrix = operateOn(inputMatrix, getQubitTensor(qubits, this));
        for (int i = 0; i < qubitArray.length; i++) {
            qubitArray[i] = inputMatrix[getPos(qubits, i)];
        }
        return qubitArray;
    }

    /**
     * This function places the states in the incoming statevector to the correct places,
     * as the matrix of the operator itself does not change (moves qubits in \p qubit_ids
     * to the beginning of the vector) eg. Hadamard on 3rd qubit will cause 0100 to move to
     * position 0001. 1001 -> 1010, 0101 -> 0011, 1001 -> 1010, 1111 -> 1111
     */
    private int getPos(final int qubits, final int currentPosition) {
        int[] x = new int[qubits];
        int[] changed = new int[qubits];
        Arrays.fill(changed, -1);
        int saved_count = 0;
        for (int i = 0; i < qubits; i++) {
            x[i] = ((currentPosition) >> (qubits - i - 1)) % 2;
        }
        for (int i = 0; i < qubit_ids.length; i++) {
            changed[qubits - i - 1] = x[qubit_ids[qubit_ids.length - i - 1]];
        }
        outer:
        for (int i = 0; i < qubits; i++) {
            int finalI = qubits - i - 1;
            if (Build.VERSION.SDK_INT >= 24 && IntStream.of(qubit_ids).noneMatch(z -> z == finalI))
                changed[qubits - qubit_ids.length - 1 - saved_count++] = x[qubits - i - 1];
            else if (Build.VERSION.SDK_INT < 24) {
                for (int qubit : qubit_ids)
                    if (qubit == finalI)
                        continue outer;
                changed[qubits - qubit_ids.length - 1 - saved_count++] = x[qubits - i - 1];
            }
        }
        int ret = 0;
        for (int i = 0; i < qubits; i++) {
            ret += changed[i] << (qubits - i - 1);
        }
        return ret;
    }

    public int measureFromProbabilities(final float[] probabilities) {
        double subtrahend = 0;
        for (int i = 0; i < probabilities.length; i++) {
            double prob = random.nextDouble();
            if (probabilities[i] > prob * (1 - subtrahend)) {
                return i;
            } else {
                subtrahend += probabilities[i];
                if (i == probabilities.length - 2) {
                    subtrahend = 2;
                }
            }
        }
        return -1;
    }

    public static float[] measureProbabilities(final Complex[] qubitArray) {
        float[] probs = new float[qubitArray.length];
        for (int i = 0; i < qubitArray.length; i++) {
            probs[i] = (float) Math.pow(qubitArray[i].mod(), 2);
            if (probs[i] < Math.pow(10, -20)) probs[i] = 0;
        }
        return probs;
    }

    public Qubit[] operateOn(final Qubit[] qs) {
        if (qs.length != NQBITS) {
            Log.e("VisualOperator", "NO RESULT");
            return null;
        }
        if (NQBITS == 1) {
            Qubit q = qs[0].copy();
            q.matrix[0] = Complex.multiply(matrix[0][0], qs[0].matrix[0]);
            q.matrix[0].add(Complex.multiply(matrix[0][1], qs[0].matrix[1]));
            q.matrix[1] = Complex.multiply(matrix[1][0], qs[0].matrix[0]);
            q.matrix[1].add(Complex.multiply(matrix[1][1], qs[0].matrix[1]));
            return new Qubit[]{q};
        }
        Complex[] inputMatrix = toQubitArray(qs);
        Complex[] resultMatrix = new Complex[MATRIX_DIM];
        for (int i = 0; i < MATRIX_DIM; i++) {
            resultMatrix[i] = new Complex(0);
            for (int j = 0; j < MATRIX_DIM; j++) {
                resultMatrix[i].add(Complex.multiply(matrix[i][j], inputMatrix[j]));
            }
        }
        double[] probs = new double[MATRIX_DIM];
        double subtrahend = 0;
        for (int i = 0; i < MATRIX_DIM; i++) {
            probs[i] = Complex.multiply(Complex.conjugate(resultMatrix[i]), resultMatrix[i]).real;
            double prob = random.nextDouble();
            if (probs[i] > prob * (1 - subtrahend)) {
                Qubit[] result = new Qubit[NQBITS];
                for (int j = 0; j < NQBITS; j++) {
                    result[j] = new Qubit();
                    if ((i >> (NQBITS - j - 1)) % 2 == 1) result[j].prepare(true);
                }
                return result;
            } else {
                subtrahend += probs[i];
                if (i == MATRIX_DIM - 2) {
                    subtrahend = 2;
                }
            }
        }
        Log.e("VisualOperator", "NO RESULT");
        return null;
    }

    public static String[] generateSymbols(int DIM) {
        int NQBITS;
        switch (DIM) {
            case 2:
                NQBITS = 1;
                break;
            case 4:
                NQBITS = 2;
                break;
            case 8:
                NQBITS = 3;
                break;
            case 16:
                NQBITS = 4;
                break;
            case 32:
                NQBITS = 5;
                break;
            case 64:
                NQBITS = 6;
                break;
            default:
                throw new NullPointerException("Invalid dimension");
        }
        String[] sym = new String[NQBITS];
        for (int i = 0; i < NQBITS; i++)
            sym[i] = "C" + i;
        return sym;
    }

    public static LinkedList<String> getPredefinedGateNames() {
        LinkedList<String> list = new LinkedList<>();
        VisualOperator visualOperator = new VisualOperator();
        try {
            Field[] fields = visualOperator.getClass().getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                if (java.lang.reflect.Modifier.isStatic(field.getModifiers())
                        && field.get(visualOperator) instanceof VisualOperator) {
                    list.add(((VisualOperator) field.get(visualOperator)).getName());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return list;
    }

    public static LinkedList<String> getPredefinedGateNames(boolean singleOnly) {
        LinkedList<String> list = new LinkedList<>();
        VisualOperator visualOperator = new VisualOperator();
        try {
            Field[] fields = visualOperator.getClass().getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                if (java.lang.reflect.Modifier.isStatic(field.getModifiers())
                        && field.get(visualOperator) instanceof VisualOperator) {
                    if (!((VisualOperator) field.get(visualOperator)).isMultiQubit() == singleOnly) {
                        list.add(((VisualOperator) field.get(visualOperator)).getName());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return list;
    }

    public static LinkedList<VisualOperator> getPredefinedGates(boolean singleOnly) {
        LinkedList<VisualOperator> list = new LinkedList<>();
        VisualOperator visualOperator = new VisualOperator();
        try {
            Field[] fields = visualOperator.getClass().getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                if (java.lang.reflect.Modifier.isStatic(field.getModifiers())
                        && field.get(visualOperator) instanceof VisualOperator) {
                    VisualOperator visualOperatorField = (VisualOperator) field.get(visualOperator);
                    if (!(visualOperatorField.isMultiQubit() && singleOnly)) {
                        if (visualOperatorField.isMultiQubit()) {
                            list.addLast(visualOperatorField);
                        } else {
                            list.add(0, visualOperatorField);
                        }
                    }
                }
            }
        } catch (Exception e) {
            Log.e("Quantum VisOp", "An exception occurred while listing available gates!");
            e.printStackTrace();
            return null;
        }
        return list;
    }

    public static VisualOperator findGateByName(String name) {
        VisualOperator visualOperator = new VisualOperator();
        try {
            Field[] fields = visualOperator.getClass().getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                if (java.lang.reflect.Modifier.isStatic(field.getModifiers())
                        && field.get(visualOperator) instanceof VisualOperator) {
                    if (((VisualOperator) field.get(visualOperator)).getName().equals(name)) {
                        return ((VisualOperator) field.get(visualOperator));
                    }
                }
            }
        } catch (Exception e) {
            Log.e("Quantum VisOp", "An exception occurred while finding gate: " + name);
            e.printStackTrace();
            return null;
        }
        return null;
    }

    /**
     * Calculates the change in the amplitude of some possible statevectors
     * If they are not 1, the matrix is not *Unitary* (see: https://en.wikipedia.org/wiki/Unitary_matrix#Equivalent_conditions)
     */
    public boolean isUnitary() {
        double lastProbability = Double.NaN;
        for (int column = 0; column <= MATRIX_DIM; column++) {
            Complex[] inputVector = new Complex[MATRIX_DIM];
            Complex[] resultVector = new Complex[MATRIX_DIM];
            for (int i = 0; i < MATRIX_DIM; i++) {
                resultVector[i] = new Complex(0);
                if (column < MATRIX_DIM)
                    if (i == column)
                        inputVector[i] = new Complex(1);
                    else
                        inputVector[i] = new Complex(0);
                else
                    inputVector[i] = new Complex(1 / Math.sqrt(MATRIX_DIM), 0);
            }
            for (int i = 0; i < MATRIX_DIM; i++) {
                for (int j = 0; j < MATRIX_DIM; j++) {
                    resultVector[i].add(Complex.multiply(matrix[i][j], inputVector[j]));
                }
            }
            double prob = 0.0;
            for (int i = 0; i < MATRIX_DIM; i++) {
                prob += Math.pow(resultVector[i].mod(), 2);
            }
            if (!Double.isNaN(lastProbability) && Math.round(prob * 1E4D) / 1E4D != lastProbability) {
                return false;
            } else {
                lastProbability = Math.round(prob * 1E4D) / 1E4D;
            }
        }
        return lastProbability < 1.0001 && lastProbability > 0.9999;
    }

    private Complex[][] generateQFTMatrix(Complex complexOmega, boolean inverse) {
        Complex[][] mat = new Complex[MATRIX_DIM][MATRIX_DIM];
        for (int i = 0; i < MATRIX_DIM; i++) {
            for (int j = 0; j < MATRIX_DIM; j++) {
                if (i == 0 || j == 0) {
                    mat[i][j] = new Complex(1);
                    mat[i][j].multiply(new Complex(1 / Math.sqrt(MATRIX_DIM), 0));
                } else {
                    // Warning! Bit reversion necessary (or use inverse bit order when processing)
                    mat[reverseBits(i, qubit_ids.length)][reverseBits(j, qubit_ids.length)] = Complex.exponent(complexOmega, new Complex((i * j * (inverse ? -1 : 1)) % MATRIX_DIM));
                    mat[reverseBits(i, qubit_ids.length)][reverseBits(j, qubit_ids.length)].multiply(new Complex(1 / Math.sqrt(MATRIX_DIM), 0));
                }
            }
        }
        return mat;
    }

    private int reverseBits(int number, int bits) {
        int reverse = 0;
        for (int i = 0; i < bits; ++i) {
            reverse <<= 1;
            reverse |= (number & 1);
            number >>= 1;
        }
        return reverse;
    }

    public void setColor(int color1) {
        color = color1;
    }

    public int getColor() {
        return color;
    }

    public void addRect(@NonNull RectF rect) {
        if (rectangle == null) rectangle = new LinkedList<>();
        rectangle.add(rect);
    }

    public void resetRect() {
        if (rectangle == null) rectangle = new LinkedList<>();
        rectangle.clear();
    }

    public List<RectF> getRect() {
        if (rectangle == null) rectangle = new LinkedList<>();
        return rectangle;
    }

    public boolean isMultiQubit() {
        return MATRIX_DIM != 2;
    }

    public int getQubits() {
        return NQBITS;
    }

    public boolean setQubitIDs(int[] qubit_ids) {
        if (qubit_ids.length != this.qubit_ids.length) {
            return false;
        } else {
            this.qubit_ids = qubit_ids;
            return true;
        }
    }

    public int[] getQubitIDs() {
        return qubit_ids;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOpenQASMSymbol() {
        String line = "";
        if (equals3Decimals(HADAMARD)) {
            line += "h qubit[" + getQubitIDs()[0] + "];";
        } else if (equals3Decimals(PAULI_X)) {
            line += "x qubit[" + getQubitIDs()[0] + "];";
        } else if (equals3Decimals(PAULI_Y)) {
            line += "y qubit[" + getQubitIDs()[0] + "];";
        } else if (equals3Decimals(PAULI_Z)) {
            line += "z qubit[" + getQubitIDs()[0] + "];";
        } else if (equals3Decimals(ID)) {
            line += "id qubit[" + getQubitIDs()[0] + "];";
        } else if (equals3Decimals(T_GATE)) {
            line += "t qubit[" + getQubitIDs()[0] + "];";
        } else if (equals3Decimals(S_GATE)) {
            line += "s qubit[" + getQubitIDs()[0] + "];";
        } else if (equals3Decimals(CNOT)) {
            line += "cx qubit[" + getQubitIDs()[0] + "],qubit[" + getQubitIDs()[1] + "];";
        } else if (equals3Decimals(CY)) {
            line += "cy qubit[" + getQubitIDs()[0] + "],qubit[" + getQubitIDs()[1] + "];";
        } else if (equals3Decimals(CZ)) {
            line += "cz qubit[" + getQubitIDs()[0] + "],qubit[" + getQubitIDs()[1] + "];";
        } else if (equals3Decimals(CT)) {
            line += "crz(pi/4) qubit[" + getQubitIDs()[0] + "],qubit[" + getQubitIDs()[1] + "];";
        } else if (equals3Decimals(CS)) {
            line += "crz(pi/2) qubit[" + getQubitIDs()[0] + "],qubit[" + getQubitIDs()[1] + "];";
        } else if (equals3Decimals(CH)) {
            line += "ch qubit[" + getQubitIDs()[0] + "],qubit[" + getQubitIDs()[1] + "];";
        } else if (equals3Decimals(SWAP)) {
            line += "swap qubit[" + getQubitIDs()[0] + "],qubit[" + getQubitIDs()[1] + "];";
        } else if (equals3Decimals(TOFFOLI)) {
            line += "ccx qubit[" + getQubitIDs()[0] + "],qubit[" + getQubitIDs()[1] + "],qubit[" + getQubitIDs()[2] + "];";
        } else if (equals3Decimals(FREDKIN)) {
            line += "cswap qubit[" + getQubitIDs()[0] + "],qubit[" + getQubitIDs()[1] + "],qubit[" + getQubitIDs()[2] + "];";
        } else if (equals3Decimals(hermitianConjugate(T_GATE.copy()))) {
            line += "tdg qubit[" + getQubitIDs()[0] + "];";
        } else if (equals3Decimals(hermitianConjugate(S_GATE.copy()))) {
            line += "sdg qubit[" + getQubitIDs()[0] + "];";
        } else if (equals3Decimals(SQRT_NOT)) {
            line += "h qubit[" + getQubitIDs()[0] + "];\n";
            line += "sdg qubit[" + getQubitIDs()[0] + "];\n";
            line += "ry(pi/2) qubit[" + getQubitIDs()[0] + "];";
        } else if (equals3Decimals(hermitianConjugate(SQRT_NOT.copy()))) {
            line += "ry(-pi/2) qubit[" + getQubitIDs()[0] + "];\n";
            line += "s qubit[" + getQubitIDs()[0] + "];\n";
            line += "h qubit[" + getQubitIDs()[0] + "];";
        } else if (isU3()) {
            line += "u3(" + theta + "," + phi + "," + lambda + ") qubit[" + getQubitIDs()[0] + "];";
        } else if (isCU3()) {
            line += "cu3(" + theta + "," + phi + "," + lambda + ") qubit[" + getQubitIDs()[0] + "],qubit[" + getQubitIDs()[1] + "];";
        } else if (!isMultiQubit()) {
            double[] angles = getAngles();
            line += "u3(" + angles[0] + "," + angles[1] + "," + angles[2] + ") qubit[" + getQubitIDs()[0] + "];\n";
            line += "//U3 autoconvert: " + getName();
        } else if (isQFT()) {
            line += "//Begin QFT autoconvert\n";
            DecimalFormat df = new DecimalFormat("#0.000####", new DecimalFormatSymbols(Locale.UK));
            for (int i = qubit_ids.length - 1; i >= 0; i--) {
                line += "h qubit[" + getQubitIDs()[i] + "];\n";
                for (int j = 0; j < i; j++) {
                    line += "cu1(" + df.format(Math.PI / Math.pow(2, i - j)) + ") qubit[" + getQubitIDs()[i] + "],qubit[" + getQubitIDs()[j] + "];\n";
                }
            }
            switch (qubit_ids.length) {
                case 2:
                    line += "swap qubit[" + getQubitIDs()[0] + "],qubit[" + getQubitIDs()[1] + "];\n";
                    break;
                case 3:
                    line += "swap qubit[" + getQubitIDs()[0] + "],qubit[" + getQubitIDs()[2] + "];\n";
                    break;
                case 4:
                    line += "swap qubit[" + getQubitIDs()[0] + "],qubit[" + getQubitIDs()[3] + "];\n";
                    line += "swap qubit[" + getQubitIDs()[1] + "],qubit[" + getQubitIDs()[2] + "];\n";
                    break;
                case 5:
                    line += "swap qubit[" + getQubitIDs()[0] + "],qubit[" + getQubitIDs()[4] + "];\n";
                    line += "swap qubit[" + getQubitIDs()[1] + "],qubit[" + getQubitIDs()[3] + "];\n";
                    break;
                case 6:
                    line += "swap qubit[" + getQubitIDs()[0] + "],qubit[" + getQubitIDs()[5] + "];\n";
                    line += "swap qubit[" + getQubitIDs()[1] + "],qubit[" + getQubitIDs()[4] + "];\n";
                    line += "swap qubit[" + getQubitIDs()[2] + "],qubit[" + getQubitIDs()[3] + "];\n";
                    break;
                default:
                    Log.e("Visual Operator", "Too many qubits for QFT");
            }
            line += "//End QFT autoconvert";
        } else {
            line += "//The following gate cannot be exported into OpenQASM: " + getName();
        }
        return line;
    }
}
