package com.tech.snapbid.utils;

import java.util.function.Supplier;
import org.springframework.stereotype.Component;
import org.springframework.dao.OptimisticLockingFailureException;
import jakarta.persistence.OptimisticLockException;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class RetryExecutor {

    public <T> T execute(int maxAttempts, Supplier<T> action) {
        int attempt = 0;
        while (true) {
            try {
                return action.get();
            } catch (OptimisticLockException | OptimisticLockingFailureException ex) {
                attempt++;
                if (attempt >= maxAttempts) {
                    log.warn("Optimistic lock failed after {} attempts", attempt);
                    throw ex;
                }
                log.debug("Optimistic lock retry {}/{}", attempt, maxAttempts);
                try { Thread.sleep(10L * attempt); } catch (InterruptedException ignored) { Thread.currentThread().interrupt(); }
            }
        }
    }
}