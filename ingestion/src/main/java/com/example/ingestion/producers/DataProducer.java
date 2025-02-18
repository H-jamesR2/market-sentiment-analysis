package com.example.ingestion.producers;

public interface DataProducer<T> {
    void sendData(T data);
}
