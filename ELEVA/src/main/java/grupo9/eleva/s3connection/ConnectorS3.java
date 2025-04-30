package grupo9.eleva.s3connection;

import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsSessionCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

public class ConnectorS3 {

    private final AwsCredentialsProvider credentialsProvider;

    public ConnectorS3() {
        String accessKey = System.getenv("AWS_ACCESS_KEY_ID");
        String secretKey = System.getenv("AWS_SECRET_ACCESS_KEY");
        String sessionToken = System.getenv("AWS_SESSION_TOKEN");

        if (sessionToken != null && !sessionToken.isEmpty()) {
            AwsSessionCredentials sessionCredentials = AwsSessionCredentials.create(
                    accessKey, secretKey, sessionToken);
            this.credentialsProvider = StaticCredentialsProvider.create(sessionCredentials);
        } else {
            AwsBasicCredentials basicCredentials = AwsBasicCredentials.create(accessKey, secretKey);
            this.credentialsProvider = StaticCredentialsProvider.create(basicCredentials);
        }
    }

    public S3Client getS3Client() {
        return S3Client.builder()
                .region(Region.US_EAST_1) // Altere se necessário
                .credentialsProvider(credentialsProvider)
                .build();
    }
}