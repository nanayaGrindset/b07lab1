import java.io.*;

public class Polynomial {

    public double[] coeffs;
    public int[] expos;

    public Polynomial(double[] coeffs, int[] expos) {
        this.coeffs = coeffs;
        this.expos = expos;
    }

    public Polynomial(File f) throws IOException {
        // one line, no whitespaces
        BufferedReader input = new BufferedReader(new FileReader(f));
        String line = input.readLine();

        // first, get the amount of terms. we assume the string is a VALID polynomial
        int termCount = 1;
        for (int i = 1; i < line.length(); i++) {
            if ((line.charAt(i) == '+') || (line.charAt(i) == '-')) {
                termCount += 1;
            }
        }
        int termIndex = 0;
        this.expos = new int[termCount];
        this.coeffs = new double[termCount];

        int tempStart = 0;
        for (int i = 0; i < line.length(); i++) {
            // technically i could use nextChar but fuck that

            if (((line.charAt(i) == '+') || (line.charAt(i) == '-') || (i == line.length() - 1)) && (i != tempStart)) {
                int index = i;
                if (i == line.length() - 1) {
                    index += 1;
                }
                String sub = line.substring(tempStart, index);
                if (sub.contains("x")) {
                    String[] termInfo = sub.split("x");
                    this.coeffs[termIndex] = Double.parseDouble(termInfo[0]);
                    this.expos[termIndex] = Integer.parseInt(termInfo[1]);
                } else {
                    this.coeffs[termIndex] = Double.parseDouble(sub);
                    this.expos[termIndex] = 0;
                }
                termIndex += 1;
                tempStart = index;
            }
        }
        input.close();
    }

    private void merge(int[] finalExpos, double[] finalCoeffs, int[] le, double[] lc, int[] re, double[] rc, int left, int right) {
        int i = 0; int j = 0; int k = 0;
        while ((i < left) && (j < right)) {
            if (le[i] <= re[j]) {
                finalExpos[k] = le[i];
                finalCoeffs[k] = lc[i];
                k += 1;
                i += 1;
            } else {
                finalExpos[k] = re[j];
                finalCoeffs[k] = rc[j];
                k += 1;
                j += 1;
            }
        }
        while (i < left) {
            finalExpos[k] = le[i];
            finalCoeffs[k] = lc[i];
            k += 1;
            i += 1;
        }
        while (j < right) {
            finalExpos[k] = re[j];
            finalCoeffs[k] = rc[j];
            k += 1;
            j += 1;
        }
    }

    private void mergeSort(int[] finalExpos, double[] finalCoeffs) {
        int n = finalCoeffs.length;
        if (n < 2) {
            return;
        }
        int mid = n / 2;
        int[] leftExpo = new int[mid];
        int[] rightExpo = new int[n - mid];
        double[] leftCoeffs = new double[mid];
        double[] rightCoeffs = new double[n - mid];

        for (int i = 0; i < mid; i++) {
            leftExpo[i] = finalExpos[i];
            leftCoeffs[i] = finalCoeffs[i];
        }
        for (int i = mid; i < n; i++) {
            rightExpo[i - mid] = finalExpos[i];
            rightCoeffs[i - mid] = finalCoeffs[i];
        }
        mergeSort(leftExpo, leftCoeffs);
        mergeSort(rightExpo, rightCoeffs);
        merge(finalExpos, finalCoeffs, leftExpo, leftCoeffs, rightExpo, rightCoeffs, mid, n - mid);
    }

    public Polynomial add(Polynomial otherPoly) {
        int i = 0; int j = 0;
        mergeSort(this.expos, this.coeffs);
        mergeSort(otherPoly.expos, otherPoly.coeffs);
        int maxSize = this.expos.length + otherPoly.expos.length;
        int tempIndex = 0;
        int tempExpos[] = new int[maxSize];
        double tempCoeff[] = new double[maxSize];

        while ((i < this.expos.length) || (j < otherPoly.expos.length)) {
            // case #1 and #2: either index has reached its end
            if (i >= this.expos.length) {
                tempExpos[tempIndex] = otherPoly.expos[j];
                tempCoeff[tempIndex] = otherPoly.coeffs[j];
                tempIndex += 1;
                j += 1;
            } else if (j >= otherPoly.expos.length) {
                tempExpos[tempIndex] = this.expos[i];
                tempCoeff[tempIndex] = this.coeffs[i];
                tempIndex += 1;
                i += 1;
                // case #3: both exponents equal
            } else if (this.expos[i] == otherPoly.expos[j]) {
                double sum = this.coeffs[i] + otherPoly.coeffs[j];
                if (sum != 0) {
                    tempExpos[tempIndex] = this.expos[i];
                    tempCoeff[tempIndex] = sum;
                    tempIndex += 1;
                }
                i += 1;
                j += 1;
                // case #4 and #5: exponents do not equal
            } else if (this.expos[i] < otherPoly.expos[j]) {
                tempExpos[tempIndex] = this.expos[i];
                tempCoeff[tempIndex] = this.coeffs[i];
                tempIndex += 1;
                i += 1;
            } else if (this.expos[i] > otherPoly.expos[j]) {
                tempExpos[tempIndex] = otherPoly.expos[j];
                tempCoeff[tempIndex] = otherPoly.coeffs[j];
                tempIndex += 1;
                j += 1;
            }
        }
        // condense list
        int finalExpos[] = new int[tempIndex];
        double finalCoeff[] = new double[tempIndex];
        for (int k = 0; k < tempIndex; k++) {
            finalCoeff[k] = tempCoeff[k];
            finalExpos[k] = tempExpos[k];
        }
        return new Polynomial(finalCoeff, finalExpos);
    }

    public double evaluate(double x) {
        double sum = 0;
        for (int i = 0; i < this.coeffs.length; i++) {
            sum += this.coeffs[i] * Math.pow(x, this.expos[i]);
        }
        return sum;
    }


    public boolean hasRoot(double test) {
        return (evaluate(test) == 0);
    }

    public Polynomial multiply(Polynomial poly) {
//        1. build temp lists of all ungrouped terms
//        2. find number of unique expos, and then construct lists of indep. length
//        3. build final lists (ie: remove 0 coeffs, and then sort)

        int[] tempExpos = new int[this.coeffs.length * poly.coeffs.length];
        // if we get a term with expo 0, we must not
        // confuse this with an empty slot which is also expo 0
        for (int i = 0; i < tempExpos.length; i++) {
            tempExpos[i] = -1;
        }
        double[] tempCoeffs = new double[this.coeffs.length * poly.coeffs.length];

        int currentTempCount = 0;
        for (int i = 0; i < this.coeffs.length; i++) {
            for (int j = 0; j < poly.coeffs.length; j++) {
                // does the term's expo already exist?
                // if so, we multiply coeffs and move on
                int ex =  this.expos[i] + poly.expos[j];
                double co = this.coeffs[i] * poly.coeffs[j];

                boolean exists = false;
                for (int t = 0; t < currentTempCount; t++) {
                    if (tempExpos[t] == ex) {
                        tempCoeffs[t] += co;
                        exists = true;
                        break;
                    }
                }
                if (exists) {
                    continue;
                }

                tempExpos[currentTempCount] = ex;
                tempCoeffs[currentTempCount] = co;
                currentTempCount += 1;
            }
        }
        // now search thru coeff list for 0s
        boolean zeroCoeffIndexes[] = new boolean[currentTempCount];
        int zeroCoeffCount = 0;
        for (int i = 0; i < currentTempCount; i++) {
            if (tempCoeffs[i] == 0) {
                zeroCoeffIndexes[i] = true;
                zeroCoeffCount += 1;
            }
        }

        int[] finalExpos = new int[currentTempCount - zeroCoeffCount];
        double[] finalCoeffs = new double[currentTempCount - zeroCoeffCount];
        int tempIndex = 0;
        for (int i = 0; i < currentTempCount; i++) {
            // does the index correspond to a 0 coeff?
            if (zeroCoeffIndexes[i] != true) {
                finalCoeffs[tempIndex] = tempCoeffs[i];
                finalExpos[tempIndex] = tempExpos[i];
                tempIndex += 1;
            }
        }
        mergeSort(finalExpos, finalCoeffs);
        return new Polynomial(finalCoeffs, finalExpos);
    }

    public void printInfo() {
        for (int i = 0; i < this.coeffs.length; i++) {
            if (this.expos[i] != 0) {
                System.out.print(this.coeffs[i] + "x^" + this.expos[i]);
            } else {
                System.out.print(this.coeffs[i]);
            }
            if (i < this.coeffs.length - 1) {
                System.out.print(" + ");
            }
        }
        System.out.print("\n");
        System.out.println("- - -");
    }

    public void saveToFile(String name) throws IOException {
        FileWriter output = new FileWriter(new File(name + ".txt"));
        for (int i = 0; i < this.coeffs.length; i++) {
            if ((i != 0) && (this.coeffs[i] > 0)) {
                output.write("+");
            }
            output.write(Double.toString(this.coeffs[i]));
            if (this.expos[i] != 0) {
                output.write("x" + this.expos[i]);
            }
        }
        output.close();
    }
}

