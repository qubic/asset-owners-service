package org.qubic.as.sync;

import lombok.extern.slf4j.Slf4j;
import org.qubic.as.sync.job.SyncJobRunner;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class AssetSyncApplication implements ApplicationRunner {

    private final SyncJobRunner jobRunner;

    public AssetSyncApplication(SyncJobRunner jobRunner) {
        this.jobRunner = jobRunner;
    }

    @Override
    public void run(ApplicationArguments args) {
        jobRunner.loop().subscribe(
                x -> log.info("On next received from sync job: {}", x),
                err -> log.error("Finished with error.", err),
                () -> log.info("Sync finished (completed).")
        );
    }

    public static void main(String[] args) {
        SpringApplication.run(AssetSyncApplication.class, args);
    }

}
