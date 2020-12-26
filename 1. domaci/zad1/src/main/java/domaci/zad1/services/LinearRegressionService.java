package domaci.zad1.services;

import java.util.List;

public interface LinearRegressionService {

    Float calculateMean(List<Float> list);

    Float calculateStandardDeviation(List<Float> list);

    Float calculateCovariance(List<Float> independentValues, List<Float> dependentValues);

    Float calculateCorellationCoefficient(List<Float> independentValues, List<Float> dependentValues);

    Float calculateRootMeanSquaredError(List<Float> independentValues, List<Float> dependentValues);

    Float calcualteIndependentCoefficientOfLREquotion(List<Float> independentValues, List<Float> dependentValues);

    Float calcualteDependentCoefficientOfLREquotion(List<Float> independentValues, List<Float> dependentValues);

    void calculateLREquotion(String independentVariableName, String dependentVariableName, List<Float> independentValues, List<Float> dependentValues);

    Float getIndependentValuesMean();

    Float getDependentValuesMean();

    Float getIndependentStandardDeviation();

    Float getDependentStandardDeviation();

    Float getCovariance();

    Float getCorellationCoefficient();

}
