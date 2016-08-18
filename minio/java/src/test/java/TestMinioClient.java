import cloud.benchflow.minio.BenchFlowMinioClient;
import io.minio.errors.*;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

/**
 * @author Simone D'Avico (simonedavico@gmail.com)
 *
 * Created on 19/08/16.
 */
public class TestMinioClient {

    public static void main(String[] args) throws InvalidPortException, InvalidEndpointException, XmlPullParserException, InsufficientDataException, NoSuchAlgorithmException, IOException, NoResponseException, InvalidKeyException, InternalException, InvalidBucketNameException, ErrorResponseException {

        BenchFlowMinioClient mc = new BenchFlowMinioClient("http://195.176.181.55:9000",
                "CYNQML6R7V12MTT32W6P",
                "SQ96V5pg02Z3kZ/0ViF9YY6GwWzZvoBmElpzEEjn");

        List<String> models = mc.listModels("BenchFlow/wfmsTest/1");
        System.out.println(models.size());
        models.forEach(System.out::println);


    }


}
