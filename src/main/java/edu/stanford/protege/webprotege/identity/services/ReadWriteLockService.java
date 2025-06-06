package edu.stanford.protege.webprotege.identity.services;

import java.util.concurrent.Callable;

public interface ReadWriteLockService {
    <T> T executeReadLock(Callable<T> readOperation);

    <T> T executeWriteLock(Callable<T> writeOperation);

    void executeWriteLock(Runnable writeOperation);
}
