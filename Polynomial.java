public class Polynomial {

    public double[] coeff;

    public Polynomial() {
        this.coeff = new double[1];
        this.coeff[0] = 0;
    }

    public Polynomial(double[] coeff) {
        this.coeff = coeff;
    }

    public Polynomial add(Polynomial poly) {
        Polynomial biggerPoly = this;
        Polynomial other = poly;
        if (poly.coeff.length > this.coeff.length) {
            biggerPoly = poly;
            other = this;
        }

        Polynomial newPoly = new Polynomial(biggerPoly.coeff);
        for (int i = 0; i < other.coeff.length; i++) {
            newPoly.coeff[i] += other.coeff[i];
        }
        return newPoly;
    }

    public double evaluate(double x) {
        double sum = 0;
        for (int i = 0; i < this.coeff.length; i++) {
            sum += this.coeff[i] * Math.pow(x, i);
        }
        return sum;
    }

    public boolean hasRoot(double test) {
        return (evaluate(test) == 0);
    }


}

