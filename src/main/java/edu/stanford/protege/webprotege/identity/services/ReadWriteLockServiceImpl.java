package edu.stanford.protege.webprotege.identity.services;

import edu.stanford.protege.webprotege.identity.config.ReadWriteLockConfig;
import org.springframework.stereotype.Service;

import java.util.concurrent.*;
import java.util.concurrent.locks.*;

@Service
public class ReadWriteLockServiceImpl implements ReadWriteLockService{

    private final ReadWriteLockConfig config;
    private final ReadWriteLock readWriteLock;

    public ReadWriteLockServiceImpl(ReadWriteLockConfig config, ReadWriteLock readWriteLock) {
        this.config = config;
        this.readWriteLock = readWriteLock;
    }

    @Override
    public <T> T executeReadLock(Callable<T> readOperation) {
        return executeWithRetries(() -> {
            if (readWriteLock.readLock().tryLock(config.getTimeout(), config.getTimeUnit())) {
                try {
                    return readOperation.call();
                } finally {
                    readWriteLock.readLock().unlock();
                }
            } else {
                throw new TimeoutException("Failed to acquire read lock");
            }
        }, config.getMaxRetries());
    }

    @Override
    public <T> T executeWriteLock(Callable<T> writeOperation) {
        return executeWithRetries(() -> {
            if (readWriteLock.writeLock().tryLock(config.getTimeout(), config.getTimeUnit())) {
                try {
                    return writeOperation.call();
                } finally {
                    readWriteLock.writeLock().unlock();
                }
            } else {
                throw new TimeoutException("Failed to acquire write lock");
            }
        }, config.getMaxRetries());
    }

    @Override
    public void executeWriteLock(Runnable writeOperation) {
        executeWithRetries(() -> {
            if (readWriteLock.writeLock().tryLock(config.getTimeout(), config.getTimeUnit())) {
                try {
                    writeOperation.run();
                    return null;
                } finally {
                    readWriteLock.writeLock().unlock();
                }
            } else {
                throw new TimeoutException("Failed to acquire write lock");
            }
        }, config.getMaxRetries());
    }

    private <T> T executeWithRetries(Callable<T> operation, int maxRetries) {
        int attempt = 0;
        while (attempt < maxRetries) {
            try {
                return operation.call();
            } catch (Exception e) {
                if (e instanceof TimeoutException) {
                    attempt++;
                    if (attempt == maxRetries) {
                        throw new RuntimeException("Operation failed after " + maxRetries + " attempts", e);
                    }
                } else {
                    throw new RuntimeException("Operation failed due to unexpected error", e);
                }
            }
        }
        throw new RuntimeException("Operation failed after " + maxRetries + " attempts");
    }
}


