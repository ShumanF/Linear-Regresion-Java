package org.example;

import com.github.sh0nk.matplotlib4j.NumpyUtils;
import com.github.sh0nk.matplotlib4j.Plot;
import com.github.sh0nk.matplotlib4j.PythonExecutionException;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.inverse.InvertMatrix;

import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Main {
    public static void main(String[] args) {

        int a_ = 2, b_ = 10, c_ = 50, N = 1000, R = 6;
        Random random = new Random();

        List<Double> x = NumpyUtils.linspace(0, 10, N);
        List<Double> z = NumpyUtils.linspace(0, 10, N);
        List<Double> y = IntStream.range(0, Math.min(z.size(), x.size()))
                .mapToObj(i -> a_ * x.get(i) + b_ * z.get(i) + c_)
                .collect(Collectors.toList());

        y = y.stream().map(n -> n + R * random.nextDouble() - 0.5).collect(Collectors.toList());

        double SX2 = 0, SX = 0, SXY = 0, SY = 0;

        for (int i = 0; i < x.size(); i ++) {
            SX2 += Math.pow(x.get(i),2);
        }
        for (int i = 0; i < x.size(); i ++) {
            SX += x.get(i);
        }
        for (int i = 0; i < y.size(); i ++) {
            SY += y.get(i);
        }
        for (int i = 0; i < Math.min(x.size(), y.size()); i ++) {
            SXY += x.get(i) * y.get(i);
        }

        INDArray matrix = Nd4j.create(new double[][]{
                {SX2,SX},{SX,N}});
        INDArray vector = Nd4j.create(new double[]{SXY,SY});

        INDArray invMatrix = InvertMatrix.invert(matrix,false);
        INDArray ab = invMatrix.mmul(vector);
        double a = ab.getDouble(0), b = ab.getDouble(1);

        List<Double> y_f = IntStream.range(0,x.size())
                .mapToObj(i -> a*x.get(i) + b ).collect(Collectors.toList());


        Plot plt = Plot.create();

        plt.plot().add(x,y,"r");
        plt.plot().add(x, y_f, "o");
        plt.legend().loc("upper right");
        plt.title("scatter");


        try {
            plt.show();

        } catch (PythonExecutionException ep) {
        } catch (IOException e) {
        }

    }
}