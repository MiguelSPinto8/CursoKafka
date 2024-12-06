package or.br.mineradora.message;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import or.br.mineradora.dto.QuotationDTO;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class KafkaEvents {

    private final Logger LOG = LoggerFactory.getLogger(KafkaEvents.class);


    @Channel("quotation-channel")
    Emitter<QuotationDTO> quotationRequestEmitter;

    public void sendNewKafkaEvent(QuotationDTO quotation) {
        LOG.info("Enviando Cotação para o Tópico Kafka...");
        quotationRequestEmitter.send(quotation)
                .toCompletableFuture()  // Convert the future to block and wait for completion
                .join();  // This will block and wait for the completion
        LOG.info("Cotação enviada com sucesso para o Tópico Kafka.");
    }
}

