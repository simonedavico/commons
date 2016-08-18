package cloud.benchflow.minio;

import cloud.benchflow.commons.hashing.Hashing;
import io.minio.ErrorCode;
import io.minio.MinioClient;
import io.minio.ObjectStat;
import io.minio.Result;
import io.minio.errors.*;
import io.minio.messages.Item;
import org.apache.commons.io.IOUtils;
import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Simone D'Avico (simonedavico@gmail.com)
 *
 * Created on 26/02/16.
 */
@SuppressWarnings("unused")
public class BenchFlowMinioClient {

    private MinioClient mc;
    private static final String TESTS_BUCKET = "tests";
    private static final String DEPLOYMENT_DESCRIPTOR_NAME = "docker-compose.yml";
    private static final String TEST_CONFIGURATION_NAME = "benchflow-test.yml";
    private static final String GENERATED_BENCHMARK_FILENAME = "benchflow-benchmark.jar";

    public BenchFlowMinioClient(final String address, final String accessKey, final String privateKey)
            throws InvalidPortException, InvalidEndpointException {
//        System.out.println("Access key: " + accessKey);
//        System.out.println("Secret key: " + privateKey);
//        System.out.println("Address: " + address);
        this.mc = new MinioClient(address, accessKey, privateKey);
    }


    /***
     * Removes object at
     * tests/{id}, if it exists
     */
    protected void removeIfExists(final String id) {
        try {
            mc.removeObject(TESTS_BUCKET, id);
        } catch (ErrorResponseException e) {
            /* happens if the object to remove doesn't exist, do nothing */
        } catch (MinioException | XmlPullParserException | NoSuchAlgorithmException |
                InvalidKeyException | IOException e) {
            throw new BenchFlowMinioClientException(e.getMessage(), e);
        }
    }

    /**
     *
     */
    protected String hashKey(String key) {
        try {
            return Hashing.hashKey(key);
        } catch (UnsupportedEncodingException | NoSuchAlgorithmException e) {
            throw new BenchFlowMinioClientException("Error while hashing key " + key, e);
        }
    }


    /***
     * Retrieves object at key {id} and returns it as a string
     */
    protected String getTextFile(final String id) {
        try {
            InputStream in = mc.getObject(TESTS_BUCKET, id);
            return IOUtils.toString(in, StandardCharsets.UTF_8);
        } catch (MinioException | InvalidKeyException | NoSuchAlgorithmException | XmlPullParserException | IOException e) {
            throw new BenchFlowMinioClientException(e.getMessage(),e);
        }
    }


    /***
     * Saves content at tests/{id}
     */
    protected void saveTextFile(final String content, final String id) {
        byte[] toSave = content.getBytes(StandardCharsets.UTF_8);
        try {
            mc.putObject(TESTS_BUCKET, id, new ByteArrayInputStream(toSave), toSave.length, "application/octet-stream");
        } catch (MinioException | NoSuchAlgorithmException | XmlPullParserException | InvalidKeyException | IOException e) {
            throw new BenchFlowMinioClientException(e.getMessage(), e);
        }
    }


    /***
     * Saves an inputstream at
     * tests/{id}
     */
    protected void saveInputStream(final InputStream content, final String id) {
        try {
            byte[] bytes = IOUtils.toByteArray(content);
            InputStream stream = new ByteArrayInputStream(bytes);
            mc.putObject(TESTS_BUCKET, id, stream, bytes.length, "application/octet-stream");
        } catch (MinioException | InvalidKeyException | NoSuchAlgorithmException |
                XmlPullParserException | IOException e) {
            throw new BenchFlowMinioClientException(e.getMessage(), e);
        }
    }


    /***
     * Returns file at tests/{id}
     */
    protected InputStream getFile(final String id) {
        try {
            return mc.getObject(TESTS_BUCKET, id);
        } catch (MinioException | NoSuchAlgorithmException | XmlPullParserException |
                InvalidKeyException | IOException e) {
            throw new BenchFlowMinioClientException(e.getMessage(), e);
        }
    }


    /***
     * Returns the deployment descriptor sent at deploy time,
     * saved at tests/{testId}/original/docker-compose.yml
     */
    public String getOriginalDeploymentDescriptor(final String testId)  {
        String id = hashKey(testId) + "/original/" + DEPLOYMENT_DESCRIPTOR_NAME;
        return getTextFile(id);
    }


    /***
     * Returns the test configuration sent at deploy time,
     * saved at tests/{testId}/original/benchflow-test.yml
     */
    public String getOriginalTestConfiguration(final String testId) {
        String id = hashKey(testId) + "/original/" + TEST_CONFIGURATION_NAME;
        return getTextFile(id);
    }


    /***
     * Saves the original deployment descriptor for a test,
     * at tests/{testId}/original/docker-compose.yml
     */
    public void saveOriginalDeploymentDescriptor(final String testId, final String deploymentDescriptor) {
        String id = hashKey(testId) + "/original/" + DEPLOYMENT_DESCRIPTOR_NAME;
        saveTextFile(deploymentDescriptor, id);
    }


    /***
     * Saves the deployment descriptor for a given experiment
     * at tests/{testId}/{experimentNumber}/docker-compose.yml
     */
    public void saveDeploymentDescriptorForExperiment(final String testId,
                                                      final long experimentNumber,
                                                      final String deploymentDescriptor) {
        String id = hashKey(testId) + "/" + experimentNumber + "/" +  DEPLOYMENT_DESCRIPTOR_NAME;
        saveTextFile(deploymentDescriptor, id);
    }


    /***
     * Saves the configuration for an experiment
     * at tests/{testId}/{experimentNumber}/benchflow-benchmark.yml
     */
    public void saveTestConfigurationForExperiment(final String testId,
                                                   final long experimentNumber,
                                                   final String experimentConfiguration) {
        String id = hashKey(testId) + "/" + experimentNumber + "/" + TEST_CONFIGURATION_NAME;
        saveTextFile(experimentConfiguration, id);
    }


    /***
     * Saves the original test configuration for a test,
     * at tests/{testId}/original/benchflow-benchmark.yml
     */
    public void saveOriginalTestConfiguration(final String testId, final String testConfiguration) {
        String id = hashKey(testId) + "/original/" + TEST_CONFIGURATION_NAME;
        saveTextFile(testConfiguration, id);
    }


    /***
     * Returns the deployment descriptor for an experiment
     * from tests/{testId}/{experimentNumber}/docker-compose.yml
     */
    public String getDeploymentDescriptorForExperiment(final String testId, final long experimentNumber) {
        String id = hashKey(testId) + "/" + experimentNumber + "/" + DEPLOYMENT_DESCRIPTOR_NAME;
        return getTextFile(id);
    }


    /***
     * Returns the deployment descriptor for a trial,
     * from tests/{testId}/{experimentNumber}/{trialNumber}/docker-compose.yml
     */
    public String getDeploymentDescriptorForTrial(final String testId, final long experimentNumber, final int trialNumber) {
        String id = hashKey(testId) + "/" + experimentNumber + "/" + trialNumber + "/" + DEPLOYMENT_DESCRIPTOR_NAME;
        return getTextFile(id);
    }


    /***
     * Returns the benchmark configuration for an experiment,
     * at tests/{testId}/original/benchflow-test.yml
     */
    public String getTestConfigurationForExperiment(final String testId, final long experimentNumber) {
        String id = hashKey(testId) + "/" + experimentNumber + "/" + TEST_CONFIGURATION_NAME;
        return getTextFile(id);
    }


    /***
     * Saves the deployment descriptor generated for a trial at
     * tests/{testId}/{experimentNumber}/{trialNumber}/docker-compose.yml
     */
    public void saveDeploymentDescriptorForTrial(final String testId, final long experimentNumber,
                                                 final int trialNumber, final String descriptor) {
        String id = hashKey(testId) + "/" + experimentNumber + "/" + trialNumber + "/" + DEPLOYMENT_DESCRIPTOR_NAME;
        saveTextFile(descriptor, id);
    }


    /***
     * Removes the deployment descriptor for a trial,
     * from tests/{testId}/{experimentNumber}/{trialNumber}/docker-compose.yml
     */
    public void removeDeploymentDescriptorForTrial(final String testId, final long experimentNumber,
                                                   final int trialNumber) {
        String id = hashKey(testId) + "/" + experimentNumber + "/" + trialNumber + "/" + DEPLOYMENT_DESCRIPTOR_NAME;
        removeIfExists(id);
    }


    /***
     * Removes the configuration for an experiment,
     * from tests/{testId}/{experimentNumber}/benchflow-test.yml
     */
    public void removeTestConfigurationForExperiment(final String testId, final long experimentNumber) {
        String id = hashKey(testId) + "/" + experimentNumber + "/" + TEST_CONFIGURATION_NAME;
        removeIfExists(id);
    }


    /***
     * Saves the generated Faban configuration for a trial,
     * at tests/{testId}/{experimentNumber}/{trialNumber}/run.xml
     */
    public void saveFabanConfiguration(final String testId, final long experimentNumber,
                                       final int trialNumber, final String configuration) {
        String id = hashKey(testId) + "/" + experimentNumber + "/" + trialNumber + "/run.xml";
        saveTextFile(configuration, id);
    }


    /***
     * Returns the Faban configuration for a trial,
     * from tests/{testId}/{experimentNumber}/{trialNumber}/run.xml
     */
    public String getFabanConfiguration(final String testId, final long experimentNumber, final int trialNumber) {
        String id = hashKey(testId) + "/" + experimentNumber + "/" + trialNumber + "/run.xml";
        return getTextFile(id);
    }


    /***
     * Removes the Faban configuration for a trial,
     * from tests/{testId}/{experimentNumber}/{trialNumber}/run.xml
     */
    public void removeFabanConfiguration(final String testId, final long experimentNumber, final int trialNumber) {
        String id = hashKey(testId) + "/" + experimentNumber + "/" + trialNumber + "/run.xml";
        removeIfExists(id);
    }


    /***
     * Returns the driver generated for an experiment,
     * from tests/{testId}/{experimentNumber}/driver.jar
     */
    public InputStream getGeneratedBenchmark(final String testId, final long experimentNumber) {
        String id = hashKey(testId) + "/" + experimentNumber + "/" + GENERATED_BENCHMARK_FILENAME;
        return getFile(id);
    }


    /***
     * Saves generated driver for an experiment,
     * at tests/{testId}/{experimentNumber}/benchflow-benchmark.jar
     */
    public void saveGeneratedBenchmark(final String testId, final long experimentNUmber, final String driverPath) {
        try {
            String id = hashKey(testId) + "/" + experimentNUmber + "/" + GENERATED_BENCHMARK_FILENAME;
            mc.putObject(TESTS_BUCKET, id, driverPath);
        } catch (MinioException | NoSuchAlgorithmException | InvalidKeyException | IOException | XmlPullParserException e) {
            throw new BenchFlowMinioClientException(e.getMessage(), e);
        }
    }


    /***
     * Saves a model,
     * at tests/{testId}/models/{modelName}
     */
    public void saveModel(final String testId, final String modelName, final InputStream model) throws IOException {
        String modelContent = IOUtils.toString(model, "UTF-8");
        String id = hashKey(testId) + "/models/" + modelName;
        saveTextFile(modelContent, id);
    }


    /***
     * Removes all models at tests/{testId}/models
     */
    public void removeModels(final String testId) {
        try {
            String id = hashKey(testId) + "/models";
            for(Result<Item> item : mc.listObjects(TESTS_BUCKET, id)) {
                mc.removeObject(TESTS_BUCKET, item.get().objectName());
            }
        } catch (MinioException | XmlPullParserException | NoSuchAlgorithmException |
                InvalidKeyException | IOException e) {
            throw new BenchFlowMinioClientException(e.getMessage(), e);
        }
    }


    /***
     * Returns all models at tests/{testId}/models
     */
    public List<String> listModels(final String testId) {
        List<String> modelNames = new LinkedList<>();
        try {
            String id = hashKey(testId) + "/models/";

            for(Result<Item> item : mc.listObjects(TESTS_BUCKET, id)) {
                modelNames.add(item.get().objectName().replace(id, ""));
            }

        } catch (MinioException | XmlPullParserException | NoSuchAlgorithmException |
                InvalidKeyException | IOException e) {
            throw new BenchFlowMinioClientException(e.getMessage(), e);
        }
        return modelNames;
    }


    /***
     * Returns a model for a benchmark,
     * from tests/{testId}/models/{modelName}
     */
    public String getModel(final String testId, final String modelName) {
        String id = hashKey(testId) + "/models/" + modelName;
        return getTextFile(id);
    }

}


