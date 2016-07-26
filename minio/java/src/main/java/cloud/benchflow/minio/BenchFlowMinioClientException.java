package cloud.benchflow.minio;

/**
 * @author Simone D'Avico (simonedavico@gmail.com)
 *
 * Created on 26/07/16.
 */
public class BenchFlowMinioClientException extends RuntimeException {

    public BenchFlowMinioClientException(String msg, Throwable e) {
        super(msg, e);
    }

}
