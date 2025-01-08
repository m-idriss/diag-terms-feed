package com.dime.term;

import com.dime.model.TermRecord;
import com.dime.wordsapi.WordsApiService;
import io.quarkus.logging.Log;
import io.smallrye.reactive.messaging.kafka.Record;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@ApplicationScoped
public class TermConsumer {

  @RestClient
  @Inject
  private WordsApiService wordsApiService;

  @Inject
  private TermProducer termProducer;

  /**
   * This method is called whenever a new term is received from the Kafka topic.
   */
  @Transactional
  @Incoming("terms-feed")
  public void receive(Record<String, String> termRecord) {
    if (termRecord == null || termRecord.value() == null) {
      Log.warn("Received null or empty record from Kafka.");
      return;
    }

    try {
      Log.infof("Received term from Kafka: %s", termRecord.value());

      TermApi termApi = wordsApiService.findByWord(termRecord.key());
      if (termApi == null) {
        Log.warn("Error: Term not found in wordsApiService for word: [" + termRecord.key() + "]");
      }
      Log.infof("Received term from Words API: %s", termApi);

      TermRecord termApiRecord = TermApiMapper.INSTANCE.toRecord(termApi);
      termProducer.sendToKafka(termRecord.key(), termApiRecord);
      Log.infof("Sent term to Kafka: %s", termApiRecord);

    } catch (Exception e) {
      Log.error("Unexpected error while processing term record", e);
    }
  }
}
