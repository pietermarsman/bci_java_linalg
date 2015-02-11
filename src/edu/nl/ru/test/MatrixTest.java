package edu.nl.ru.test;

import edu.nl.ru.linalg.Matrix;
import edu.nl.ru.miscellaneous.Triple;
import edu.nl.ru.miscellaneous.Tuple;
import junit.framework.TestCase;
import org.apache.commons.math3.linear.DefaultRealMatrixPreservingVisitor;
import org.apache.commons.math3.linear.RealVector;

public class MatrixTest extends TestCase {

    Matrix a, b, c, d;


    protected void setUp() throws Exception {
        double[][] dataA = {{1.0, 2.0}, {5.0, 4.0}};
        double[][] dataB = {{0.5, 0.4, 0.2}, {0.3, 0.2, 0.2}, {.2, .2, .7}};
        double[][] dataC = {{1.2, -1232.0}, {-67.5, .232}};
        double[][] dataD = {{1.2, 23., 12., 12.}, {12., 43., 432., 23.}, {3., 23.2, -12., -3.2}, {1., 1., 2., 2.}};
        a = new Matrix(dataA);
        b = new Matrix(dataB);
        c = new Matrix(dataC);
        d = new Matrix(dataD);
    }

    public void testMeanAll() throws Exception {
        Matrix mean = a.mean();
        assertEquals(3.0, mean.getData()[0][0]);
    }

    public void testMean0() throws Exception {
        Matrix mean = a.mean(0);
        assertEquals(3.0, mean.getData()[0][0]);
        assertEquals(3.0, mean.getData()[1][0]);
    }

    public void testMean1() throws Exception {
        Matrix mean = a.mean(1);
        assertEquals(1.5, mean.getData()[0][0]);
        assertEquals(4.5, mean.getData()[1][0]);
    }

    public void testSumAll() throws Exception {
        Matrix sum = a.sum();
        assertEquals(12.0, sum.getData()[0][0]);
    }

    public void testSum0() throws Exception {
        Matrix sum = a.sum(0);
        assertEquals(6.0, sum.getData()[0][0]);
        assertEquals(6.0, sum.getData()[1][0]);
    }

    public void testSum1() throws Exception {
        Matrix sum = a.sum(1);
        assertEquals(3.0, sum.getData()[0][0]);
        assertEquals(9.0, sum.getData()[1][0]);
    }

    public void testCovariance() throws Exception {
        Matrix cov = a.covariance();
        double[][] goodAnswer = {{0.5, -.5}, {-.5, .5}};
        assertEquals(new Matrix(goodAnswer), cov);
    }

    public void testRepeat0() throws Exception {
        Matrix repeat = a.repeat(2, 0);
        double[][] goodAnswer = {{1.0, 2.0}, {1.0, 2.0}, {5.0, 4.0}, {5.0, 4.0}};
        assertEquals(new Matrix(goodAnswer), repeat);
    }

    public void testRepeat1() throws Exception {
        Matrix repeat = a.repeat(2, 1);
        double[][] goodAnswer = {{1.0, 1.0, 2.0, 2.0}, {5.0, 5.0, 4.0, 4.0}};
        assertEquals(new Matrix(goodAnswer), repeat);
    }

    public void testDetrendLinear0() throws Exception {
        Matrix detrend = b.detrend(0, "linear");
        double[][] goodMatrix = {{.016, .033, .082}, {-0.033, -0.066, -0.166}, {0.016, 0.033, 0.083}};
        assertEquals(new Matrix(goodMatrix).round(2), detrend.round((2)));
    }

    public void testDetrendLinear1() throws Exception {
        Matrix detrend = b.detrend(1, "linear");
        double[][] goodMatrix = {{-0.016, 0.033, -0.016}, {0.016, -0.033, 0.016}, {0.0833, -0.166, 0.083}};
        assertEquals(new Matrix(goodMatrix).round(2), detrend.round((2)));
    }

    public void testDetrendConstant0() throws Exception {
        Matrix detrend = b.detrend(0, "constant");
        double[][] goodMatrix = {{0.166, 0.133, -0.166}, {-0.033, -0.066, -0.166}, {-0.133, -0.066, 0.333}};
        assertEquals(new Matrix(goodMatrix).round(2), detrend.round((2)));
    }

    public void testDetrendConstant1() throws Exception {
        Matrix detrend = b.detrend(1, "constant");
        double[][] goodMatrix = {{0.133, 0.033, -0.166}, {0.066, -0.033, -0.033}, {-0.166, -0.166, 0.33}};
        assertEquals(new Matrix(goodMatrix).round(2), detrend.round((2)));
    }

    public void testFft0() throws Exception {
        Matrix power = a.fft(0);
        double[][] goodMatrix = {{36., 36.}, {16., 4.}};
        assertEquals(new Matrix(goodMatrix), power);
    }

    public void testFft1() throws Exception {
        Matrix power = a.fft(1);
        double[][] goodMatrix = {{9., 1.}, {81., 1.}};
        assertEquals(new Matrix(goodMatrix), power);
    }

    public void testIFft0() throws Exception {
        Matrix inversePower = a.ifft(0);
        //        System.out.println(inversePower);
    }

    public void testIFft1() throws Exception {
        Matrix inversePower = a.ifft(1);
        //        System.out.println(inversePower);
    }

    public void testZeros() throws Exception {
        Matrix zeros = Matrix.zeros(10, 10);
        assertEquals(0.0, zeros.sum().getData()[0][0]);
    }

    public void testOnes() throws Exception {
        Matrix ones = Matrix.ones(10, 10);
        assertEquals(100.0, ones.sum().getData()[0][0]);
    }

    public void testEye() throws Exception {
        Matrix eye = Matrix.eye(10);
        assertEquals(10.0, eye.sum().getData()[0][0]);
    }

    public void testCar() throws Exception {
        Matrix car = Matrix.car(2);
        double[][] goodMatrix = {{.5, -.5}, {-.5, .5}};
        assertEquals(new Matrix(goodMatrix), car);
    }

    public void testSpatialFilterCar() throws Exception {
        Matrix filtered = a.spatialFilter("car");
        double[][] goodMatrix = {{-2., -1.}, {2., 1.}};
        assertEquals(new Matrix(goodMatrix), filtered);
    }

    public void testSpatialFilterWhiten() throws Exception {
        Matrix filtered = b.spatialFilter("whiten");
        double[][] goodMatrix = {{5.223, 4.001, 3.912}, {6.484, 5.021, 5.685}, {3.152, 2.548, 4.432}};
        assertEquals(new Matrix(goodMatrix).round(2), filtered.round(2));
    }

    public void testFlipUP() throws Exception {
        Matrix flip = a.flipUD();
        double[][] goodMatrix = {{5., 4.}, {1., 2.}};
        assertEquals(new Matrix(goodMatrix), flip);
    }

    public void testFlipULR() throws Exception {
        Matrix flip = a.flipLR();
        double[][] goodMatrix = {{2., 1.}, {4., 5.}};
        assertEquals(new Matrix(goodMatrix), flip);
    }

    public void testEig() throws Exception {
        // todo slight difference with python
        Tuple<Matrix, RealVector> eig = b.eig();
        double[] goodValues = {.993, .439, -0.032};
        double[][] goodVector = {{0.604, 0.674, 0.59}, {0.402, .252, -0.810}, {0.687, -0.709, 0.061}};
        assertEquals(new Matrix(new Matrix(goodValues)).round(2), new Matrix(eig.y.toArray()).round(2));
        assertEquals(new Matrix(goodVector).round(2), eig.x.round(2));
    }

    public void testSVD() throws Exception {
        // todo different signs than python in U (columns) and V (rows)
        Triple<Matrix, Matrix, Matrix> usv_t = b.svd();
        double[][] goodValuesU = {{0.60584241, -0.65941226, 0.44511845}, {0.40002531, -0.2311365, -0.88687974}, {0.6877025, 0.71536801, 0.1237493}};
        double[][] goodValuesS = {{0.99574753, 0., 0.}, {0., 0.44439383, 0.}, {0., 0., 0.03163813}};
        double[][] goodValuesVT = {{0.56286285, 0.46184651, 0.68548027}, {-0.57600592 , -0.37560963, 0.72604035}, {-0.59279219 , 0.80350184, -0.05460962}};
        assertEquals(new Matrix(goodValuesU).round(2), usv_t.x.round(2));
        assertEquals(new Matrix(goodValuesS).round(2), usv_t.y.round(2));
        assertEquals(new Matrix(goodValuesVT).round(2), usv_t.z.round(2));
    }

    public void testConvolve0() throws Exception {
        Matrix conv = b.convolve(a.getRow(0), 0);
        double[][] goodValues = {{.5, 1.4, 1., .4}, {.3, .8, .6, .4}, {.2, .6, 1.1, 1.4}};
        assertEquals(new Matrix(goodValues).round(2), conv.round(2));
    }

    public void testConvolve1() throws Exception {
        Matrix conv = b.convolve(a.getRow(0), 1);
        double[][] goodValues = {{.5, .4, .2}, {1.3, 1., .6}, {.8, .6, 1.1}, {.4, .4, 1.4}};
        assertEquals(new Matrix(goodValues).round(2), conv.round(2));
    }

    public void testVar() throws Exception {
        Matrix var = b.variance(-1);
        double[] goodValues = {.028};
        assertEquals(new Matrix(goodValues).round(3), var.round(3));
    }

    public void testVar0() throws Exception {
        Matrix var = b.variance(0);
        double[] goodValues = {.0155, .0088, .0555};
        assertEquals(new Matrix(goodValues).round(3), var.round(3));
    }

    public void testVar1() throws Exception {
        Matrix var = b.variance(1);
        double[] goodValues = {.0155, .0022, .0555};
        assertEquals(new Matrix(goodValues).round(3), var.round(3));
    }

    public void testAbs() throws Exception {
        Matrix abs = c.abs();
        abs.walkInOptimizedOrder(new DefaultRealMatrixPreservingVisitor() {
            public void visit(int row, int column, double value) {
                assertTrue(value >= 0);
            }
        });
    }

    public void testOutlierRemoval0() throws Exception {
        Matrix ret = b.removeOutliers(0, -1., 1., 3, "var");
        assertTrue(ret.getRowDimension() == 3);
        assertTrue(ret.getColumnDimension() == 2);
    }

    public void testOutlierRemoval1() throws Exception {
        // todo python implemenation is not right
        // todo using mu is not properly tested. All the rows/columns are deleted.
        Matrix ret = b.removeOutliers(1, -1., 1., 3, "var");
        assertTrue(ret.getRowDimension() == 2);
        assertTrue(ret.getColumnDimension() == 3);
    }

    public void testWelchEven() throws Exception {
        Matrix ret = d.welch(256, "density", "constant");
        // TODO check if it is correct answer
    }

    public void testWelchUneven() throws Exception {
        Matrix ret = b.welch(256, "density", "constant");
        // TODO check if it is correct answer
    }

}