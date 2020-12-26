package domaci.zad1;

import domaci.zad1.services.DatasetReaderService;
import domaci.zad1.servicesImpl.DatasetReaderServiceImpl;
import domaci.zad1.services.LinearRegressionService;
import domaci.zad1.servicesImpl.LinearRegressionServiceImpl;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.boot.autoconfigure.SpringBootApplication;

public class Main {

    public static DatasetReaderService datasetReaderService = new DatasetReaderServiceImpl();
    public static LinearRegressionService linearRegressionService = new LinearRegressionServiceImpl();

    public static void main(String[] args) {
        datasetReaderService.readDatasetFromFile();
        String independentVariableName = datasetReaderService.getIndependentVariableName();
        String dependentVariableName = datasetReaderService.getDependentVariableName();
        List<Float> independentValues = new ArrayList<Float>(datasetReaderService.getDataset().keySet());
        List<Float> dependentValues = new ArrayList<Float>(datasetReaderService.getDataset().values());
        linearRegressionService.calculateLREquotion(independentVariableName, dependentVariableName, independentValues, dependentValues);
    }

}
