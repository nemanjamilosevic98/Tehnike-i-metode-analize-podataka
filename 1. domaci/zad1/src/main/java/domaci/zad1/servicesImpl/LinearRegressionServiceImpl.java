package domaci.zad1.servicesImpl;

import domaci.zad1.services.LinearRegressionService;
import java.util.ArrayList;
import java.util.List;

public class LinearRegressionServiceImpl implements LinearRegressionService {

    private Float independentValuesMean;
    private Float dependentValuesMean;
    private Float independentStandardDeviation;
    private Float dependentStandardDeviation;
    private Float covariance;
    private Float corellationCoefficient;
    private Float rootMeanSquaredError;
    private Float independentLREquotionCoeff;
    private Float dependentLREquotionCoeff;

    public Float calculateMean(List<Float> list, boolean isSquareMean) {
        Float sum = 0f;
        for (Float x : list) {
            sum += isSquareMean ? (float) Math.pow((double) x, 2) : x;
        }
        return sum / list.size();
    }

    public Float calculateMean(List<Float> list) {
        return calculateMean(list, false);
    }

    public Float calculateStandardDeviation(List<Float> list) {
        Float mean = calculateMean(list);
        Float sqaureMean = calculateMean(list, true);
        return (float) Math.sqrt((double) sqaureMean - Math.pow((double) mean, 2));
    }

    public Float calculateCovariance(List<Float> independentValues, List<Float> dependentValues) {
        Float independentValMean = calculateMean(independentValues);
        Float dependentValMean = calculateMean(dependentValues);
        List<Float> multiplicationList = new ArrayList<Float>();
        for (int i = 0; i < independentValues.size(); i++) {
            multiplicationList.add(independentValues.get(i) * dependentValues.get(i));
        }
        return calculateMean(multiplicationList) - independentValMean * dependentValMean;
    }

    public Float calculateCorellationCoefficient(List<Float> independentValues, List<Float> dependentValues) {
        Float independentStandardDev = calculateStandardDeviation(independentValues);
        Float dependentStandardDev = calculateStandardDeviation(dependentValues);
        Float covariance = calculateCovariance(independentValues, dependentValues);
        return covariance / (independentStandardDev * dependentStandardDev);
    }

    public Float calcualteIndependentCoefficientOfLREquotion(List<Float> independentValues, List<Float> dependentValues) {
        Float covariance = calculateCovariance(independentValues, dependentValues);
        Float independentStandardDev = calculateStandardDeviation(independentValues);
        return covariance / (float) Math.pow((double) independentStandardDev, 2);
    }

    public Float calcualteDependentCoefficientOfLREquotion(List<Float> independentValues, List<Float> dependentValues) {
        Float independentValMean = calculateMean(independentValues);
        Float dependentValMean = calculateMean(dependentValues);
        Float independentLREquotionCoeff = calcualteIndependentCoefficientOfLREquotion(independentValues, dependentValues);
        return dependentValMean - independentLREquotionCoeff * independentValMean;
    }

    public void calculateLREquotion(String independentVariableName, String dependentVariableName, List<Float> independentValues, List<Float> dependentValues) {
        independentValuesMean = calculateMean(independentValues);
        dependentValuesMean = calculateMean(dependentValues);
        independentStandardDeviation = calculateStandardDeviation(independentValues);
        dependentStandardDeviation = calculateStandardDeviation(dependentValues);
        covariance = calculateCovariance(independentValues, dependentValues);
        corellationCoefficient = calculateCorellationCoefficient(independentValues, dependentValues);
        rootMeanSquaredError = calculateRootMeanSquaredError(independentValues, dependentValues);
        independentLREquotionCoeff = calcualteIndependentCoefficientOfLREquotion(independentValues, dependentValues);
        dependentLREquotionCoeff = calcualteDependentCoefficientOfLREquotion(independentValues, dependentValues);
        System.out.println("------------------------------------------------------------------");
        System.out.println("independentLREquotionCoeff: " + independentLREquotionCoeff);
        System.out.println("dependentLREquotionCoeff: " + dependentLREquotionCoeff);
        System.out.println("------------------------------------------------------------------");
        String dependentCoeffSign = dependentLREquotionCoeff > 0 ? " + " : " ";
        System.out.println("Linear Regression Equotion: " + dependentVariableName + " = " + independentLREquotionCoeff + "  * " + independentVariableName
                + dependentCoeffSign + dependentLREquotionCoeff);
        System.out.println("------------------------------------------------------------------");
        System.out.println("independentValuesMean: " + independentValuesMean);
        System.out.println("dependentValuesMean: " + dependentValuesMean);
        System.out.println("independentStandardDeviation: " + independentStandardDeviation);
        System.out.println("dependentStandardDeviation: " + dependentStandardDeviation);
        System.out.println("covariance: " + covariance);
        System.out.println("corellationCoefficient: " + corellationCoefficient);
        System.out.println("squaredCorellationCoefficient: " + corellationCoefficient * corellationCoefficient);
        System.out.println("rootMeanSquaredError: " + rootMeanSquaredError);
        calculateRootMeanSquaredError(independentValues, dependentValues);
    }

    public Float calculateRootMeanSquaredError(List<Float> independentValues, List<Float> dependentValues) {
        independentLREquotionCoeff = calcualteIndependentCoefficientOfLREquotion(independentValues, dependentValues);
        dependentLREquotionCoeff = calcualteDependentCoefficientOfLREquotion(independentValues, dependentValues);
        List<Float> actualAndpredictionDifferencesList = new ArrayList<Float>();
        for (int i = 0; i < independentValues.size(); i++) {
            Float prediction = independentValues.get(i) * independentLREquotionCoeff + dependentLREquotionCoeff;
            Float difference = dependentValues.get(i) - prediction;
            actualAndpredictionDifferencesList.add((float) Math.pow(difference, 2));
        }
        return (float) Math.sqrt((double) calculateMean(actualAndpredictionDifferencesList));
    }

    public Float getIndependentValuesMean() {
        return independentValuesMean;
    }

    public Float getDependentValuesMean() {
        return dependentValuesMean;
    }

    public Float getIndependentStandardDeviation() {
        return independentStandardDeviation;
    }

    public Float getDependentStandardDeviation() {
        return dependentStandardDeviation;
    }

    public Float getCovariance() {
        return covariance;
    }

    public Float getCorellationCoefficient() {
        return corellationCoefficient;
    }

}
