package domaci.zad1.servicesImpl;

import domaci.zad1.services.DatasetReaderService;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class DatasetReaderServiceImpl implements DatasetReaderService {

    private final String FILE_PATH = "src\\main\\java\\resources\\dataset.csv";
    private String independentVariableName;
    private String dependentVariableName;
    private Map<Float, Float> dataset = new HashMap<Float, Float>();

    public void readDatasetFromFile() {
        BufferedReader reader = null;
        String line = "";
        try {
            reader = new BufferedReader(new FileReader(FILE_PATH));
            boolean firstLine = true;
            while ((line = reader.readLine()) != null) {
                String[] row = line.split(",");
                if (!firstLine) {
                    Float independentValue = Float.parseFloat(row[0]);
                    Float dependentValue = Float.parseFloat(row[1]);
                    dataset.put(independentValue, dependentValue);
                } else {
                    independentVariableName = row[0];
                    dependentVariableName = row[1];
                    firstLine = !firstLine;
                }
            }
        } catch (Exception e) {
            System.out.println("error: " + e);
        } finally {
            try {
                reader.close();
                System.out.println("------------------------------------------------------------------");
                System.out.println(independentVariableName + " | " + dependentVariableName);
                for (Float key : dataset.keySet()) {
                    System.out.println(key + " , " + dataset.get(key));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String getIndependentVariableName() {
        return independentVariableName;
    }

    public String getDependentVariableName() {
        return dependentVariableName;
    }

    public Map<Float, Float> getDataset() {
        return dataset;
    }

}
