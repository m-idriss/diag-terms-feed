package com.dime.term;

import com.dime.model.TermRecord;
import com.dime.wordsapi.WordsApiService;
import io.micrometer.common.util.StringUtils;
import io.quarkus.logging.Log;
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
  public void receive(String word) {
    if (StringUtils.isBlank(word)) {
      Log.warn("Received null or empty word from Kafka.");
      return;
    }

    try {
      Log.infof("Received word from Kafka: %s", word);

      TermApi termApi = wordsApiService.findByWord(word);
      if (termApi == null) {
        Log.warn("Error: Term not found in wordsApiService for word: [" + word + "]");
      }
      Log.infof("Received term from Words API: %s", termApi);

      TermRecord termApiRecord = TermApiMapper.INSTANCE.toRecord(termApi);
      termProducer.sendToKafka(word, termApiRecord);
      Log.infof("Sent term to Kafka: %s", termApiRecord);

    } catch (Exception e) {
      Log.error("Unexpected error while processing term record", e);
    }
  }
}
