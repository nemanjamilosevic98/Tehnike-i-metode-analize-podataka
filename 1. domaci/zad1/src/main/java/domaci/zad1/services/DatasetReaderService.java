package domaci.zad1.services;

import java.util.Map;

public interface DatasetReaderService {

    void readDatasetFromFile();

    String getIndependentVariableName();

    String getDependentVariableName();

    Map<Float, Float> getDataset();
}
