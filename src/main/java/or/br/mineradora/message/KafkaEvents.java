package or.br.mineradora.message;

import jakarta.enterprise.context.ApplicationScoped;
import or.br.mineradora.dto.QuotationDTO;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class KafkaEvents {

    private final Logger LOG = LoggerFactory.getLogger(KafkaEvents.class);

    @Channel("quotation-channel")
    Emitter<QuotationDTO> quotatioRequestEmitter;

    public void sendNewKafkaEvent(QuotationDTO quotation){

        LOG.info("-- Enviando Cotação para Tópico Kafka --");
        quotatioRequestEmitter.send(quotation).toCompletableFuture().join();
    }
}
