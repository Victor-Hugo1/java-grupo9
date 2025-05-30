package grupo9.eleva.s3;

import grupo9.eleva.logs.Log;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import java.util.ArrayList;
import java.util.List;

public class ConexaoS3 {
    private static List<Log> logList = new ArrayList<>();

    public static List<Log> getLogList() {
        return logList;
    }

    public void setLogList(List<Log> logList) {
        this.logList = logList;
    }

    public S3Client getS3Client() {
        return S3Client.builder()
                .region(Region.of("us-east-1"))
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
    }

    public void adicionarLog(Log log){
        logList.add(log);
    }

    @Override
    public String toString() {
        return "ConexaoS3{}";
    }
}
